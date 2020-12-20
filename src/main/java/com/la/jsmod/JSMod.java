package com.la.jsmod;

import com.la.jsmod.commands.HotReloadCommand;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid = JSMod.MOD_ID, version = JSMod.VERSION, name = JSMod.MOD_NAME)
public class JSMod
{
    public static final String MOD_ID = "jsmod";
    public static final String MOD_NAME = "JSMod - JS Superpowers to MC";
    public static final String VERSION = "0.0.1";

    public static Logger logger;

    public ScriptLoader scriptLoader;

    @Mod.Instance(MOD_ID)
    public static JSMod instance;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
    }
    
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        scriptLoader = new ScriptLoader();
        scriptLoader.requestColdStart();
    }

    @EventHandler
    public static void init(FMLServerStartingEvent event)
    {
        event.registerServerCommand(new HotReloadCommand());
    }

    @Override
    protected void finalize() {
        scriptLoader.releaseRuntime();
    }
}
