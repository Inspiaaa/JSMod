package com.la.jsmod.jslib;

import com.caoccao.javet.annotations.V8Function;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.values.reference.V8ValueObject;
import com.la.jsmod.JSEngine;
import com.la.jsmod.JSMod;

public class JSConsole {
    public static JSConsole instance;

    @V8Function
    public void log(Object msg) {
        JSMod.logger.info(msg.toString());
    }

    @V8Function
    public void error(Object msg) {
        JSMod.logger.error(msg.toString());
    }

    public static V8ValueObject create(V8Runtime runtime) {
        instance = new JSConsole();
        return JSEngine.instance.createGlobalJsLib(instance);
    }
}
