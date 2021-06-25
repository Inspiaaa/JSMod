package com.la.jsmod.jslib;

import com.caoccao.javet.annotations.V8Function;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.values.reference.V8ValueObject;
import com.la.jsmod.JSEngine;
import net.minecraft.client.Minecraft;

public class JSRendering {
    public static JSRendering instance;
    private final Minecraft mc;

    public JSRendering() {
        mc = Minecraft.getMinecraft();
    }

    @V8Function
    public float getFOV() {
        return mc.gameSettings.fovSetting;
    }
    @V8Function
    public void setFOV(float fov) {
        mc.gameSettings.fovSetting = fov;
    }

    @V8Function
    public float getGamma() {
        return mc.gameSettings.gammaSetting;
    }
    @V8Function
    public void setGamma(float gamma) {
        mc.gameSettings.gammaSetting = gamma;
    }

    public static V8ValueObject create(V8Runtime runtime) {
        instance = new JSRendering();
        return JSEngine.instance.createGlobalJsLib(instance);
    }
}
