package com.github.yuttyann.scriptblockplus.listener.nms;

import org.bukkit.World;

import com.github.yuttyann.scriptblockplus.enums.reflection.PackageType;

public final class NMSWorld {

	private final Object world;

	public NMSWorld(World world) {
		Object nmsWorld = null;
		try {
			nmsWorld = PackageType.CB.invokeMethod(world, null, "getHandle", (Object[]) null);
		} catch (ReflectiveOperationException e) {
			e.printStackTrace();
		}
		this.world = nmsWorld;
	}

	public Object getWorld() {
		return world;
	}

	public MovingPosition rayTrace(Vec3D start, Vec3D end, boolean flag) {
		try {
			Object nmsStart = start.toNMSVec3D();
			Object nmsEnd = end.toNMSVec3D();
			Object rayTrace = PackageType.NMS.invokeMethod(world, null, "rayTrace", nmsStart, nmsEnd, flag);
			if (rayTrace == null) {
				return null;
			}
			return new MovingPosition(rayTrace);
		} catch (ReflectiveOperationException e) {
			e.printStackTrace();
		}
		return null;
	}
}