package com.la.jsmod.jslib;

import com.caoccao.javet.annotations.V8Function;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.values.reference.V8ValueArray;
import com.caoccao.javet.values.reference.V8ValueObject;
import com.la.jsmod.JSEngine;
import com.la.jsmod.util.ConversionHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

public class JSPlayer {
    public static JSPlayer instance;
    private Minecraft mc;
    private EntityPlayerSP player;

    private V8Runtime runtime;

    public JSPlayer(V8Runtime runtime) {
        mc = Minecraft.getMinecraft();
        player = mc.player;
        this.runtime = runtime;
    }

    @V8Function
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
                    break;
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
    // blockLookingAt

    @V8Function
    public boolean isOnGround() {
        return player.onGround;
    }

    @V8Function
    public boolean isSprinting() {
        return mc.player.isSprinting();
    }

    @V8Function
    public boolean isSneaking() {
        return mc.player.isSneaking();
    }

    @V8Function
    public float getHealth() {
        return mc.player.getHealth();
    }

    @V8Function
    public float getMaxHealth() {
        return mc.player.getMaxHealth();
    }

    private V8ValueArray makeVector(double... parameters) {
        V8ValueArray vec = null;

        try {
            vec = runtime.createV8ValueArray();

            for (double parameter : parameters) {
                vec.push(parameter);
            }

            vec.setWeak();
        }
        catch (JavetException e) {
            e.printStackTrace();
        }

        return vec;
    }

    @V8Function
    public V8ValueArray getPos() {
        // TODO: Add prototype to vec object (To add useful methods like magnitude)
        return makeVector(player.posX, player.posY, player.posZ);
    }

    @V8Function
    public V8ValueArray getVel() {
        // TODO: Add prototype to vec object (To add useful methods like magnitude)
        return makeVector(player.motionX, player.motionY, player.motionZ);
    }

    @V8Function
    public V8ValueArray getRot() {
        return makeVector(player.rotationYaw, player.rotationPitch);
    }

    @V8Function
    public void setPos(V8ValueArray pos) {
        try {
            double x = ConversionHelper.toDouble(pos.get(0));
            double y = ConversionHelper.toDouble(pos.get(1));
            double z = ConversionHelper.toDouble(pos.get(2));

            mc.player.setPosition(x, y, z);
        }
        catch (JavetException e) {
            e.printStackTrace();
        }

        // TODO: Close the pos resource?
    }

    @V8Function
    public void setRot(V8ValueArray rot) {
        try {
            float yaw = ConversionHelper.toFloat(rot.get(0));
            float pitch = ConversionHelper.toFloat(rot.get(1));

            mc.player.rotationYawHead = yaw;
            mc.player.rotationPitch = pitch;
        }
        catch (JavetException e) {
            e.printStackTrace();
        }

        // TODO: Close the rot resource?
    }

    @V8Function
    public void setVel(V8ValueArray vel) {
        try {
            double x = ConversionHelper.toDouble(vel.get(0));
            double y = ConversionHelper.toDouble(vel.get(1));
            double z = ConversionHelper.toDouble(vel.get(2));

            mc.player.motionX = x;
            mc.player.motionY = y;
            mc.player.motionZ = z;
        }
        catch (JavetException e) {
            e.printStackTrace();
        }

        // TODO: Close the pos resource?
    }

    public static V8ValueObject create(V8Runtime runtime) {
        instance = new JSPlayer(runtime);
        return JSEngine.instance.createGlobalJsLib(instance);
    }
}
