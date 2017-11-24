package com.github.yuttyann.scriptblockplus.listener.interact;

import java.lang.reflect.Method;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.World;

import com.github.yuttyann.scriptblockplus.enums.PackageType;

public final class NMSWorld {

	private final Object world;

	public NMSWorld(World world) {
		Object nmsWorld = null;
		try {
			nmsWorld = PackageType.CB.getMethod("CraftWorld", "getHandle", ArrayUtils.EMPTY_CLASS_ARRAY);
			nmsWorld = ((Method) nmsWorld).invoke(world, ArrayUtils.EMPTY_OBJECT_ARRAY);
		} catch (ReflectiveOperationException e) {
			e.printStackTrace();
		}
		this.world = nmsWorld;
	}

	public Object getWorld() {
		return world;
	}

	public MovingPosition rayTrace(Vec3D start, Vec3D end, boolean flag) throws ReflectiveOperationException {
		Object nmsStart = start.toNMSVec3D();
		Object nmsEnd = end.toNMSVec3D();
		Object rayTrace = PackageType.NMS.invokeMethod(world, "World", "rayTrace", nmsStart, nmsEnd, flag);
		if (rayTrace == null) {
			return null;
		}
		return new MovingPosition(rayTrace);
	}
}