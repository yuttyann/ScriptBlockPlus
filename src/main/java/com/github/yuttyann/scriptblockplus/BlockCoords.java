package com.github.yuttyann.scriptblockplus;

import org.bukkit.Location;

public class BlockCoords extends Location {

	public String coords;
	public String fullCoords;

	public BlockCoords(Location location) {
		super(location.getWorld(), location.getX(), location.getY(), location.getZ());
		this.coords = getBlockX() + ", " + getBlockY() + ", " + getBlockZ();
		this.fullCoords = getWorld().getName() + ", " + coords;
	}

	public String getCoords() {
		return coords;
	}

	public String getFullCoords() {
		return fullCoords;
	}

	public Location getCenter() {
		Location location = clone();
		location.setX(location.getBlockX() + 0.5D);
		location.setZ(location.getBlockZ() + 0.5D);
		return location;
	}

	public Location getAllCenter() {
		Location location = clone();
		location.setX(location.getBlockX() + 0.5D);
		location.setY(location.getBlockY() + 0.5D);
		location.setZ(location.getBlockZ() + 0.5D);
		return location;
	}

	public static String getCoords(Location location) {
		return new BlockCoords(location).getCoords();
	}

	public static String getFullCoords(Location location) {
		return new BlockCoords(location).getFullCoords();
	}

	public static Location getCenter(Location location) {
		return new BlockCoords(location).getCenter();
	}

	public static Location getAllCenter(Location location) {
		return new BlockCoords(location).getAllCenter();
	}
}