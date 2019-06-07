package com.github.yuttyann.scriptblockplus.listener.nms;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;

import com.github.yuttyann.scriptblockplus.enums.reflection.PackageType;

public class Vec3D {

	public static final Vec3D ZERO = new Vec3D(0.0D, 0.0D, 0.0D);

	private final double x;
	private final double y;
	private final double z;

	public Vec3D(double x, double y, double z) {
		this.x = x == -0.0D ? 0.0D : x;
		this.y = y == -0.0D ? 0.0D : y;
		this.z = z == -0.0D ? 0.0D : z;
	}

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public int getBlockX() {
        return NumberConversions.floor(x);
    }

    public int getBlockY() {
        return NumberConversions.floor(y);
    }

    public int getBlockZ() {
        return NumberConversions.floor(z);
    }

	public Vec3D add(double x, double y, double z) {
		return new Vec3D(this.x + x, this.y + y, this.z + z);
	}

	public Vec3D subtract(double x, double y, double z) {
		return new Vec3D(this.x - x, this.y - y, this.z - z);
	}

	public Vec3D multiply(double m) {
		return new Vec3D(this.x * m, this.y * m, this.z * m);
	}

	@Override
	public int hashCode() {
		long i = Double.doubleToLongBits(this.x);
		int j = (int) (i ^ i >>> 32);
		i = Double.doubleToLongBits(this.y);
		j = 31 * j + (int) (i ^ i >>> 32);
		i = Double.doubleToLongBits(this.z);
		j = 31 * j + (int) (i ^ i >>> 32);
		return j;
	}

	@Override
	public String toString() {
		return "(" + this.x + ", " + this.y + ", " + this.z + ")";
	}

	public Vector toVector() {
		return new Vector(x, y, z);
	}

	public Location toLocation(World world) {
		return toLocation(world, 0.0F, 0.0F);
	}

	public Location toLocation(World world, float yaw, float pitch) {
		return new Location(world, x, y, z, yaw, pitch);
	}

	public Object toNMSVec3D() throws ReflectiveOperationException {
		return PackageType.NMS.newInstance("Vec3D", x, y, z);
	}

	public static Vec3D fromNMSVec3D(Object nmsVec3D) throws ReflectiveOperationException {
		double x = PackageType.NMS.getField("Vec3D", "x").getDouble(nmsVec3D);
		double y = PackageType.NMS.getField("Vec3D", "y").getDouble(nmsVec3D);
		double z = PackageType.NMS.getField("Vec3D", "z").getDouble(nmsVec3D);
		return new Vec3D(x, y, z);
	}
}