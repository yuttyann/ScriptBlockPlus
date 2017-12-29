package com.github.yuttyann.scriptblockplus;

import java.util.Arrays;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

public class BlockCoords extends Location implements Cloneable {

	private String coords, fullCoords;
	private BlockCoords center, allCenter;
	private boolean[] isModified = new boolean[4];

	public BlockCoords(Location location) {
		super(location.getWorld(), location.getX(), location.getY(), location.getZ());
	}

	@Override
	public void setWorld(World world) {
		setModified(true);
		super.setWorld(world);
	}

	@Override
	public void setX(double x) {
		setModified(true);
		super.setX(x);
	}

	@Override
	public void setY(double y) {
		setModified(true);
		super.setY(y);
	}

	@Override
	public void setZ(double z) {
		setModified(true);
		super.setZ(z);
	}

	@Override
	public void setYaw(float yaw) {
		setModified(true);
		super.setYaw(yaw);
	}

	@Override
	public void setPitch(float pitch) {
		setModified(true);
		super.setPitch(pitch);
	}

	@Override
	public BlockCoords setDirection(Vector vector) {
		setModified(true);
		return (BlockCoords) super.setDirection(vector);
	}

	@Override
	public BlockCoords add(Location vec) {
		setModified(true);
		return (BlockCoords) super.add(vec);
	}

	@Override
	public BlockCoords add(Vector vec) {
		setModified(true);
		return (BlockCoords) super.add(vec);
	}

	@Override
	public BlockCoords add(double x, double y, double z) {
		setModified(true);
		return (BlockCoords) super.add(x, y, z);
	}

	@Override
	public BlockCoords subtract(Location vec) {
		setModified(true);
		return (BlockCoords) super.subtract(vec);
	}

	@Override
	public BlockCoords subtract(Vector vec) {
		setModified(true);
		return (BlockCoords) super.subtract(vec);
	}

	@Override
	public BlockCoords subtract(double x, double y, double z) {
		setModified(true);
		return (BlockCoords) super.subtract(x, y, z);
	}

	public BlockCoords multiply(double m) {
		setModified(true);
		return (BlockCoords) super.multiply(m);
	}

	public BlockCoords zero() {
		setModified(true);
		return (BlockCoords) super.zero();
	}

	public String getCoords() {
		return coords == null || isModified(0) ? coords = getCoords(this) : coords;
	}

	public String getFullCoords() {
		return fullCoords == null || isModified(1) ? fullCoords = getFullCoords(this) : fullCoords;
	}

	public BlockCoords getCenter() {
		return center == null || isModified(2) ? center = getCenter(this) : center;
	}

	public BlockCoords getAllCenter() {
		return allCenter == null || isModified(3) ? allCenter = getAllCenter(this) : allCenter;
	}

	private void setModified(boolean flag) {
		Arrays.fill(isModified, flag);
	}

	private boolean isModified(int id) {
		if (id < 0 || id >= isModified.length) {
			return false;
		}
		return isModified[id] && !(isModified[id] = false);
	}

	@Override
	public BlockCoords clone() {
		BlockCoords blockCoords = new BlockCoords(this);
		blockCoords.coords = this.coords;
		blockCoords.fullCoords = this.fullCoords;
		blockCoords.setModified(true);
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