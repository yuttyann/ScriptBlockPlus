package com.github.yuttyann.scriptblockplus.listener.nms;

import org.bukkit.World;

import com.github.yuttyann.scriptblockplus.enums.reflection.PackageType;

public final class NMSWorld {

	private final Object nmsWorld;
	private final World bukkitWorld;

	public NMSWorld(World world) {
		Object nmsWorld = null;
		try {
			nmsWorld = PackageType.CB.invokeMethod(world, null, "getHandle");
		} catch (ReflectiveOperationException e) {
			e.printStackTrace();
		}
		this.nmsWorld = nmsWorld;
		this.bukkitWorld = world;
	}

	public Object getNMSWorld() {
		return nmsWorld;
	}

	public World getBukkitWorld() {
		return bukkitWorld;
	}

	public MovingPosition rayTrace(Vec3D vec3d1, Vec3D vec3d2) {
		try {
			Object nmsVec3D1 = vec3d1.toNMSVec3D();
			Object nmsVec3D2 = vec3d2.toNMSVec3D();
			Object rayTrace = PackageType.NMS.invokeMethod(nmsWorld, null, "rayTrace", nmsVec3D1, nmsVec3D2);
			return rayTrace == null ? null : new MovingPosition(rayTrace);
		} catch (ReflectiveOperationException e) {
			e.printStackTrace();
		}
		return null;
	}
}