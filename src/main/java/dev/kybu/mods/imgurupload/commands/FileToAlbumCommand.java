package dev.kybu.mods.imgurupload.commands;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.kybu.mods.imgurupload.data.ScreenshotMetadata;
import dev.kybu.mods.imgurupload.util.ImgurUtil;
import dev.kybu.mods.imgurupload.util.PlayerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class FileToAlbumCommand extends CommandBase implements ICommand {
    @Override
    public String getName() {
        return "filetoalbum";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/filetoalbum [File]";
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        if(args.length == 1) {
            return getFilesFromFolder();
        }
        return super.getTabCompletions(server, sender, args, targetPos);
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        final EntityPlayer entityPlayer = (EntityPlayer) sender;

        if(args.length == 0) {
            PlayerUtil.sendMessage("§c" + getUsage(sender));
            return;
        }

        final List<String> fileNames = getFilesFromFolder();
        if(args[0].equalsIgnoreCase("list")) {
            PlayerUtil.sendMessage("§7Folgende Aktis sind vorhanden:");
            for (final String fileName : fileNames) {
                PlayerUtil.sendClientMessaage(" §8- " + (fileName.endsWith(".old") ? "§c" : "§a") + fileName, iTextComponent -> {
                    iTextComponent.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/filetoalbum " + fileName));
                    iTextComponent.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString(TextFormatting.GRAY + "Klick um ein Album zu generieren")));
                });
            }
            return;
        }

        final String fileName = String.join(" ", args);
        if(!fileNames.contains(fileName)) {
            PlayerUtil.sendMessage("§cKeine gültige Datei");
            return;
        }

        final File saveFolder = new File(Minecraft.getMinecraft().mcDataDir, "kybuu");
        final File file = new File(saveFolder, fileName);

        final List<String> idsFromFile = getScreenshotsFromFile(file);
        final String albumName = fileName.replaceAll("\\..*", "");
        PlayerUtil.sendMessage("§aSaving as " + albumName);
        PlayerUtil.sendClientMessaage("http://31.214.243.251:187/images/?" + String.join(";" , idsFromFile), iTextComponent -> {
            iTextComponent.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "http://31.214.243.251:187/images/?" + String.join(";" , idsFromFile)));
        });
        final File oldDirectory = new File("kybuu_old");
        oldDirectory.mkdirs();

        file.renameTo(new File(oldDirectory, file.getName() + ".old"));
    }

    private List<String> getScreenshotsFromFile(final File file) {
        final Gson gson = new Gson();
        final List<String> list = new ArrayList<>();
        try(final FileReader reader = new FileReader(file)) {
            final JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
            for (final Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                final String id = entry.getValue().getAsJsonObject().get("imgurUrl").getAsString()
                        .replace("https://i.imgur.com/", "")
                        .replace(".png", "")
                        .replace(".jpg", "");

                list.add(id);

            }
        } catch(final Exception exception) {
            exception.printStackTrace();
        }
        return list;
    }

    private List<String> getFilesFromFolder() {
        final List<String> set = new ArrayList<>();
        final File saveFolder = new File(Minecraft.getMinecraft().mcDataDir, "kybuu");
        for (final File file : Objects.requireNonNull(saveFolder.listFiles())) {
            set.add(file.getName());
        }
        return set;
    }
}
