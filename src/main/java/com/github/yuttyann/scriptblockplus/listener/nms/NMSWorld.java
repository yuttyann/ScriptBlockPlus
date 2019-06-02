package com.github.yuttyann.scriptblockplus.listener.nms;

import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import com.github.yuttyann.scriptblockplus.enums.reflection.PackageType;
import com.github.yuttyann.scriptblockplus.script.option.nms.NMSHelper;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public final class NMSWorld {

	private final World bukkitWorld;

	public NMSWorld(World world) {
		this.bukkitWorld = world;
	}

	public World getWorld() {
		return bukkitWorld;
	}

	public MovingPosition rayTrace(Vec3D vec3d1, Vec3D vec3d2) {
		if (Utils.isCBXXXorLater("1.14")) {
			Vector vector = vec3d2.toVector();
			Location location = new Location(bukkitWorld, vec3d1.getX(), vec3d1.getY(), vec3d1.getZ());
			RayTraceResult result = bukkitWorld.rayTraceBlocks(location, vector, 4.5D, FluidCollisionMode.NEVER, true);
			return new MovingPosition(bukkitWorld, result);
		} else {
			Object nmsVec3D1 = vec3d1.toNMSVec3D();
			Object nmsVec3D2 = vec3d2.toNMSVec3D();
			try {
				Object world = PackageType.CB.invokeMethod(bukkitWorld, null, "getHandle");
				Object rayTrace;
				if (Utils.isCBXXXorLater("1.13")) {
					Object never = NMSHelper.getEnumField(PackageType.NMS.getClass("FluidCollisionOption"), "NEVER");
					rayTrace = PackageType.NMS.invokeMethod(world, null, "rayTrace", nmsVec3D1, nmsVec3D2, never, true, false);
				} else {
					rayTrace = PackageType.NMS.invokeMethod(world, null, "rayTrace", nmsVec3D1, nmsVec3D2, true);
				}
				return rayTrace == null ? null : new MovingPosition(bukkitWorld, rayTrace);
			} catch (ReflectiveOperationException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
}