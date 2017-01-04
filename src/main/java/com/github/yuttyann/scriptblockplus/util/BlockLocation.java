package com.github.yuttyann.scriptblockplus.util;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

public class BlockLocation implements Cloneable, ConfigurationSerializable {

	private World world;
	private Block block;
	private double x;
	private double y;
	private double z;
	private int blockX;
	private int blockY;
	private int blockZ;
	private String coords;
	private String fullcoords;

	public BlockLocation(Location location) {
		this(location.getWorld(), location.getX(), location.getY(), location.getZ());
	}

	public BlockLocation(World world, double x, double y, double z) {
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
		this.blockX = Location.locToBlock(x);
		this.blockY = Location.locToBlock(y);
		this.blockZ = Location.locToBlock(z);
		this.block = world.getBlockAt(blockX, blockY, blockZ);
		this.coords = blockX + ", " + blockY + ", " + blockZ;
		this.fullcoords = world.getName() + ", " + coords;
	}

	public String getCoords(boolean isFull) {
		return isFull ? fullcoords : coords;
	}

	public World getWorld() {
		return world;
	}

	public Block getBlock() {
		return block;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getZ() {
		return z;
	}

	public int getBlockX() {
		return blockX;
	}

	public int getBlockY() {
		return blockY;
	}

	public int getBlockZ() {
		return blockZ;
	}

	public Location toLocation() {
		return new Location(world, x, y, z);
	}

	@Override
	public String toString() {
		return "BlockLocation{world=" + world + ",x=" + x + ",y=" + y + ",z=" + z + "}";
	}

	public BlockLocation clone() {
		try {
			return (BlockLocation) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new Error(e);
		}
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("world", world.getName());
		data.put("x", Double.valueOf(x));
		data.put("y", Double.valueOf(y));
		data.put("z", Double.valueOf(z));
		return data;
	}
}