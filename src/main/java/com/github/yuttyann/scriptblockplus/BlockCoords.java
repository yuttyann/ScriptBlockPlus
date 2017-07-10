package com.github.yuttyann.scriptblockplus;

import org.bukkit.Location;

public class BlockCoords extends Location {

	public BlockCoords(Location location) {
		super(location.getWorld(), location.getX(), location.getY(), location.getZ());
	}

	public Location getLocation() {
		return this;
	}

	public String getCoords() {
		return getBlockX() + ", " + getBlockY() + ", " + getBlockZ();
	}

	public String getFullCoords() {
		return getWorld().getName() + ", " + getCoords();
	}

	public Location getCenter() {
		Location location = clone();
		location = location.clone();
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
		return location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ();
	}

	public static String getFullCoords(Location location) {
		return location.getWorld().getName() + ", " + getCoords(location);
	}

	public static Location getCenter(Location location) {
		location = location.clone();
		location.setX(location.getBlockX() + 0.5D);
		location.setZ(location.getBlockZ() + 0.5D);
		return location;
	}

	public static Location getAllCenter(Location location) {
		location = location.clone();
		location.setX(location.getBlockX() + 0.5D);
		location.setY(location.getBlockY() + 0.5D);
		location.setZ(location.getBlockZ() + 0.5D);
		return location;
	}
}