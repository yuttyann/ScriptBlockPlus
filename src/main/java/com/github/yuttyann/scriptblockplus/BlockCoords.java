package com.github.yuttyann.scriptblockplus;

import org.bukkit.Location;


public class BlockCoords {

	public static String getCoords(boolean isFull, Location location) {
		return isFull ? getFullCoords(location) : getCoords(location);
	}

	public static String getFullCoords(Location location) {
		return location.getWorld().getName() + ", " + getCoords(location);
	}

	public static String getCoords(Location location) {
		return location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ();
	}

	public static Location getAllCenter(Location location) {
		location = location.clone();
		location.setX(location.getBlockX() + 0.5D);
		location.setY(location.getBlockY() + 0.5D);
		location.setZ(location.getBlockZ() + 0.5D);
		return location;
	}

	public static Location getCenter(Location location) {
		location = location.clone();
		location.setX(location.getBlockX() + 0.5D);
		location.setZ(location.getBlockZ() + 0.5D);
		return location;
	}
}
