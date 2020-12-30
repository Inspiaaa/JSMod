package com.la.jsmod.util;

import com.eclipsesource.v8.V8Array;
import net.minecraft.util.math.BlockPos;

public class ConversionHelper {
    public static int toInt(Object val) {
        if (val instanceof Double) {
            return ((Double) val).intValue();
        }

        // Assume its an Integer
        return (Integer) val;
    }

    public static double toDouble(Object val) {
        if (val instanceof Integer) {
            return ((Integer) val).doubleValue();
        }

        // Assume its a Double
        return (Double) val;
    }

    public static float toFloat(Object val) {
        if (val instanceof Integer) {
            return ((Integer) val).floatValue();
        }

        if (val instanceof Double) {
            return ((Double) val).floatValue();
        }

        // Assume its a Float
        return (Float) val;
    }

    public static BlockPos toBlockPos(V8Array pos) {
        int x = toInt(pos.get(0));
        int y = toInt(pos.get(1));
        int z = toInt(pos.get(2));

        return new BlockPos(x, y, z);
    }
}
