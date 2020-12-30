package com.la.jsmod.jslib;

import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8Array;
import com.eclipsesource.v8.V8Object;
import com.la.jsmod.JSEngine;
import com.la.jsmod.util.ConversionHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

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
    // getChest()
    // canSeeEntity(Entity ...)

    public boolean isOnGround() {
        return player.onGround;
    }

    public boolean isSprinting() {
        return mc.player.isSprinting();
    }

    public boolean isSneaking() {
        return mc.player.isSneaking();
    }

    public float getHealth() {
        return mc.player.getHealth();
    }

    public float getMaxHealth() {
        return mc.player.getMaxHealth();
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

    public V8Array getRot() {
        V8Array rot = new V8Array(JSEngine.instance.runtime);
        rot.push(player.rotationYawHead);
        rot.push(player.rotationPitch);
        JSEngine.instance.releaseNextTick(rot);

        return rot;
    }

    public void setPos(V8Array pos) {
        double x = ConversionHelper.toDouble(pos.get(0));
        double y = ConversionHelper.toDouble(pos.get(1));
        double z = ConversionHelper.toDouble(pos.get(2));
        pos.release();

        mc.player.setPosition(x, y, z);
    }

    public void setRot(V8Array rot) {
        float yaw = ConversionHelper.toFloat(rot.get(0));
        float pitch = ConversionHelper.toFloat(rot.get(1));
        rot.release();

        mc.player.rotationYawHead = yaw;
        mc.player.rotationPitch = pitch;
    }

    public void setVel(V8Array vel) {
        double x = ConversionHelper.toDouble(vel.get(0));
        double y = ConversionHelper.toDouble(vel.get(1));
        double z = ConversionHelper.toDouble(vel.get(2));
        vel.release();

        mc.player.motionX = x;
        mc.player.motionY = y;
        mc.player.motionZ = z;
    }

    public static V8Object create(V8 runtime) {
        instance = new JSPlayer();

        V8Object obj = new V8Object(runtime);

        obj.registerJavaMethod(instance, "leftClick", "leftClick", new Class[] {});

        obj.registerJavaMethod(instance, "isOnGround", "isOnGround", new Class[] {});
        obj.registerJavaMethod(instance, "isSprinting", "isSprinting", new Class[] {});
        obj.registerJavaMethod(instance, "isSneaking", "isSneaking", new Class[] {});

        obj.registerJavaMethod(instance, "getHealth", "getHealth", new Class[] {});
        obj.registerJavaMethod(instance, "getMaxHealth", "getMaxHealth", new Class[] {});

        obj.registerJavaMethod(instance, "getPos", "getPos", new Class[] {});
        obj.registerJavaMethod(instance, "getVel", "getVel", new Class[] {});
        obj.registerJavaMethod(instance, "getRot", "getRot", new Class[] {});

        obj.registerJavaMethod(instance, "setPos", "setPos", new Class[] {V8Array.class});
        obj.registerJavaMethod(instance, "setVel", "setVel", new Class[] {V8Array.class});
        obj.registerJavaMethod(instance, "setRot", "setRot", new Class[] {V8Array.class});

        JSEngine.instance.releaseAtEnd(obj);
        return obj;
    }
}
