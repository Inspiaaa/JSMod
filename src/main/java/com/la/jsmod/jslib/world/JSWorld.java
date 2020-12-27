package com.la.jsmod.jslib.world;

import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8Array;
import com.eclipsesource.v8.V8Object;
import com.la.jsmod.JSEngine;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;

public class JSWorld {
    public static JSWorld instance;
    private Minecraft mc;

    public JSWorld() {
        mc = Minecraft.getMinecraft();
    }

    private int toInt(Object val) {
        if (val instanceof Double) {
            return ((Double) val).intValue();
        }

        // Assume its an Integer
        return (Integer) val;
    }

    public void setBlock(V8Array pos, Integer blockID) {
        int x = toInt(pos.get(0));
        int y = toInt(pos.get(1));
        int z = toInt(pos.get(2));
        pos.release();

        Block block = JSBlocks.get(blockID);
        IBlockState state = block.getDefaultState();

        mc.world.setBlockState(new BlockPos(x, y ,z), state);
    }

    public int getBlock(V8Array pos) {
        int x = toInt(pos.get(0));
        int y = toInt(pos.get(1));
        int z = toInt(pos.get(2));
        pos.release();

        try {
            IBlockState state = mc.world.getBlockState(new BlockPos(x, y, z));
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
        obj.registerJavaMethod(instance, "getBlock", "getBlock", new Class[] { V8Array.class });

        JSEngine.instance.releaseAtEnd(obj);
        return obj;
    }
}
