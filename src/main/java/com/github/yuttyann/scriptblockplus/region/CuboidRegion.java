package com.github.yuttyann.scriptblockplus.region;

import java.util.Objects;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

public class CuboidRegion implements Region {

	private World world;
	private Vector pos1;
	private Vector pos2;

	public void setWorld(World world) {
		if (!Objects.equals(this.world, world)) {
			this.pos1 = null;
			this.pos2 = null;
		}
		this.world = world;
	}

	public void setPos1(Vector pos1) {
		this.pos1 = pos1;
	}

	public void setPos2(Vector pos2) {
		this.pos2 = pos2;
	}

	@Override
	public World getWorld() {
		return world;
	}

	@Override
	public Location getMinimumPoint() {
		return toLocation(min(pos1.getX(), pos2.getX()), min(pos1.getY(), pos2.getY()), min(pos1.getZ(), pos2.getZ()));
	}

	@Override
	public Location getMaximumPoint() {
		return toLocation(max(pos1.getX(), pos2.getX()), max(pos1.getY(), pos2.getY()), max(pos1.getZ(), pos2.getZ()));
	}

	@Override
	public boolean hasPositions() {
		return world != null && pos1 != null && pos2 != null;
	}

	private Location toLocation(double x, double y, double z) {
		return new Location(world, x, y, z);
	}

	private double min(double a, double b) {
		return Math.min(a, b);
	}

	private double max(double a, double b) {
		return Math.max(a, b);
	}
}