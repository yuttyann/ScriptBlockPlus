package com.github.yuttyann.scriptblockplus.region;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import com.github.yuttyann.scriptblockplus.BlockCoords;

public final class CuboidRegionBlocks {

	private final World world;
	private final Location min;
	private final Location max;

	public CuboidRegionBlocks(Region region) {
		this.world = Objects.requireNonNull(region.getWorld());
		this.min = Objects.requireNonNull(region.getMinimumPoint());
		this.max = Objects.requireNonNull(region.getMaximumPoint());
	}

	public World getWorld() {
		return world;
	}

	public Location getMinimumPoint() {
		return BlockCoords.unmodifiableLocation(min);
	}

	public Location getMaximumPoint() {
		return BlockCoords.unmodifiableLocation(max);
	}

	public int getCount() {
		int[] count = { 0 };
		forEach(b -> count[0]++);
		return count[0];
	}

	public Set<Block> getBlocks() {
		Set<Block> set = new HashSet<>();
		forEach(b -> set.add(b.getBlock(world)));
		return set;
	}

	public void forEach(Consumer<BlockPos> action) {
		BlockPos position = new BlockPos();
		for (int x = min.getBlockX(); x <= max.getBlockX(); x++) {
			for (int y = min.getBlockY(); y <= max.getBlockY(); y++) {
				for (int z = min.getBlockZ(); z <= max.getBlockZ(); z++) {
					action.accept(position.setPos(x, y, z));
				}
			}
		}
	}

	public final class BlockPos {

		private int x;
		private int y;
		private int z;

		private BlockPos setPos(int x, int y, int z) {
			this.x = x;
			this.y = y;
			this.z = z;
			return this;
		}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}

		public int getZ() {
			return z;
		}

		public Block getBlock(World world) {
			return world.getBlockAt(x, y, z);
		}
	}
}