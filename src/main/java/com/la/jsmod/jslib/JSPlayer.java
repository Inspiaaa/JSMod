package com.la.jsmod.jslib;

import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8Object;
import com.la.jsmod.JSEngine;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

public class JSPlayer {
    public static JSPlayer instance;
    private static Minecraft mc;

    public JSPlayer() {
        mc = Minecraft.getMinecraft();
    }

    // Copied from Minecraft.java
    public void leftClick() {
        if (!mc.player.isRowingBoat()) {
            switch (mc.objectMouseOver.typeOfHit) {
                case ENTITY:
                    mc.playerController.attackEntity(mc.player, mc.objectMouseOver.entityHit);
                    break;
                case BLOCK:
                    BlockPos blockpos = mc.objectMouseOver.getBlockPos();

                    if (!mc.world.isAirBlock(blockpos)) {
                        mc.playerController.clickBlock(blockpos, mc.objectMouseOver.sideHit);
                        break;
                    }

                case MISS:
                    mc.player.resetCooldown();
                    net.minecraftforge.common.ForgeHooks.onEmptyLeftClick(mc.player);
            }

            mc.player.swingArm(EnumHand.MAIN_HAND);
        }
    }

    // TODO: Add following features
    // rightClick()
    // isInInventory()
    // getPos()
    // getVel()
    // getRotation()
    // setRotation() / look()
    // isInChest()
    // sprint
    // sneak
    // isOnGround()

    public static V8Object create(V8 runtime) {
        instance = new JSPlayer();

        V8Object obj = new V8Object(runtime);
        obj.registerJavaMethod(instance, "leftClick", "leftClick", new Class[] {});
        JSEngine.instance.releaseAtEnd(obj);
        return obj;
    }
}
