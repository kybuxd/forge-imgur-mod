package dev.kybu.mods.imgurupload.listener;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.MoreExecutors;
import dev.kybu.mods.imgurupload.commands.FlushLastCommand;
import dev.kybu.mods.imgurupload.commands.SaveLastCommand;
import dev.kybu.mods.imgurupload.data.Location;
import dev.kybu.mods.imgurupload.data.ScreenshotMetadata;
import dev.kybu.mods.imgurupload.util.ImgurUtil;
import dev.kybu.mods.imgurupload.util.PlayerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.client.event.ScreenshotEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber(Side.CLIENT)
public class ScreenshotListener {

    @SubscribeEvent
    public static void onScreenshot(final ScreenshotEvent event) {
        PlayerUtil.sendClientMessaage("Uploading Image to Imgur...");
        Futures.addCallback(ImgurUtil.uploadScreenshotToImgur(event.getImage()), new FutureCallback<String>() {
            @Override
            public void onSuccess(@Nullable String result) {
                if(result == null) {
                    PlayerUtil.sendClientMessaage("Ein Fehler bei der Verarbeitung ist aufgetreten!", iTextComponent -> {
                        iTextComponent.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString("Klicke, um die Datei zu öffnen")));
                        iTextComponent.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, event.getScreenshotFile().getAbsolutePath()));
                    });
                    return;
                }

                PlayerUtil.sendClientMessaage("Result: " + result, iTextComponent -> {
                    iTextComponent.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString("Klicke, um die URL zu kopieren")));
                    iTextComponent.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, result));
                });
                PlayerUtil.sendClientMessaage("Du kannst jetzt mit /savelast Titel diesen Screenshot abspeichern!", iTextComponent -> {
                    iTextComponent.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/savelast "));
                });

                final EntityPlayerSP entityPlayerSP = PlayerUtil.getPlayer();

                SaveLastCommand.LAST_SCREENSHOT_METADATA = new ScreenshotMetadata(
                        result,
                        event.getScreenshotFile().getAbsolutePath(),
                        Location.fromPlayer(entityPlayerSP),
                        entityPlayerSP.getHealth(),
                        entityPlayerSP.getName(),
                        entityPlayerSP.getEntityWorld().getProviderName(),
                        entityPlayerSP.experienceLevel,
                        System.currentTimeMillis(),
                        Minecraft.getMinecraft().getCurrentServerData() == null ? "LOKAL" : Minecraft.getMinecraft().getCurrentServerData().serverIP
                );
                FlushLastCommand.SCREENSHOT_METADATA.add(SaveLastCommand.LAST_SCREENSHOT_METADATA);
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        }, MoreExecutors.directExecutor());
    }

}
