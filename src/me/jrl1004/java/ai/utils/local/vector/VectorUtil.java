package me.jrl1004.java.ai.utils.local.vector;

import org.bukkit.util.Vector;

public class VectorUtil {

    public static boolean equals(Vector a, Vector b) {

        if (Double.doubleToLongBits(a.getX()) != Double.doubleToLongBits(b.getX())) {
            return false;
        }
        if (Double.doubleToLongBits(a.getY()) != Double.doubleToLongBits(b.getY())) {
            return false;
        }
        if (Double.doubleToLongBits(a.getZ()) != Double.doubleToLongBits(b.getZ())) {
            return false;
        }
        return true;
    }

}
