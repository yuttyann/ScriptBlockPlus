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
		this.world = location.getWorld();
		this.block = location.getBlock();
		this.x = location.getBlockX();
		this.y = location.getBlockY();
		this.z = location.getBlockZ();
		this.coords = x + ", " + y + ", " + z;
		this.fullcoords = world.getName() + ", " + coords;
	}

	public BlockLocation(World world, int x, int y, int z) {
		this.location = new Location(world, x, y, z);
		this.world = world;
		this.block = location.getBlock();
		this.x = x;
		this.y = y;
		this.z = z;
		this.coords = x + ", " + y + ", " + z;
		this.fullcoords = world.getName() + ", " + coords;
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
