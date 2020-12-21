package com.la.jsmod.jslib;

import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8Object;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class JSInput {
    public static JSInput instance;

    public boolean isMouseDown(Integer button) {
        return Mouse.isButtonDown(button);
    }

    public boolean isKeyDown(Integer key) {
        return Keyboard.isKeyDown(key);
    }

    public static V8Object create(V8 runtime) {
        instance = new JSInput();

        V8Object obj = new V8Object(runtime);
        obj.registerJavaMethod(instance, "isMouseDown", "isMouseDown", new Class[] {Integer.class});
        obj.registerJavaMethod(instance, "isKeyDown", "isKeyDown", new Class[] {Integer.class});
        return obj;
    }
}
