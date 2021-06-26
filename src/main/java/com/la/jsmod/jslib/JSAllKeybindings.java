package com.la.jsmod.jslib;

import com.caoccao.javet.annotations.V8Function;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.values.reference.V8ValueArray;
import com.caoccao.javet.values.reference.V8ValueObject;
import com.la.jsmod.JSEngine;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

public class JSAllKeybindings {
    public static class JSKeyBind {
        KeyBinding binding;

        public JSKeyBind(KeyBinding binding) {
            this.binding = binding;
        }

        @V8Function
        public void setState(boolean isActive) {
            KeyBinding.setKeyBindState(binding.getKeyCode(), isActive);
        }

        // Forces the KeyBind to update its state regardless of the game state
        @V8Function
        public void update() {
            int keyCode = binding.getKeyCode();
            KeyBinding.setKeyBindState(keyCode, Keyboard.isKeyDown(keyCode));
        }

        @V8Function
        public int getKeyCode() {
            return binding.getKeyCode();
        }

        public V8ValueObject create(V8Runtime runtime) {
            return JSEngine.instance.createGlobalJsLib(this);
        }

        public static V8ValueObject create(V8Runtime runtime, KeyBinding binding) {
            return new JSKeyBind(binding).create(runtime);
        }
    }

    public static V8ValueObject create(V8Runtime runtime) throws JavetException {
        GameSettings gs = Minecraft.getMinecraft().gameSettings;

        V8ValueObject obj = runtime.createV8ValueObject();
        obj.setWeak();

        V8ValueArray hotbarKeyBinds = runtime.createV8ValueArray();
        hotbarKeyBinds.setWeak();
        for (KeyBinding binding : gs.keyBindsHotbar) {
            hotbarKeyBinds.push(JSKeyBind.create(runtime, binding));
        }

        obj.set("attack", JSKeyBind.create(runtime, gs.keyBindAttack));
        obj.set("useItem", JSKeyBind.create(runtime, gs.keyBindUseItem));
        obj.set("drop", JSKeyBind.create(runtime, gs.keyBindDrop));
        obj.set("pickBlock", JSKeyBind.create(runtime, gs.keyBindPickBlock));

        obj.set("forward", JSKeyBind.create(runtime, gs.keyBindForward));
        obj.set("right", JSKeyBind.create(runtime, gs.keyBindRight));
        obj.set("left", JSKeyBind.create(runtime, gs.keyBindLeft));
        obj.set("back", JSKeyBind.create(runtime, gs.keyBindBack));
        obj.set("jump", JSKeyBind.create(runtime, gs.keyBindJump));

        obj.set("sneak", JSKeyBind.create(runtime, gs.keyBindSneak));
        obj.set("sprint", JSKeyBind.create(runtime, gs.keyBindSprint));

        obj.set("chat", JSKeyBind.create(runtime, gs.keyBindChat));
        obj.set("command", JSKeyBind.create(runtime, gs.keyBindCommand));
        obj.set("inventory", JSKeyBind.create(runtime, gs.keyBindInventory));
        obj.set("playerList", JSKeyBind.create(runtime, gs.keyBindPlayerList));

        obj.set("fullscreen", JSKeyBind.create(runtime, gs.keyBindFullscreen));
        obj.set("screenshot", JSKeyBind.create(runtime, gs.keyBindScreenshot));
        obj.set("spectatorOutlines", JSKeyBind.create(runtime, gs.keyBindSpectatorOutlines));

        obj.set("togglePerspective", JSKeyBind.create(runtime, gs.keyBindTogglePerspective));
        obj.set("smoothCamera", JSKeyBind.create(runtime, gs.keyBindSmoothCamera));

        obj.set("swapHands", JSKeyBind.create(runtime, gs.keyBindSwapHands));
        obj.set("hotbar", hotbarKeyBinds);

        return obj;
    }
}
