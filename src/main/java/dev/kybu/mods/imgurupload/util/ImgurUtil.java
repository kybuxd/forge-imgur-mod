package dev.kybu.mods.imgurupload.util;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import dev.kybu.mods.imgurupload.ImgurUploadMod;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class ImgurUtil {

    public static ListenableFuture<String> uploadScreenshotToImgur(final BufferedImage image) {
        return ImgurUploadMod.ASYNC_EXECUTOR.submit(() -> {
            try {
                final URL url = new URL("https://api.imgur.com/3/image/");
                final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Authorization", "Client-ID " + System.getenv("IMGUR_CLIENTID"));
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connection.connect();

                try(final OutputStreamWriter outputStreamWriter = new OutputStreamWriter(connection.getOutputStream())) {
                    outputStreamWriter.write(URLEncoder.encode("image", "UTF-8") + "=" + bufferedImageToData(image));
                    outputStreamWriter.flush();
                } catch(final Exception exception) {
                    exception.printStackTrace();
                }

                final StringBuilder stringBuilder = new StringBuilder();
                try(final BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    String line;
                    while((line = reader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    return exportLink(stringBuilder.toString());
                } catch(final Exception exception) {
                    exception.printStackTrace();
                }

            } catch (MalformedURLException exception) {
                exception.printStackTrace();
            }
            return "No data";
        });
    }

    public static ListenableFuture<String> imageIdsToAlbum(final Iterable<String> imageIds, final String title) {
        return ImgurUploadMod.ASYNC_EXECUTOR.submit(() -> {
            try {
                final URL url = new URL("https://api.imgur.com/3/album");
                final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(true);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Authorization", "Client-ID " + System.getenv("IMGUR_CLIENTID"));
                connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW");

                OutputStream os = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

                // Add image ids to the request
                for (String imageId : imageIds) {
                    PlayerUtil.sendMessage("Sending imageID " + imageId);
                    writer.write("------WebKitFormBoundary7MA4YWxkTrZu0gW\r\n");
                    writer.write("Content-Disposition: form-data; name=\"ids[]\"\r\n");
                    writer.write("\r\n");
                    writer.write(imageId + "\r\n");
                }

                // Add album title to the request
                writer.write("------WebKitFormBoundary7MA4YWxkTrZu0gW\r\n");
                writer.write("Content-Disposition: form-data; name=\"title\"\r\n");
                writer.write("\r\n");
                writer.write(title + "\r\n");

                // Add album description to the request
                writer.write("------WebKitFormBoundary7MA4YWxkTrZu0gW\r\n");
                writer.write("Content-Disposition: form-data; name=\"description\"\r\n");
                writer.write("\r\n");
                writer.write("junge danke dafür iblali\r\n");

                // End the request
                writer.write("------WebKitFormBoundary7MA4YWxkTrZu0gW--\r\n");
                writer.flush();
                writer.close();

                int responseCode = connection.getResponseCode();
                System.out.println("Response Code: " + responseCode);

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(
                            connection.getInputStream()));
                    String inputLine;
                    StringBuilder response = new StringBuilder();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    return response.toString();
                } else {
                    System.out.println("POST request not worked " + responseCode);
                }

            } catch (MalformedURLException exception) {
                exception.printStackTrace();
            }
            return "No data";
        });
    }

    private static String exportLink(final String jsonResult) {
        final JsonObject jsonElement = new Gson().fromJson(jsonResult, JsonObject.class);
        return jsonElement.getAsJsonObject("data").getAsJsonPrimitive("link").getAsString();
    }

    private static String bufferedImageToData(final BufferedImage image) throws UnsupportedEncodingException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "png", byteArrayOutputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return URLEncoder.encode(Base64.encode(byteArrayOutputStream.toByteArray()), "UTF-8");
    }

}
