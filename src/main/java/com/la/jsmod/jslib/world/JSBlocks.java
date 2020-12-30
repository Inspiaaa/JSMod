package com.la.jsmod.jslib.world;

import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8Array;
import com.eclipsesource.v8.V8Object;
import com.google.common.collect.ImmutableMap;
import com.la.jsmod.JSEngine;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class JSBlocks {

    private static HashMap<Integer, Block> idToBlock = new HashMap<Integer, Block>();
    private static HashMap<Block, Integer> blockToID = new HashMap<Block, Integer>();
    private static HashMap<Block, HashMap<String, IProperty<?>>> blockAndNameToProperty = new HashMap<Block, HashMap<String, IProperty<?>>>();
    private static HashMap<IProperty<?>, HashMap<String, Comparable<?>>> propertyToValues= new HashMap<IProperty<?>, HashMap<String, Comparable<?>>>();

    public static IProperty<?> getProperty(Block block, String name) {
        return blockAndNameToProperty.get(block).get(name);
    }

    public static Comparable<?> getPropertyValue(IProperty prop, String valueName) {
        return propertyToValues.get(prop).get(valueName);
    }

    public static Block get(Integer id) {
        return idToBlock.get(id);
    }

    public static int getID(Block block) {
        return blockToID.get(block);
    }

    private static void updatePropertiesRegistry(Block block) {
        IBlockState state = block.getDefaultState();

        ImmutableMap<IProperty<?>, Comparable<?>> map = state.getProperties();

        if (blockAndNameToProperty.containsKey(block)) {
            return;
        }

        HashMap<String, IProperty<?>> nameToProperty = new HashMap<String, IProperty<?>>();
        blockAndNameToProperty.put(block, nameToProperty);

        for (Map.Entry<IProperty<?>, Comparable<?>> entry : map.entrySet()) {
            IProperty<?> prop = entry.getKey();
            Collection<?> allowedValues = prop.getAllowedValues();
            String name = prop.getName();

            if (nameToProperty.containsKey(name)) {
                continue;
            }

            HashMap<String, Comparable<?>> nameToValue = new HashMap<String, Comparable<?>>();
            nameToProperty.put(name, prop);
            propertyToValues.put(prop, nameToValue);

            for (Object value : allowedValues) {
                nameToValue.put(value.toString(), (Comparable<?>) value);
            }
        }
    }

    public static V8Object create(V8 runtime) {
        V8Object obj = new V8Object(runtime);

        int id = 0;
        for (Block block : ForgeRegistries.BLOCKS) {
            ResourceLocation res = block.getRegistryName();
            String domain = res.getResourceDomain();
            String name = res.getResourcePath().toUpperCase();

            updatePropertiesRegistry(block);

            // Block belongs to standard Minecraft
            if (domain.equals("minecraft")) {
                obj.add(name, id);
            }

            // Block belongs to another mod
            else {
                V8Array params = new V8Array(runtime);
                params.push(domain);

                if (! obj.executeBooleanFunction("hasOwnProperty", params)) {
                    V8Object namespace = new V8Object(runtime);
                    JSEngine.instance.releaseAtEnd(namespace);
                    obj.add(domain, namespace);
                }

                V8Object namespace = (V8Object) obj.get(domain);
                namespace.add(name, id);
                namespace.release();

                params.release();
            }

            idToBlock.put(id, block);
            blockToID.put(block, id);
            id ++;
        }

        JSEngine.instance.releaseAtEnd(obj);
        return obj;
    }
}
