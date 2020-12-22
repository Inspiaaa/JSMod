package com.la.jsmod.jslib;

import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8Array;
import com.eclipsesource.v8.V8Object;
import com.eclipsesource.v8.V8Value;
import com.la.jsmod.JSEngine;
import com.la.jsmod.JSMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

public class JSAllKeybindings {
    static class JSKeyBind {
        KeyBinding binding;

        public JSKeyBind(KeyBinding binding) {
            this.binding = binding;
        }

        public void setState(Boolean isActive) {
            KeyBinding.setKeyBindState(binding.getKeyCode(), isActive);
        }

        // Forces the KeyBind to update its state regardless of the game state
        public void update() {
            int keyCode = binding.getKeyCode();
            KeyBinding.setKeyBindState(keyCode, Keyboard.isKeyDown(keyCode));
        }

        public int getKeyCode() {
            return binding.getKeyCode();
        }

        public V8Object create(V8 runtime) {
            V8Object obj = new V8Object(runtime);

            obj.registerJavaMethod(this, "setState", "setState", new Class[] {Boolean.class});
            obj.registerJavaMethod(this, "getKeyCode", "getKeyCode", new Class[] {});
            obj.registerJavaMethod(this, "update", "update", new Class[] {});

            JSEngine.instance.releaseAtEnd(obj);
            return obj;
        }

        public static V8Object create(V8 runtime, KeyBinding binding) {
            return new JSKeyBind(binding).create(runtime);
        }
    }

    public static V8Object create(V8 runtime) {
        GameSettings gs = Minecraft.getMinecraft().gameSettings;

        V8Object obj = new V8Object(runtime);

        V8Array hotbarKeyBinds = new V8Array(runtime);
        for (KeyBinding binding : gs.keyBindsHotbar) {
            hotbarKeyBinds.push(JSKeyBind.create(runtime, binding));
        }

        obj.add("attack", JSKeyBind.create(runtime, gs.keyBindAttack));
        obj.add("useItem", JSKeyBind.create(runtime, gs.keyBindUseItem));
        obj.add("drop", JSKeyBind.create(runtime, gs.keyBindDrop));
        obj.add("pickBlock", JSKeyBind.create(runtime, gs.keyBindPickBlock));

        obj.add("forward", JSKeyBind.create(runtime, gs.keyBindForward));
        obj.add("right", JSKeyBind.create(runtime, gs.keyBindRight));
        obj.add("left", JSKeyBind.create(runtime, gs.keyBindLeft));
        obj.add("back", JSKeyBind.create(runtime, gs.keyBindBack));
        obj.add("jump", JSKeyBind.create(runtime, gs.keyBindJump));

        obj.add("sneak", JSKeyBind.create(runtime, gs.keyBindSneak));
        obj.add("sprint", JSKeyBind.create(runtime, gs.keyBindSprint));

        obj.add("chat", JSKeyBind.create(runtime, gs.keyBindChat));
        obj.add("command", JSKeyBind.create(runtime, gs.keyBindCommand));
        obj.add("inventory", JSKeyBind.create(runtime, gs.keyBindInventory));
        obj.add("playerList", JSKeyBind.create(runtime, gs.keyBindPlayerList));

        obj.add("fullscreen", JSKeyBind.create(runtime, gs.keyBindFullscreen));
        obj.add("screenshot", JSKeyBind.create(runtime, gs.keyBindScreenshot));
        obj.add("spectatorOutlines", JSKeyBind.create(runtime, gs.keyBindSpectatorOutlines));

        obj.add("togglePerspective", JSKeyBind.create(runtime, gs.keyBindTogglePerspective));
        obj.add("smoothCamera", JSKeyBind.create(runtime, gs.keyBindSmoothCamera));

        obj.add("swapHands", JSKeyBind.create(runtime, gs.keyBindSwapHands));
        obj.add("hotbar", hotbarKeyBinds);

        JSEngine.instance.releaseAtEnd(hotbarKeyBinds);
        JSEngine.instance.releaseAtEnd(obj);
        return obj;
    }
}
