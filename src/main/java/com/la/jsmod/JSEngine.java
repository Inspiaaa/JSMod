package com.la.jsmod;

import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8Array;
import com.eclipsesource.v8.V8ScriptCompilationException;
import com.eclipsesource.v8.V8ScriptExecutionException;
import com.la.jsmod.jslib.*;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.input.Keyboard;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.Key;
import java.util.logging.Logger;

public class JSEngine {
    // TODO: Create command to activate / deactivate this from the game
    public static boolean printErrors = true;

    private final String DEFAULT_SCRIPT_NAME = "__main__";
    public V8 runtime;

    public JSEngine() {
        MinecraftForge.EVENT_BUS.register(this);
    }


    public void loadFile(String path) {
        loadFile(path, DEFAULT_SCRIPT_NAME);
    }

    public void loadFile(String path, String name) {
        JSMod.logger.info("Loading " + path + " into V8 Runtime");
        try {
            File file = new File(path);
            FileInputStream fis = new FileInputStream(file);

            byte[] data = new byte[(int) file.length()];
            fis.read(data);
            fis.close();

            String code = new String(data, "UTF-8");
            safeLoad(code, name);
        }
        catch (IOException e) {
            JSMod.logger.error("Unable to load file " + path);
        }
    }


    // TODO: Use runtime.executeVoidScript(code, scriptName, 0); for better js error messages
    public void safeLoad(String code) {
        safeLoad(code, true, DEFAULT_SCRIPT_NAME);
    }

    public void safeLoad(String code, String name) {
        safeLoad(code, true, name);
    }

    public void safeLoad(String code, boolean showError, String name) {
        try {
            runtime.executeVoidScript(code, name, 0);
        }
        catch (V8ScriptCompilationException e) {
            if (showError) {
                JSMod.logger.error(e.getJSStackTrace());
            }
        }
        catch (V8ScriptExecutionException e) {
            if (showError) {
                JSMod.logger.error(e.getJSStackTrace());
            }
        }
    }


    public void safeCallVoid(String name) {
        safeCallVoid(name, null);
    }

    public void safeCallVoid(String name, V8Array parameters) {
        try {
            runtime.executeVoidFunction(name, parameters);
        }
        catch (V8ScriptExecutionException e) {
            if (printErrors) {
                JSMod.logger.error(e.getJSStackTrace());
            }
        }
    }

    public Object safeCall(String name) {
        return safeCall(name, null);
    }

    public Object safeCall(String name, V8Array parameters) {
        try {
            return runtime.executeFunction(name, parameters);
        }
        catch (V8ScriptExecutionException e) {
            if (printErrors) {
                JSMod.logger.error(e.getJSStackTrace());
            }
        }

        return null;
    }


    public void createRuntime() {
        JSMod.logger.info("Creating V8 Runtime");
        runtime = V8.createV8Runtime();

        // TODO: Keep track of these objects and release them individually
        runtime.add("Console", JSConsole.create(runtime));
        runtime.add("Chat", JSChat.create(runtime));
        runtime.add("Input", JSInput.create(runtime));
        runtime.add("Player", JSPlayer.create(runtime));
        runtime.add("KeyBind", JSAllKeybindings.create(runtime));
    }

    public void releaseRuntime() {
        JSMod.logger.info("Releasing V8 Runtime");
        runtime.release(false);
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        if (event.side != Side.CLIENT)
            return;

        try {
            safeCallVoid("onTick");
        }
        catch (Throwable e) {
            JSMod.logger.error(e.getStackTrace());
        }
    }

    @SubscribeEvent
    public void onKeyPressed(InputEvent.KeyInputEvent event) {
        int keyCode = Keyboard.getEventKey();

        try {
            V8Array params = new V8Array(runtime);

            params.push(keyCode);

            if (Keyboard.isKeyDown(keyCode)) {
                safeCallVoid("onKeyDown", params);
            }
            else {
                safeCallVoid("onKeyUp", params);
            }

            params.release();
        }
        catch (Throwable e) {
            JSMod.logger.error(e.getStackTrace());
        }
    }
}
