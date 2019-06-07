package com.github.yuttyann.scriptblockplus.listener.nms;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import com.github.yuttyann.scriptblockplus.enums.reflection.PackageType;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public final class MovingPosition {

	private static final String KEY_BPS = Utils.isCBXXXorLater("1.13") ? "getBlockPosition" : "a";
	private static final String KEY_MOP = "MovingObjectPosition" + (Utils.isCBXXXorLater("1.14") ? "Block" : "");

	private final Block block;
	private final BlockFace blockFace;

	MovingPosition(World world, Object rayTrace) throws ReflectiveOperationException {
		Object pos = PackageType.NMS.invokeMethod(rayTrace, KEY_MOP, KEY_BPS);
		int x = (int) PackageType.NMS.invokeMethod(pos, "BaseBlockPosition", "getX");
		int y = (int) PackageType.NMS.invokeMethod(pos, "BaseBlockPosition", "getY");
		int z = (int) PackageType.NMS.invokeMethod(pos, "BaseBlockPosition", "getZ");
		this.block = world.getBlockAt(x, y, z);
		this.blockFace = notchToBlockFace(getDirection(rayTrace));
	}

	public Block getHitBlock() {
		return block;
	}

	public BlockFace getBlockFace() {
		return blockFace;
	}

	private Enum<?> getDirection(Object rayTrace) throws ReflectiveOperationException {
		if (Utils.isCBXXXorLater("1.14")) {
			return (Enum<?>) PackageType.NMS.invokeMethod(rayTrace, KEY_MOP, "getDirection");
		}
		return (Enum<?>) PackageType.NMS.getField(KEY_MOP, "direction").get(rayTrace);
	}

	private BlockFace notchToBlockFace(Enum<?> direction) {
		if (direction == null) {
			return BlockFace.SELF;
		}
		switch (direction.ordinal()) {
			case 1:
				return BlockFace.DOWN;
			case 2:
				return BlockFace.UP;
			case 3:
				return BlockFace.NORTH;
			case 4:
				return BlockFace.SOUTH;
			case 5:
				return BlockFace.WEST;
			case 6:
				return BlockFace.EAST;
			default:
				return BlockFace.SELF;
		}
	}
}