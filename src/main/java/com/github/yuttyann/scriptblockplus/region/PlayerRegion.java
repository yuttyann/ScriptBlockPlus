package com.github.yuttyann.scriptblockplus.region;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlayerRegion implements Region {

	private World world;
	private int x;
	private int y;
	private int z;
	private int range;

	public PlayerRegion(@NotNull Player player, int range) {
		Location location = player.getLocation();
		this.world = location.getWorld();
		this.x = location.getBlockX();
		this.y = location.getBlockY();
		this.z = location.getBlockZ();
		this.range = range;
	}

	@Override
	@NotNull
	public World getWorld() {
		return world;
	}

	@Override
	@NotNull
	public String getName() {
		return world == null ? "null" : world.getName();
	}

	@Override
	@NotNull
	public Location getMinimumPoint() {
		return toLocation(x - range, y - range, z - range);
	}

	@Override
	@NotNull
	public Location getMaximumPoint() {
		return toLocation(x + range, y + range, z + range);
	}

	@Override
	@NotNull
	public boolean hasPositions() {
		return true;
	}

	@NotNull
	private Location toLocation(double x, double y, double z) {
		return new Location(world, x, y, z);
	}
}
