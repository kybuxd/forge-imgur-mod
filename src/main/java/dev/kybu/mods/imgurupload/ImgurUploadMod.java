package dev.kybu.mods.imgurupload;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import dev.kybu.mods.imgurupload.commands.FileToAlbumCommand;
import dev.kybu.mods.imgurupload.commands.FlushLastCommand;
import dev.kybu.mods.imgurupload.commands.SaveLastCommand;
import dev.kybu.mods.imgurupload.commands.ScreenCacheCommand;
import net.minecraft.init.Blocks;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SideOnly(Side.CLIENT)
@Mod(modid = ImgurUploadMod.MODID, name = ImgurUploadMod.NAME, version = ImgurUploadMod.VERSION)
public class ImgurUploadMod
{
    public static final String MODID = "imgurupload";
    public static final String NAME = "Kybu's Imgur Upload";
    public static final String VERSION = "1.0.0-BETA";
    public static final ListeningExecutorService ASYNC_EXECUTOR = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()));

    private static Logger logger;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        // some example code
        logger.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());
    }



    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        logger.info("Post Init deine Mutter");
        ClientCommandHandler.instance.registerCommand(new SaveLastCommand());
        ClientCommandHandler.instance.registerCommand(new FlushLastCommand());
        ClientCommandHandler.instance.registerCommand(new ScreenCacheCommand());
        ClientCommandHandler.instance.registerCommand(new FileToAlbumCommand());
    }
}
