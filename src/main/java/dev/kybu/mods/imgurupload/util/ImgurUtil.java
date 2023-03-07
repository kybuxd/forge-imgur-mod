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
