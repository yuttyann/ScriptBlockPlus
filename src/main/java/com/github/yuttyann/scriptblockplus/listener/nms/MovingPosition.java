package com.github.yuttyann.scriptblockplus.listener.nms;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.RayTraceResult;

import com.github.yuttyann.scriptblockplus.enums.reflection.PackageType;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public final class MovingPosition {

	private static final String KEY_MOP = "MovingObjectPosition";

	private final Block block;
	private final BlockFace blockFace;

	MovingPosition(World world, Object rayTrace) throws ReflectiveOperationException {
		String name = Utils.isCBXXXorLater("1.13") ? "getBlockPosition" : "a";
		Object blockPosition = PackageType.NMS.invokeMethod(rayTrace, null, name);
		int x = (int) PackageType.NMS.invokeMethod(blockPosition, null, "getX");
		int y = (int) PackageType.NMS.invokeMethod(blockPosition, null, "getY");
		int z = (int) PackageType.NMS.invokeMethod(blockPosition, null, "getZ");
		this.block = world.getBlockAt(x, y, z);

		Object face = PackageType.NMS.getField(KEY_MOP, "direction").get(rayTrace);
		this.blockFace = (BlockFace) PackageType.CB_BLOCK.invokeMethod(null, "CraftBlock", "notchToBlockFace", face);
	}

	MovingPosition(World world, RayTraceResult rayTrace) {
		this.block = rayTrace.getHitBlock();
		this.blockFace = rayTrace.getHitBlockFace();
	}

	public Block getBlock() {
		return block;
	}

	public BlockFace getFace() {
		return blockFace;
	}
}