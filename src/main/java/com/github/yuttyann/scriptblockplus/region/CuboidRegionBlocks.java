package com.github.yuttyann.scriptblockplus.region;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

public final class CuboidRegionBlocks {

	private final World world;
	private final Location min;
	private final Location max;

	private int count = -1;

	public CuboidRegionBlocks(@NotNull Region region) {
		this.world = Objects.requireNonNull(region.getWorld());
		this.min = Objects.requireNonNull(region.getMinimumPoint());
		this.max = Objects.requireNonNull(region.getMaximumPoint());
	}

	@NotNull
	public World getWorld() {
		return world;
	}

	@NotNull
	public Location getMinimumPoint() {
		return BlockCoords.unmodifiableLocation(min);
	}

	@NotNull
	public Location getMaximumPoint() {
		return BlockCoords.unmodifiableLocation(max);
	}

	public int getCount() {
		if (count == -1) {
			int[] count = { 0 };
			forEach(b -> count[0]++);
			this.count = count[0];
		}
		return count;
	}

	@NotNull
	public Set<Block> getBlocks() {
		Set<Block> set = new HashSet<>();
		forEach(b -> set.add(b.getBlock(world)));
		return set;
	}

	public void forEach(@NotNull Consumer<BlockPos> action) {
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

		@NotNull
		public Block getBlock(@NotNull World world) {
			return world.getBlockAt(x, y, z);
		}
	}
}