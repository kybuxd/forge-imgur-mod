package dev.kybu.mods.imgurupload.commands;

import dev.kybu.mods.imgurupload.data.ScreenshotMetadata;
import dev.kybu.mods.imgurupload.util.PlayerUtil;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.client.IClientCommand;

import java.text.SimpleDateFormat;
import java.util.logging.SimpleFormatter;

public class ScreenCacheCommand extends CommandBase implements IClientCommand {
    @Override
    public boolean allowUsageWithoutPrefix(ICommandSender sender, String message) {
        return false;
    }

    @Override
    public String getName() {
        return "screencache";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "screencache [clear]";
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        final EntityPlayerSP entityPlayerSP = (EntityPlayerSP) sender;
        if(args.length == 0) {
            PlayerUtil.sendClientMessaage("Es sind derzeit " + FlushLastCommand.SCREENSHOT_METADATA.size() + " Screenshots im Cache!");
            for (final ScreenshotMetadata screenshotMetadatum : FlushLastCommand.SCREENSHOT_METADATA) {
                PlayerUtil.sendClientMessaage(" > " + screenshotMetadatum.getImgurUrl() + " auf " + screenshotMetadatum.getServerIp() + " um " + new SimpleDateFormat("dd.MM.yyyy HH:mm").format(screenshotMetadatum.getTimestamp()));
            }
            return;
        }

        if(args[0].equalsIgnoreCase("clear")) {
            FlushLastCommand.SCREENSHOT_METADATA.clear();
            PlayerUtil.sendClientMessaage("Screenshot Cache wurde geleert.");
        }
    }
}
