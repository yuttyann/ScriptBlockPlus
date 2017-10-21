package com.github.yuttyann.scriptblockplus;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

public class BlockCoords extends Location implements Cloneable {

	private String coords;
	private String fullCoords;
	private boolean isModified;

	public BlockCoords(Location location) {
		super(location.getWorld(), location.getX(), location.getY(), location.getZ());
	}

	@Override
	public void setWorld(World world) {
		isModified = true;
		super.setWorld(world);
	}

	@Override
	public void setX(double x) {
		isModified = true;
		super.setX(x);
	}

	@Override
	public void setY(double y) {
		isModified = true;
		super.setY(y);
	}

	@Override
	public void setZ(double z) {
		isModified = true;
		super.setZ(z);
	}

	@Override
	public void setYaw(float yaw) {
		isModified = true;
		super.setYaw(yaw);
	}

	@Override
	public void setPitch(float pitch) {
		isModified = true;
		super.setPitch(pitch);
	}

	@Override
	public BlockCoords setDirection(Vector vector) {
		isModified = true;
		return (BlockCoords) super.setDirection(vector);
	}

	@Override
	public BlockCoords add(Location vec) {
		isModified = true;
		return (BlockCoords) super.add(vec);
	}

	@Override
	public BlockCoords add(Vector vec) {
		isModified = true;
		return (BlockCoords) super.add(vec);
	}

	@Override
	public BlockCoords add(double x, double y, double z) {
		isModified = true;
		return (BlockCoords) super.add(x, y, z);
	}

	@Override
	public BlockCoords subtract(Location vec) {
		isModified = true;
		return (BlockCoords) super.subtract(vec);
	}

	@Override
	public BlockCoords subtract(Vector vec) {
		isModified = true;
		return (BlockCoords) super.subtract(vec);
	}

	@Override
	public BlockCoords subtract(double x, double y, double z) {
		isModified = true;
		return (BlockCoords) super.subtract(x, y, z);
	}

	public BlockCoords multiply(double m) {
		isModified = true;
		return (BlockCoords) super.multiply(m);
	}

	public BlockCoords zero() {
		isModified = true;
		return (BlockCoords) super.zero();
	}

	public String getCoords() {
		if (coords == null || isModified) {
			isModified = false;
			coords = getCoords(this);
		}
		return coords;
	}

	public String getFullCoords() {
		if (fullCoords == null || isModified) {
			isModified = false;
			fullCoords = getFullCoords(this);
		}
		return fullCoords;
	}

	public BlockCoords getCenter() {
		return getCenter(this);
	}

	public BlockCoords getAllCenter() {
		return getAllCenter(this);
	}

	public BlockCoords clone() {
		BlockCoords blockCoords = new BlockCoords(this);
		blockCoords.coords = this.coords;
		blockCoords.fullCoords = this.fullCoords;
		blockCoords.isModified = this.isModified;
		return blockCoords;
	}

	public static String getCoords(Location location) {
		return location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ();
	}

	public static String getFullCoords(Location location) {
		return location.getWorld().getName() + ", " + getCoords(location);
	}

	public static BlockCoords getCenter(Location location) {
		BlockCoords blockCoords = new BlockCoords(location);
		blockCoords.setX(blockCoords.getBlockX() + 0.5D);
		blockCoords.setZ(blockCoords.getBlockZ() + 0.5D);
		return blockCoords;
	}

	public static BlockCoords getAllCenter(Location location) {
		BlockCoords blockCoords = new BlockCoords(location);
		blockCoords.setX(blockCoords.getBlockX() + 0.5D);
		blockCoords.setY(blockCoords.getBlockY() + 0.5D);
		blockCoords.setZ(blockCoords.getBlockZ() + 0.5D);
		return blockCoords;
	}
}