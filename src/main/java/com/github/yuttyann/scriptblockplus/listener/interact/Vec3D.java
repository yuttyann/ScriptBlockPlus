package com.github.yuttyann.scriptblockplus.listener.interact;

import java.lang.reflect.Field;

import org.bukkit.util.Vector;

import com.github.yuttyann.scriptblockplus.enums.PackageType;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public class Vec3D {

	public static final Vec3D a = new Vec3D(0.0D, 0.0D, 0.0D);

	private static Field field_X;
	private static Field field_Y;
	private static Field field_Z;

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

	public Vec3D a(Vec3D vec3D) {
		return new Vec3D(vec3D.x - x, vec3D.y - y, vec3D.z - z);
	}

	public Vec3D a() {
		double d = Math.sqrt(x * x + y * y + z * z);
		if (d < 1.0E-4D) {
			return a;
		}
		return new Vec3D(x / d, y / d, z / d);
	}

	public double b(Vec3D vec3D) {
		return x * vec3D.x + y * vec3D.y + z * vec3D.z;
	}

	public Vec3D d(Vec3D vec3D) {
		return a(vec3D.x, vec3D.y, vec3D.z);
	}

	public Vec3D a(double x, double y, double z) {
		return add(-x, -y, -z);
	}

	public Vec3D e(Vec3D vec3D) {
		return add(vec3D.x, vec3D.y, vec3D.z);
	}

	public Vec3D add(double x, double y, double z) {
		return new Vec3D(this.x + x, this.y + y, this.z + z);
	}

	public double f(Vec3D vec3D) {
		double d1 = vec3D.x - x;
		double d2 = vec3D.y - y;
		double d3 = vec3D.z - z;
		return Math.sqrt(d1 * d1 + d2 * d2 + d3 * d3);
	}

	public double distanceSquared(Vec3D vec3D) {
		double d1 = vec3D.x - x;
		double d2 = vec3D.y - y;
		double d3 = vec3D.z - z;
		return d1 * d1 + d2 * d2 + d3 * d3;
	}

	public double c(double x, double y, double z) {
		double d1 = x - this.x;
		double d2 = y - this.y;
		double d3 = z - this.z;
		return d1 * d1 + d2 * d2 + d3 * d3;
	}

	public Vec3D a(double paramDouble) {
		return new Vec3D(x * paramDouble, y * paramDouble, z * paramDouble);
	}

	public double b() {
		return Math.sqrt(x * x + y * y + z * z);
	}

	public Vec3D a(Vec3D vec3D, double paramDouble) {
		double d1 = vec3D.x - x;
		double d2 = vec3D.y - y;
		double d3 = vec3D.z - z;
		if (d1 * d1 < 1.0000000116860974E-7D) {
			return null;
		}
		double d4 = (paramDouble - x) / d1;
		if (d4 < 0.0D || d4 > 1.0D) {
			return null;
		}
		return new Vec3D(x + d1 * d4, y + d2 * d4, z + d3 * d4);
	}

	public Vec3D b(Vec3D vec3D, double paramDouble) {
		double d1 = vec3D.x - x;
		double d2 = vec3D.y - y;
		double d3 = vec3D.z - z;
		if (d2 * d2 < 1.0000000116860974E-7D) {
			return null;
		}
		double d4 = (paramDouble - y) / d2;
		if (d4 < 0.0D || d4 > 1.0D) {
			return null;
		}
		return new Vec3D(x + d1 * d4, y + d2 * d4, z + d3 * d4);
	}

	public Vec3D c(Vec3D vec3D, double paramDouble) {
		double d1 = vec3D.x - x;
		double d2 = vec3D.y - y;
		double d3 = vec3D.z - z;
		if (d3 * d3 < 1.0000000116860974E-7D) {
			return null;
		}
		double d4 = (paramDouble - z) / d3;
		if (d4 < 0.0D || d4 > 1.0D) {
			return null;
		}
		return new Vec3D(x + d1 * d4, y + d2 * d4, z + d3 * d4);
	}

	public Vec3D a(float paramFloat) {
		double f1 = Math.cos(paramFloat);
		double f2 = Math.sin(paramFloat);
		double d1 = x;
		double d2 = y * f1 + z * f2;
		double d3 = z * f1 - y * f2;
		return new Vec3D(d1, d2, d3);
	}

	public Vec3D b(float paramFloat) {
		double f1 = Math.cos(paramFloat);
		double f2 = Math.sin(paramFloat);
		double d1 = x * f1 + z * f2;
		double d2 = y;
		double d3 = z * f1 - x * f2;
		return new Vec3D(d1, d2, d3);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Vec3D)) {
			return false;
		}
		Vec3D localVec3D = (Vec3D) obj;
		if (Double.compare(localVec3D.x, x) != 0) {
			return false;
		}
		if (Double.compare(localVec3D.y, y) != 0) {
			return false;
		}
		return Double.compare(localVec3D.z, z) == 0;
	}

	@Override
	public int hashCode() {
		long l = Double.doubleToLongBits(x);
		int i = (int) (l ^ l >>> 32);
		l = Double.doubleToLongBits(y);
		i = 31 * i + (int) (l ^ l >>> 32);
		l = Double.doubleToLongBits(z);
		i = 31 * i + (int) (l ^ l >>> 32);
		return i;
	}

	@Override
	public String toString() {
		return "(" + x + ", " + y + ", " + z + ")";
	}

	public Object toNMSVec3D() {
		return newNMSVec3D(x, y, z);
	}

	public static Vec3D fromNMSVec3D(Object nmsVec3D) throws ReflectiveOperationException {
		if (field_X == null) {
			field_X = nmsVec3D.getClass().getField(Utils.isCB19orLater() ? "x" : Utils.isCB175orLater() ? "a" : "c");
		}
		if (field_Y == null) {
			field_Y = nmsVec3D.getClass().getField(Utils.isCB19orLater() ? "y" : Utils.isCB175orLater() ? "b" : "d");
		}
		if (field_Z == null) {
			field_Z = nmsVec3D.getClass().getField(Utils.isCB19orLater() ? "z" : Utils.isCB175orLater() ? "c" : "e");
		}
		return new Vec3D(field_X.getDouble(nmsVec3D), field_Y.getDouble(nmsVec3D), field_Z.getDouble(nmsVec3D));
	}

	public static Object newNMSVec3D(Vector vector) {
		return newNMSVec3D(vector.getX(), vector.getY(), vector.getZ());
	}

	public static Object newNMSVec3D(double x, double y, double z) {
		Object vec3D = null;
		try {
			if (Utils.isCB1710orLater()) {
				vec3D = PackageType.NMS.newInstance("Vec3D", x, y, z);
			} else {
				vec3D = PackageType.NMS.invokeMethod(null, "Vec3D", "a", x, y, z);
			}
		} catch (ReflectiveOperationException e) {
			e.printStackTrace();
		}
		return vec3D;
	}
}