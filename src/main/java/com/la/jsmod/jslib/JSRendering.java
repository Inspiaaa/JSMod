package com.la.jsmod.jslib;

import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8Object;
import com.la.jsmod.JSEngine;
import net.minecraft.client.Minecraft;

public class JSRendering {
    public static JSRendering instance;
    private final Minecraft mc;

    public JSRendering() {
        mc = Minecraft.getMinecraft();
    }

    public float getFOV() {
        return mc.gameSettings.fovSetting;
    }

    public void setFOV(Float fov) {
        mc.gameSettings.fovSetting = fov;
    }
    public void setFOV(Integer fov) {
        mc.gameSettings.fovSetting = fov;
    }

    public float getGamma() {
        return mc.gameSettings.gammaSetting;
    }

    public void setGamma(Float gamma) {
        mc.gameSettings.gammaSetting = gamma;
    }
    public void setGamma(Integer gamma) {
        mc.gameSettings.gammaSetting = gamma;
    }

    public static V8Object create(V8 runtime) {
        instance = new JSRendering();

        V8Object obj = new V8Object(runtime);

        obj.registerJavaMethod(instance, "setFOV", "setFOV", new Class[] {Float.class});
        obj.registerJavaMethod(instance, "setFOV", "setFOV", new Class[] {Integer.class});
        obj.registerJavaMethod(instance, "getFOV", "getFOV", new Class[] {});

        obj.registerJavaMethod(instance, "setGamma", "setGamma", new Class[] {Float.class});
        obj.registerJavaMethod(instance, "setGamma", "setGamma", new Class[] {Integer.class});
        obj.registerJavaMethod(instance, "getGamma", "getGamma", new Class[] {});

        JSEngine.instance.releaseAtEnd(obj);
        return obj;
    }
}
