package com.la.jsmod.jslib;

import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8Array;
import com.eclipsesource.v8.V8Object;
import com.la.jsmod.JSEngine;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class JSPlayer {
    public static JSPlayer instance;
    private static Minecraft mc;
    private static EntityPlayerSP player;

    public JSPlayer() {
        mc = Minecraft.getMinecraft();
        player = mc.player;
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

    public boolean isOnGround() {
        return player.onGround;
    }

    public V8Array getPos() {
        // TODO: Add prototype to vec object (To add useful methods like magnitude)
        V8Array vec = new V8Array(JSEngine.instance.runtime);
        vec.push(player.posX);
        vec.push(player.posY);
        vec.push(player.posZ);
        JSEngine.instance.releaseNextTick(vec);

        return vec;
    }

    public V8Array getVel() {
        // TODO: Add prototype to vec object (To add useful methods like magnitude)
        V8Array vec = new V8Array(JSEngine.instance.runtime);
        vec.push(player.motionX);
        vec.push(player.motionY);
        vec.push(player.motionZ);
        JSEngine.instance.releaseNextTick(vec);

        return vec;
    }

    public static V8Object create(V8 runtime) {
        instance = new JSPlayer();

        V8Object obj = new V8Object(runtime);

        obj.registerJavaMethod(instance, "leftClick", "leftClick", new Class[] {});
        obj.registerJavaMethod(instance, "isOnGround", "isOnGround", new Class[] {});
        obj.registerJavaMethod(instance, "getPos", "getPos", new Class[] {});
        obj.registerJavaMethod(instance, "getVel", "getVel", new Class[] {});

        JSEngine.instance.releaseAtEnd(obj);
        return obj;
    }
}
