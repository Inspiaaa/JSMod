package com.la.jsmod;

import com.caoccao.javet.annotations.V8Function;
import com.caoccao.javet.exceptions.*;
import com.caoccao.javet.interop.V8Host;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.values.reference.V8ValueObject;
import com.la.jsmod.jslib.*;
import com.la.jsmod.jslib.world.JSBlocks;
import com.la.jsmod.jslib.world.JSWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.input.Keyboard;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedList;

public class JSEngine {
    public static JSEngine instance;

    public V8Runtime runtime;

    // TODO: Create command to activate / deactivate this from the game
    public static boolean printErrors = true;

    private final String DEFAULT_SCRIPT_NAME = "__main__";

    // private final LinkedList<V8ValueObject> objectsToReleaseAtEnd = new LinkedList<V8ValueObject>();
    private final LinkedList<String> objectsToReleaseAtEnd = new LinkedList<String>();

    public JSEngine() {
        MinecraftForge.EVENT_BUS.register(this);
        instance = this;
    }

    public String formatJsError(JavetScriptingError e) {
        // E.g.
        /*
        TypeError: Cannot read property 'a' of null
        2| null.a
           ^^^^^^
         */

        StringBuilder builder = new StringBuilder();

        builder.append(e.getMessage());
        builder.append("\n");
        builder.append(e.getResourceName());
        builder.append("\n");

        String lineStart = e.getLineNumber() + "| ";
        builder.append(lineStart);
        builder.append(e.getSourceLine());
        builder.append("\n");

        for (int i = 0; i < lineStart.length() + e.getStartColumn()-1; i++) {
            builder.append(" ");
        }
        for (int i = 0; i < e.getEndColumn() - e.getStartColumn() + 1; i++) {
            builder.append("^");
        }

        return builder.toString();
    }

    private void handleJavetError(JavetException e) {
        if (e instanceof BaseJavetScriptingException) {
            JSMod.logger.error(formatJsError(((BaseJavetScriptingException) e).getScriptingError()));
            return;
        }

        e.printStackTrace();
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


    public void safeLoad(String code) {
        safeLoad(code, true, DEFAULT_SCRIPT_NAME);
    }

    public void safeLoad(String code, String name) {
        safeLoad(code, true, name);
    }

    public void safeLoad(String code, boolean showError, String name) {
        try {
            // runtime.execute(code, new V8ScriptOrigin(name), true);
            runtime.getExecutor(code).setResourceName(name).executeVoid();
        }
        catch (JavetCompilationException e) {
            if (showError) {
                JSMod.logger.error("Compilation error loading " + name);
                JSMod.logger.error(formatJsError(e.getScriptingError()));
            }
        }
        catch (JavetExecutionException e) {
            if (showError) {
                JSMod.logger.error(formatJsError(e.getScriptingError()));
            }
        }
        catch (JavetException e) {
            e.printStackTrace();
        }
    }


    public void safeCallVoid(String name, Object... parameters) {
        try {
            runtime.getGlobalObject().invokeVoid(name, parameters);
            // runtime.ca
        }
        catch (JavetException e) {
            if (printErrors) {
                handleJavetError(e);
            }
        }
    }

    public Object safeCall(String name, Object... parameters) {
        try {
            return runtime.getGlobalObject().invoke(name, parameters);
        }
        catch (JavetException e) {
            if (printErrors) {
                handleJavetError(e);
            }
        }

        return null;
    }


    public V8ValueObject createGlobalJsLib(Object interceptor) {
        V8ValueObject obj = null;

        try {
            obj = runtime.createV8ValueObject();
            obj.bind(interceptor);
            obj.setWeak();
        }
        catch (JavetException e) {
            e.printStackTrace();
        }

        return obj;
    }

    private void loadGlobalLibrary(String name, V8ValueObject obj) {
        try {
            runtime.getGlobalObject().set(name, obj);
        }
        catch (JavetException e) {
            e.printStackTrace();
        }

        objectsToReleaseAtEnd.add(name);
    }

    public static class InterceptorTest {
        @V8Function
        public int getX() {
            return 1;
        }
    }

    public void createRuntime() {
        JSMod.logger.info("Creating V8 Runtime");

        try {
            runtime = V8Host.getV8Instance().createV8Runtime();

        } catch (JavetException e) {
            e.printStackTrace();
        }

        JSMod.logger.info("Loading builtin libraries");
        // TODO: Maybe make Console lower case so that it is more standard
        loadGlobalLibrary("Console", JSConsole.create(runtime));
        loadGlobalLibrary("Chat", JSChat.create(runtime));
        loadGlobalLibrary("Input", JSInput.create(runtime));
        loadGlobalLibrary("Player", JSPlayer.create(runtime));

        try {
            loadGlobalLibrary("KeyBind", JSAllKeybindings.create(runtime));
        } catch (JavetException e) {
            e.printStackTrace();
        }

        loadGlobalLibrary("Rendering", JSRendering.create(runtime));

        try {
            loadGlobalLibrary("Blocks", JSBlocks.create(runtime));
        } catch (JavetException e) {
            e.printStackTrace();
        }

        loadGlobalLibrary("World", JSWorld.create(runtime));
    }

    public void releaseRuntime() {
        JSMod.logger.info("Releasing " + objectsToReleaseAtEnd.size() + " global namespaces");

        for (String name : objectsToReleaseAtEnd) {
            try {
                runtime.getGlobalObject().delete(name);
            } catch (JavetException e) {
                e.printStackTrace();
            }
        }

        objectsToReleaseAtEnd.clear();

        JSMod.logger.info("Releasing V8 Runtime");
        try {
            // Call the GC to make sure that any objects that have actually been removed
            // do not cause a supposed memory leak warning
            runtime.lowMemoryNotification();
            runtime.close();
        }
        catch (JavetException e) {
            e.printStackTrace();
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        if (event.side != Side.CLIENT)
            return;

        if (event.phase != TickEvent.Phase.END)
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
            if (Keyboard.isKeyDown(keyCode)) {
                safeCallVoid("onKeyDown", keyCode);
            }
            else {
                safeCallVoid("onKeyUp", keyCode);
            }
        }
        catch (Throwable e) {
            JSMod.logger.error(e.getStackTrace());
        }
    }
}
