package com.la.jsmod.jslib;

import com.caoccao.javet.annotations.V8Function;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.values.reference.V8ValueObject;
import com.la.jsmod.JSEngine;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class JSInput {
    public static JSInput instance;

    @V8Function
    public boolean isMouseDown(int button) {
        return Mouse.isButtonDown(button);
    }

    @V8Function
    public boolean isKeyDown(int key) {
        return Keyboard.isKeyDown(key);
    }

    public static V8ValueObject create(V8Runtime runtime) {
        instance = new JSInput();
        return JSEngine.instance.createGlobalJsLib(instance);
    }
}
