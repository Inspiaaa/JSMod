package com.la.jsmod.jslib;

import com.caoccao.javet.annotations.V8Function;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.reference.V8ValueObject;
import com.la.jsmod.JSEngine;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentString;

public class JSChat {
    public static JSChat instance;
    private final Minecraft mc;

    public JSChat() {
        mc = Minecraft.getMinecraft();
    }

    // TODO: public message: mc.player.sendChatMessage(obj.toString());

    @V8Function
    public void msg(V8Value obj) {
        try {
            mc.ingameGUI.getChatGUI().printChatMessage(new TextComponentString(obj.toString()));
        }
        catch (Exception e) {}

        // TODO: Do an actual Null pointer check instead of try except
    }

    public static V8ValueObject create(V8Runtime runtime) {
        instance = new JSChat();
        return JSEngine.instance.createGlobalJsLib(instance);
    }
}
