package com.la.jsmod.jslib;

import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8Object;
import com.la.jsmod.JSMod;
import net.minecraft.client.Minecraft;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

public class JSConsole {
    public static JSConsole instance;
    private static Minecraft mc;

    public JSConsole() {
        mc = Minecraft.getMinecraft();
    }

    public void log(Object msg) {
        JSMod.logger.info(msg.toString());
    }

    public void error(Object msg) {
        JSMod.logger.error(msg);
    }

    public static V8Object create(V8 runtime) {
        instance = new JSConsole();

        V8Object obj = new V8Object(runtime);
        obj.registerJavaMethod(instance, "log", "log", new Class[] {Object.class});
        obj.registerJavaMethod(instance, "error", "error", new Class[] {Object.class});
        return obj;
    }
}
