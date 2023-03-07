package dev.kybu.mods.imgurupload.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import java.util.function.Consumer;

public class PlayerUtil {

    public static void sendClientMessaage(final String message, final Consumer<ITextComponent> modifyComponent) {
        final TextFormatting grayColor = colorCodeToTextFormatting("7"), yellowColor = colorCodeToTextFormatting("e");
        ITextComponent textComponent = new TextComponentString(
                grayColor + "[" + yellowColor + "Imgur" + grayColor + "] " + message
        );
        if(modifyComponent != null) {
            modifyComponent.accept(textComponent);
        }

        Minecraft.getMinecraft().player.sendMessage(textComponent);
    }

    public static void sendClientMessaage(final String message) {
        sendClientMessaage(message, null);
    }

    public static EntityPlayerSP getPlayer() {
        return Minecraft.getMinecraft().player;
    }

    public static TextFormatting colorCodeToTextFormatting(String colorCode) {
        if(colorCode.length() == 1) {
            colorCode = "\u00a7" + colorCode;
        }

        for (final TextFormatting value : TextFormatting.values()) {
            if(value.toString().equalsIgnoreCase(colorCode)) {
                return value;
            }
        }
        return null;
    }

}
