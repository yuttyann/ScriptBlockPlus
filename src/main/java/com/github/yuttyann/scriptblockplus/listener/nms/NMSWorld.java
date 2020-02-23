package com.github.yuttyann.scriptblockplus.listener.nms;

import java.lang.reflect.Constructor;

import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.github.yuttyann.scriptblockplus.enums.reflection.PackageType;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public final class NMSWorld {


	private static final Enum<?>[] NMS_ENUMS = new Enum<?>[2];

	static {
		try {
			if (Utils.isCBXXXorLater("1.14")) {
				NMS_ENUMS[0] = PackageType.NMS.getEnumValueOf("RayTrace$BlockCollisionOption", "OUTLINE");
				NMS_ENUMS[1] = PackageType.NMS.getEnumValueOf("RayTrace$FluidCollisionOption", "NONE");
			} else if (Utils.isCBXXXorLater("1.13")) {
				NMS_ENUMS[0] = PackageType.NMS.getEnumValueOf("FluidCollisionOption", "NEVER");
			}
		} catch (ReflectiveOperationException e) {
			e.printStackTrace();
		}
	}

	private static final Constructor<?> RAYTRACE = getConstructor();

	private final World bukkitWorld;

	public NMSWorld(@NotNull World world) {
		this.bukkitWorld = world;
	}

	@NotNull
	public World getWorld() {
		return bukkitWorld;
	}

	@Nullable
	public MovingPosition rayTrace(SBVector vector1, SBVector vector2) {
		try {
			Object vec3d1 = vector1.toNMSVec3D();
			Object vec3d2 = vector2.toNMSVec3D();
			Object world = PackageType.CB.invokeMethod(bukkitWorld, "CraftWorld", "getHandle");
			if (Utils.isCBXXXorLater("1.14")) {
				Object rayTrace = RAYTRACE.newInstance(vec3d1, vec3d2, NMS_ENUMS[0], NMS_ENUMS[1], null);
				Object result = PackageType.NMS.invokeMethod(world, "World", "rayTrace", rayTrace);
				return result == null ? null : new MovingPosition(bukkitWorld, result);
			}
			Object[] args;
			if (Utils.isCBXXXorLater("1.13")) {
				args = new Object[] { vec3d1, vec3d2, NMS_ENUMS[0], false, false };
			} else {
				args = new Object[] { vec3d1, vec3d2, false };
			}
			Object rayTrace = PackageType.NMS.invokeMethod(world, "World", "rayTrace", args);
			return rayTrace == null ? null : new MovingPosition(bukkitWorld, rayTrace);
		} catch (ReflectiveOperationException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Nullable
	private static Constructor<?> getConstructor() {
		if (!Utils.isCBXXXorLater("1.14")) {
			return null;
		}
		try {
			Class<?>[] classes = new Class<?>[5];
			Class<?> vec3d = PackageType.NMS.getClass("Vec3D");
			classes[0] = vec3d;
			classes[1] = vec3d;
			classes[2] = NMS_ENUMS[0].getClass();
			classes[3] = NMS_ENUMS[1].getClass();
			classes[4] = PackageType.NMS.getClass("Entity");
			return PackageType.NMS.getConstructor("RayTrace", classes);
		} catch (ReflectiveOperationException e) {
			e.printStackTrace();
		}
		return null;
	}
}