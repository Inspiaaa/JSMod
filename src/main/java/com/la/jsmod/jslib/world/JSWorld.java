package com.la.jsmod.jslib.world;

import com.caoccao.javet.annotations.V8Function;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.values.primitive.V8ValueBoolean;
import com.caoccao.javet.values.primitive.V8ValueInteger;
import com.caoccao.javet.values.primitive.V8ValueString;
import com.caoccao.javet.values.reference.IV8ValueArray;
import com.caoccao.javet.values.reference.V8ValueArray;
import com.caoccao.javet.values.reference.V8ValueObject;
import com.google.common.collect.ImmutableMap;
import com.la.jsmod.JSEngine;
import com.la.jsmod.util.ConversionHelper;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;

import java.util.Map;

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
    @V8Function
    public void setBlockLocally(V8ValueArray pos, Integer blockID) {
        BlockPos blockPos = ConversionHelper.toBlockPos(pos);

        Block block = JSBlocks.get(blockID);
        IBlockState state = block.getDefaultState();

        mc.world.setBlockState(blockPos, state);
    }

    // setBlock attempts to place the block on the integrated server side (if available) but can resort to just setting
    // it locally for the client
    @V8Function
    public void setBlock(V8ValueArray pos, Integer blockID) {
        BlockPos blockPos = ConversionHelper.toBlockPos(pos);

        Block block = JSBlocks.get(blockID);
        IBlockState state = block.getDefaultState();

        if (mc.getIntegratedServer() != null)
            mc.getIntegratedServer().getEntityWorld().setBlockState(blockPos, state);
        else
            mc.world.setBlockState(blockPos, state);
    }

    @V8Function
    public int getBlock(V8ValueArray pos) {
        BlockPos blockPos = ConversionHelper.toBlockPos(pos);

        try {
            IBlockState state = mc.world.getBlockState(blockPos);
            return JSBlocks.getID(state.getBlock());
        }
        catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @V8Function
    public V8ValueObject getBlockState(V8ValueArray pos) throws JavetException {
        BlockPos blockPos = ConversionHelper.toBlockPos(pos);

        V8ValueObject obj = JSEngine.instance.runtime.createV8ValueObject();
        obj.setWeak();

        try {
            IBlockState state = mc.world.getBlockState(blockPos);
            ImmutableMap<IProperty<?>, Comparable<?>> map = state.getProperties();

            for (Map.Entry<IProperty<?>, Comparable<?>> entry : map.entrySet()) {
                IProperty<?> prop = entry.getKey();
                Comparable<?> value = entry.getValue();

                String name = prop.getName();

                if (prop instanceof PropertyInteger) {
                    obj.set(name, (Integer) value);
                }
                else if (prop instanceof PropertyBool) {
                    obj.set(name, (Boolean) value);
                }
                else {
                    obj.set(prop.getName(), value.toString());
                }
            }

            return obj;
        }
        catch (Exception e) {
            e.printStackTrace();
            return obj;
        }
    }

    @V8Function
    public void setBlockState(V8ValueArray pos, V8ValueObject jsState) throws JavetException {
        BlockPos blockPos = ConversionHelper.toBlockPos(pos);

        IBlockState oldState = mc.world.getBlockState(blockPos);
        IBlockState newState = oldState;

        IV8ValueArray propertyNames = jsState.getOwnPropertyNames();
        int count = propertyNames.getLength();

        for (int i = 0; i < count; i++) {
            String propertyName = ((V8ValueString) propertyNames.get(i)).toPrimitive();
            Object value = jsState.get(propertyName);

            try {
                IProperty prop = JSBlocks.getProperty(oldState.getBlock(), propertyName);

                if (value instanceof V8ValueInteger) {
                    newState = newState.withProperty(prop, ((V8ValueInteger) value).toPrimitive());
                } else if (value instanceof V8ValueBoolean) {
                    newState = newState.withProperty(prop, ((V8ValueBoolean) value).toPrimitive());
                } else if (value instanceof V8ValueString) {
                    newState = newState.withProperty(prop, JSBlocks.getPropertyValue(prop, ((V8ValueString) value).toPrimitive()));
                }
            }
            catch (Exception e) {}
        }

        if (mc.getIntegratedServer() != null) {
            mc.getIntegratedServer().getEntityWorld().setBlockState(blockPos, newState);
        }
        else {
            mc.world.setBlockState(blockPos, newState);
        }
    }

    public static V8ValueObject create(V8Runtime runtime) {
        instance = new JSWorld();
        return JSEngine.instance.createGlobalJsLib(instance);
    }
}
