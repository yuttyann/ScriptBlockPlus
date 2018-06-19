package com.github.yuttyann.scriptblockplus.listener.nms;

import java.lang.reflect.Field;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import com.github.yuttyann.scriptblockplus.enums.reflection.PackageType;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public final class MovingPosition {

	private class BlockPos {

		private final int x;
		private final int y;
		private final int z;

		private BlockPos(Object rayTrace) throws ReflectiveOperationException {
			if (Utils.isCBXXXorLater("1.8")) {
				Object blockPosition = PackageType.NMS.invokeMethod(rayTrace, null, "a", (Object[]) null);
				this.x = (int) PackageType.NMS.invokeMethod(blockPosition, null, "getX", (Object[]) null);
				this.y = (int) PackageType.NMS.invokeMethod(blockPosition, null, "getY", (Object[]) null);
				this.z = (int) PackageType.NMS.invokeMethod(blockPosition, null, "getZ", (Object[]) null);
			} else {
				this.x = getMOPField("b").getInt(rayTrace);
				this.y = getMOPField("c").getInt(rayTrace);
				this.z = getMOPField("d").getInt(rayTrace);
			}
		}
	}

	private final Vec3D vec3d;
	private final BlockPos blockPos;
	private final BlockFace blockFace;

	MovingPosition(Object rayTrace) throws ReflectiveOperationException {
		this.vec3d = Vec3D.fromNMSVec3D(getMOPField("pos").get(rayTrace));
		this.blockPos = new BlockPos(rayTrace);

		Object face = getMOPField(Utils.isCBXXXorLater("1.8") ? "direction" : "face").get(rayTrace);
		this.blockFace = (BlockFace) PackageType.CB_BLOCK.invokeMethod(null, "CraftBlock", "notchToBlockFace", face);
	}

	public Vec3D getPosition() {
		return vec3d == null ? Vec3D.a : vec3d;
	}

	public Block getBlock(World world) {
		return world.getBlockAt(blockPos.x, blockPos.y, blockPos.z);
	}

	public BlockFace getFace() {
		return blockFace;
	}

	private Field getMOPField(String fieldName) throws ReflectiveOperationException {
		return PackageType.NMS.getField("MovingObjectPosition", fieldName);
	}
}