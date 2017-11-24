package com.github.yuttyann.scriptblockplus.listener.interact;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import com.github.yuttyann.scriptblockplus.enums.PackageType;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public final class MovingPosition {

	private static class BlockPos {

		private static Method a;

		private static Object getX;
		private static Object getY;
		private static Object getZ;

		public final int x;
		public final int y;
		public final int z;

		private BlockPos(Object rayTrace) throws ReflectiveOperationException {
			if (Utils.isCB18orLater()) {
				if (a == null) {
					a = rayTrace.getClass().getMethod("a", ArrayUtils.EMPTY_CLASS_ARRAY);
				}
				Object blockPosition = a.invoke(rayTrace, ArrayUtils.EMPTY_OBJECT_ARRAY);
				if (getX == null) {
					getX = blockPosition.getClass().getMethod("getX", ArrayUtils.EMPTY_CLASS_ARRAY);
				}
				if (getY == null) {
					getY = blockPosition.getClass().getMethod("getY", ArrayUtils.EMPTY_CLASS_ARRAY);
				}
				if (getZ == null) {
					getZ = blockPosition.getClass().getMethod("getZ", ArrayUtils.EMPTY_CLASS_ARRAY);
				}
				this.x = (int) ((Method) getX).invoke(blockPosition, ArrayUtils.EMPTY_OBJECT_ARRAY);
				this.y = (int) ((Method) getY).invoke(blockPosition, ArrayUtils.EMPTY_OBJECT_ARRAY);
				this.z = (int) ((Method) getZ).invoke(blockPosition, ArrayUtils.EMPTY_OBJECT_ARRAY);
			} else {
				if (getX == null) {
					getX = rayTrace.getClass().getField("b");
				}
				if (getY == null) {
					getY = rayTrace.getClass().getField("c");
				}
				if (getZ == null) {
					getZ = rayTrace.getClass().getField("d");
				}
				this.x = ((Field) getX).getInt(rayTrace);
				this.y = ((Field) getX).getInt(rayTrace);
				this.z = ((Field) getX).getInt(rayTrace);
			}
		}
	}

	private static Field pos;
	private static Field direction;

	private final BlockPos blockPos;
	private final Vec3D position;
	private final BlockFace blockFace;

	public MovingPosition(Object rayTrace) throws ReflectiveOperationException {
		if (pos == null) {
			pos = rayTrace.getClass().getField("pos");
		}
		if (direction == null) {
			direction = rayTrace.getClass().getField(Utils.isCB18orLater() ? "direction" : "face");
		}
		this.blockPos = new BlockPos(rayTrace);
		this.position = Vec3D.fromNMSVec3D(pos.get(rayTrace));
		this.blockFace = (BlockFace) PackageType.CB_BLOCK.invokeMethod(null, "CraftBlock", "notchToBlockFace", direction.get(rayTrace));
	}

	public Vec3D getPosition() {
		return position == null ? Vec3D.a : position;
	}

	public Block getBlock(World world) {
		return world.getBlockAt(blockPos.x, blockPos.y, blockPos.z);
	}

	public BlockFace getFace() {
		return blockFace;
	}
}