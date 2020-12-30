package com.la.jsmod.jslib.world;

import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8Array;
import com.eclipsesource.v8.V8Object;
import com.la.jsmod.JSEngine;
import com.la.jsmod.util.ConversionHelper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;

public class JSWorld {
    public static JSWorld instance;
    private final Minecraft mc;

    // TODO: Add methods for following:
    // - Get time of day
    // - Get weather
    // - Get seed
    // ...

    public JSWorld() {
        mc = Minecraft.getMinecraft();
    }

    // setBlockLocally only sets the block for the client and is not permanent
    public void setBlockLocally(V8Array pos, Integer blockID) {
        BlockPos blockPos = ConversionHelper.toBlockPos(pos);
        pos.release();

        Block block = JSBlocks.get(blockID);
        IBlockState state = block.getDefaultState();

        mc.world.setBlockState(blockPos, state);
    }

    // setBlock attempts to place the block on the integrated server side (if available) but can resort to just setting
    // it locally for the client
    public void setBlock(V8Array pos, Integer blockID) {
        BlockPos blockPos = ConversionHelper.toBlockPos(pos);
        pos.release();

        Block block = JSBlocks.get(blockID);
        IBlockState state = block.getDefaultState();

        if (mc.getIntegratedServer() != null)
            mc.getIntegratedServer().getEntityWorld().setBlockState(blockPos, state);
        else
            mc.world.setBlockState(blockPos, state);
    }

    public int getBlock(V8Array pos) {
        BlockPos blockPos = ConversionHelper.toBlockPos(pos);
        pos.release();

        try {
            IBlockState state = mc.world.getBlockState(blockPos);
            return JSBlocks.getID(state.getBlock());
        }
        catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static V8Object create(V8 runtime) {
        instance = new JSWorld();

        V8Object obj = new V8Object(runtime);

        obj.registerJavaMethod(instance, "setBlock", "setBlock", new Class[] { V8Array.class, Integer.class });
        obj.registerJavaMethod(instance, "setBlock", "setBlockLocally", new Class[] { V8Array.class, Integer.class });

        obj.registerJavaMethod(instance, "getBlock", "getBlock", new Class[] { V8Array.class });

        JSEngine.instance.releaseAtEnd(obj);
        return obj;
    }
}
