package com.github.yuttyann.scriptblockplus.util;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

public class BlockLocation {

	private World world;
	private Block block;
	private String coords;
	private String fullcoords;
	private Location location;
	private int x, y, z;

	public BlockLocation(Location location) {
		this.location = location;
		world = location.getWorld();
		block = location.getBlock();
		x = location.getBlockX();
		y = location.getBlockY();
		z = location.getBlockZ();
		coords = x + ", " + y + ", " + z;
		fullcoords = world.getName() + ", " + coords;
	}

	public BlockLocation(World world, int x, int y, int z) {
		location = new Location(world, x, y, z);
		this.world = world;
		this.block = location.getBlock();
		this.x = x;
		this.y = y;
		this.z = z;
		coords = x + ", " + y + ", " + z;
		fullcoords = world.getName() + ", " + coords;
	}

	public String getCoords(boolean isFull) {
		return isFull ? fullcoords : coords;
	}

	public Location getLocation() {
		return location;
	}

	public World getWorld() {
		return world;
	}

	public Block getBlock() {
		return block;
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
}
