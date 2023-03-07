package dev.kybu.mods.imgurupload.commands;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.kybu.mods.imgurupload.data.ScreenshotMetadata;
import dev.kybu.mods.imgurupload.util.PlayerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.client.IClientCommand;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FlushLastCommand extends CommandBase implements IClientCommand {

    public static List<ScreenshotMetadata> SCREENSHOT_METADATA = new ArrayList<>();

    @Override
    public boolean allowUsageWithoutPrefix(ICommandSender sender, String message) {
        return false;
    }

    @Override
    public String getName() {
        return "flushlast";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/flushlast Titel";
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        final EntityPlayer player = (EntityPlayer) sender;
        if(SCREENSHOT_METADATA.size() == 0) {
            PlayerUtil.sendClientMessaage(TextFormatting.RED + "Fehler: Es gibt keine gespeicherten Screenshot!");
            return;
        }

        if(args.length == 0) {
            PlayerUtil.sendClientMessaage(TextFormatting.RED + "Syntax: /flushlast Titel (Info: Es werden " + SCREENSHOT_METADATA.size() + " Screenshots gespeichert!)");
            return;
        }
        final String titel = String.join(" ", args);

        final File file = saveMetadataToFileWithTitle(titel);
        PlayerUtil.sendClientMessaage(TextFormatting.GREEN + "Erfolgreich: Die Datei wurde nach " + file.getAbsolutePath() + " gespeichert!", iTextComponent -> {
            iTextComponent.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString(TextFormatting.GRAY + "Klick um die Datei zu öffnen")));
            iTextComponent.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, file.getAbsolutePath()));
        });
        SCREENSHOT_METADATA.clear();
    }

    private File saveMetadataToFileWithTitle(final String title) {
        final File saveFolder = new File(Minecraft.getMinecraft().mcDataDir, "kybuu");
        if(!saveFolder.exists()) saveFolder.mkdir();

        File saveFile = new File(saveFolder, title + ".json");
        int existIndex = 0;
        while(saveFile.exists()) {
            saveFile = new File(saveFolder, title + (existIndex++) + ".json");
        }
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        final JsonObject object = new JsonObject();

        int index = 0;
        for (final ScreenshotMetadata screenshotMetadatum : SCREENSHOT_METADATA) {
            final JsonElement serializedMetadata = gson.toJsonTree(screenshotMetadatum);
            object.add("Screenshot_" + index++, serializedMetadata);
        }

        try(final FileWriter writer = new FileWriter(saveFile)) {
            writer.write(gson.toJson(object));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return saveFile;
    }
}
