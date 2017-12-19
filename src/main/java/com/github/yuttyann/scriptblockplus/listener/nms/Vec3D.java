package com.github.yuttyann.scriptblockplus.listener.nms;

import javax.annotation.Nullable;

import org.bukkit.util.Vector;

import com.github.yuttyann.scriptblockplus.enums.reflection.PackageType;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public class Vec3D {

	public static final Vec3D a = new Vec3D(0.0D, 0.0D, 0.0D);

	public final double x;
	public final double y;
	public final double z;

	public Vec3D(double x, double y, double z) {
		if (x == -0.0D) {
			x = 0.0D;
		}
		if (y == -0.0D) {
			y = 0.0D;
		}
		if (z == -0.0D) {
			z = 0.0D;
		}
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vec3D a(Vec3D vec3d) {
		return new Vec3D(vec3d.x - this.x, vec3d.y - this.y, vec3d.z - this.z);
	}

	public Vec3D a() {
		double d0 = (double) MathHelper.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
		return d0 < 1.0E-4D ? Vec3D.a : new Vec3D(this.x / d0, this.y / d0, this.z / d0);
	}

	public double b(Vec3D vec3d) {
		return this.x * vec3d.x + this.y * vec3d.y + this.z * vec3d.z;
	}

	public Vec3D d(Vec3D vec3d) {
		return this.a(vec3d.x, vec3d.y, vec3d.z);
	}

	public Vec3D a(double x, double y, double z) {
		return this.add(-x, -y, -z);
	}

	public Vec3D e(Vec3D vec3d) {
		return this.add(vec3d.x, vec3d.y, vec3d.z);
	}

	public Vec3D add(double x, double y, double z) {
		return new Vec3D(this.x + x, this.y + y, this.z + z);
	}

	public double f(Vec3D vec3d) {
		double d0 = vec3d.x - this.x;
		double d1 = vec3d.y - this.y;
		double d2 = vec3d.z - this.z;
		return (double) MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
	}

	public double distanceSquared(Vec3D vec3d) {
		double d0 = vec3d.x - this.x;
		double d1 = vec3d.y - this.y;
		double d2 = vec3d.z - this.z;
		return d0 * d0 + d1 * d1 + d2 * d2;
	}

	public double c(double x, double y, double z) {
		double d3 = x - this.x;
		double d4 = y - this.y;
		double d5 = z - this.z;
		return d3 * d3 + d4 * d4 + d5 * d5;
	}

	public Vec3D a(double d0) {
		return new Vec3D(this.x * d0, this.y * d0, this.z * d0);
	}

	public double b() {
		return (double) MathHelper.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
	}

	@Nullable
	public Vec3D a(Vec3D vec3d, double d0) {
		double d1 = vec3d.x - this.x;
		double d2 = vec3d.y - this.y;
		double d3 = vec3d.z - this.z;
		if (d1 * d1 < 1.0000000116860974E-7D) {
			return null;
		} else {
			double d4 = (d0 - this.x) / d1;
			return d4 >= 0.0D && d4 <= 1.0D ? new Vec3D(this.x + d1 * d4, this.y + d2 * d4, this.z + d3 * d4) : null;
		}
	}

	@Nullable
	public Vec3D b(Vec3D vec3d, double d0) {
		double d1 = vec3d.x - this.x;
		double d2 = vec3d.y - this.y;
		double d3 = vec3d.z - this.z;
		if (d2 * d2 < 1.0000000116860974E-7D) {
			return null;
		} else {
			double d4 = (d0 - this.y) / d2;
			return d4 >= 0.0D && d4 <= 1.0D ? new Vec3D(this.x + d1 * d4, this.y + d2 * d4, this.z + d3 * d4) : null;
		}
	}

	@Nullable
	public Vec3D c(Vec3D vec3d, double d0) {
		double d1 = vec3d.x - this.x;
		double d2 = vec3d.y - this.y;
		double d3 = vec3d.z - this.z;
		if (d3 * d3 < 1.0000000116860974E-7D) {
			return null;
		} else {
			double d4 = (d0 - this.z) / d3;
			return d4 >= 0.0D && d4 <= 1.0D ? new Vec3D(this.x + d1 * d4, this.y + d2 * d4, this.z + d3 * d4) : null;
		}
	}

	public Vec3D a(float f) {
		float f1 = MathHelper.cos(f);
		float f2 = MathHelper.sin(f);
		double d0 = this.x;
		double d1 = this.y * (double) f1 + this.z * (double) f2;
		double d2 = this.z * (double) f1 - this.y * (double) f2;
		return new Vec3D(d0, d1, d2);
	}

	public Vec3D b(float f) {
		float f1 = MathHelper.cos(f);
		float f2 = MathHelper.sin(f);
		double d0 = this.x * (double) f1 + this.z * (double) f2;
		double d1 = this.y;
		double d2 = this.z * (double) f1 - this.x * (double) f2;
		return new Vec3D(d0, d1, d2);
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (!(object instanceof Vec3D)) {
			return false;
		} else {
			Vec3D vec3d = (Vec3D) object;
			return Double.compare(vec3d.x, this.x) != 0 ? false : (Double.compare(vec3d.y, this.y) != 0 ? false : Double.compare(vec3d.z, this.z) == 0);
		}
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

	public Object toNMSVec3D() {
		return newNMSVec3D(x, y, z);
	}

	public static Vec3D fromNMSVec3D(Object nmsVec3D) throws ReflectiveOperationException {
		double x = PackageType.NMS.getField("Vec3D", getFieldName("x", "a", "c")).getDouble(nmsVec3D);
		double y = PackageType.NMS.getField("Vec3D", getFieldName("y", "b", "d")).getDouble(nmsVec3D);
		double z = PackageType.NMS.getField("Vec3D", getFieldName("z", "c", "e")).getDouble(nmsVec3D);
		return new Vec3D(x, y, z);
	}

	public static Object newNMSVec3D(Vector vector) {
		return newNMSVec3D(vector.getX(), vector.getY(), vector.getZ());
	}

	public static Object newNMSVec3D(double x, double y, double z) {
		Object vec3D = null;
		try {
			if (Utils.isCB18orLater()) {
				vec3D = PackageType.NMS.newInstance("Vec3D", x, y, z);
			} else {
				vec3D = PackageType.NMS.invokeMethod(null, "Vec3D", "a", x, y, z);
			}
		} catch (ReflectiveOperationException e) {
			e.printStackTrace();
		}
		return vec3D;
	}

	private static String getFieldName(String _19orLater, String _175orLater, String other) {
		return Utils.isCB19orLater() ? _19orLater : Utils.isCB175orLater() ? _175orLater : other;
	}
}