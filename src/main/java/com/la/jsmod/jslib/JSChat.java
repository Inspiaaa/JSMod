package com.la.jsmod.jslib;

import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8Object;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentString;

public class JSChat {
    public static JSChat instance;
    private static Minecraft mc;

    public JSChat() {
        mc = Minecraft.getMinecraft();
    }

    // TODO: public message: mc.player.sendChatMessage(obj.toString());

    public void msg(Object obj) {
        try {
            mc.ingameGUI.getChatGUI().printChatMessage(new TextComponentString(obj.toString()));
        }
        catch (Exception e) {}

        // TODO: Do an actual Null pointer check instead of try except
    }

    public static V8Object create(V8 runtime) {
        instance = new JSChat();

        V8Object obj = new V8Object(runtime);
        obj.registerJavaMethod(instance, "msg", "msg", new Class[] {Object.class});

        return obj;
    }
}
