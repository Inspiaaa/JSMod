package com.la.jsmod.jslib.world;

import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8Array;
import com.eclipsesource.v8.V8Object;
import com.la.jsmod.JSEngine;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.HashMap;

public class JSBlocks {

    // TODO: Create map from int id to Block
    private static HashMap<Integer, Block> idToBlock = new HashMap<Integer, Block>();
    private static HashMap<Block, Integer> blockToID = new HashMap<Block, Integer>();

    public static Block get(Integer id) {
        return idToBlock.get(id);
    }

    public static int getID(Block block) {
        return blockToID.get(block);
    }

    public static V8Object create(V8 runtime) {
        V8Object obj = new V8Object(runtime);

        int id = 0;
        for (Block block : ForgeRegistries.BLOCKS) {
            ResourceLocation res = block.getRegistryName();
            String domain = res.getResourceDomain();
            String name = res.getResourcePath().toUpperCase();

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
