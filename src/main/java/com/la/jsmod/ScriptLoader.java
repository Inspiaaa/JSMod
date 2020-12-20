package com.la.jsmod;

import com.eclipsesource.v8.V8Array;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.io.File;

public class ScriptLoader {
    public static ScriptLoader instance;
    public String scriptFolder = "C:\\Users\\LavaA\\Documents\\Coding\\Java\\Minecraft\\JSMod\\js";
    public JSEngine engine;

    // TODO: Add another method for initialising & loading (not quite hotReload())

    private boolean isReloadRequested = false;
    private boolean isColdStartRequested = true;

    public ScriptLoader() {
        engine = new JSEngine();
        instance = this;

        MinecraftForge.EVENT_BUS.register(this);
    }

    public void requestHotReload() {
        isReloadRequested = true;
    }

    private static String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1);
    }

    private boolean isJSFile(File file) {
        return getFileExtension(file.getName()).toLowerCase().equals("js");
    }

    private void loadFiles(String folderPath) {
        File root = new File(folderPath);
        walkAndLoadFiles(root, "");
    }

    private void walkAndLoadFiles(File root, String location) {
        File[] list = root.listFiles();

        if (list == null) return;

        for (File f : list) {
            if (f.isDirectory()) {
                walkAndLoadFiles(f, location + "/" + f.getName());
            }
            else if (isJSFile(f)){
                engine.loadFile(f.getAbsolutePath(), location + "/" + f.getName());
            }
        }
    }

    public void requestColdStart() {
        isReloadRequested = true;
        isColdStartRequested = true;
    }

    public void coldStart() {
        engine.createRuntime();
        loadFiles(scriptFolder);
        engine.safeCallVoid("onInit");
    }

    public void hotReload() {
        JSMod.logger.info("Performing hot reload...");
        JSMod.logger.info("Storing previous state");

        Object previousState = engine.safeCall("saveState");
        String stateStr = "";

        if (previousState instanceof String) {
            stateStr = (String) previousState;
        }

        engine.releaseRuntime();
        engine.createRuntime();
        loadFiles(scriptFolder);
        engine.safeCallVoid("onInit");

        JSMod.logger.info("Reloading previous state");
        if (! stateStr.equals("")) {
            V8Array params = new V8Array(engine.runtime);
            params.push(stateStr);
            engine.safeCallVoid("loadState", params);
            params.release();
        }
    }

    public void releaseRuntime() {
        engine.releaseRuntime();
    }

    @Override
    protected void finalize() {
        releaseRuntime();
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        if (event.side != Side.CLIENT) return;

        if (isReloadRequested) {
            if (isColdStartRequested) {
                coldStart();
            }
            else {
                hotReload();
            }
            isReloadRequested = false;
        }
    }
}
