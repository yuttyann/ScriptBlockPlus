package com.github.yuttyann.scriptblockplus.listener.raytrace;

import com.github.yuttyann.scriptblockplus.enums.reflection.PackageType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SBVector extends Vector {

    public static final SBVector ZERO = new SBVector(0.0D, 0.0D, 0.0D);

    public SBVector(double x, double y, double z) {
        super(x == -0.0D ? 0.0D : x, y == -0.0D ? 0.0D : y, z == -0.0D ? 0.0D : z);
    }

    @NotNull
    public SBVector add(double x, double y, double z) {
        return new SBVector(this.x + x, this.y + y, this.z + z);
    }

    @NotNull
    public SBVector subtract(double x, double y, double z) {
        return new SBVector(this.x - x, this.y - y, this.z - z);
    }

    @NotNull
    public SBVector multiply(double m) {
        return new SBVector(this.x * m, this.y * m, this.z * m);
    }

    @Nullable
    public Object toNMSVec3D() {
        try {
            return PackageType.NMS.newInstance("Vec3D", x, y, z);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
        return null;
    }
}