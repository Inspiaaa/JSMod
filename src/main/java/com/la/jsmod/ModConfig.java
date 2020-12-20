package com.la.jsmod;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = JSMod.MOD_ID)
@Config(modid = JSMod.MOD_ID)
public class ModConfig {
    @Config.Name("Root file path")
    public static String filePath = "C:\\Users\\LavaA\\Documents\\Coding\\Java\\Minecraft\\JSMod\\js";

    @SubscribeEvent
    public static void onChange(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (! event.getModID().equals(JSMod.MOD_ID))
            return;

        JSMod.logger.info("Config file changed => Reloading");

        ConfigManager.sync(JSMod.MOD_ID, Config.Type.INSTANCE);

        ScriptLoader.instance.scriptFolder = filePath;
        ScriptLoader.instance.requestHotReload();
    }
}
