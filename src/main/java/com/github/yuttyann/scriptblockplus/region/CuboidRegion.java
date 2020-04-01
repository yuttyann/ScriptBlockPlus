package com.github.yuttyann.scriptblockplus.region;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * ScriptBlockPlus CuboidRegion クラス
 * @author yuttyann44581
 */
public class CuboidRegion implements Region {

	private World world;
	private Vector pos1;
	private Vector pos2;

	public void setWorld(@Nullable World world) {
		if (this.world != null && !Objects.equals(this.world, world)) {
			setPos1(null);
			setPos2(null);
		}
		this.world = world;
	}

	public void setPos1(@Nullable Vector pos1) {
		this.pos1 = pos1;
	}

	public void setPos2(@Nullable Vector pos2) {
		this.pos2 = pos2;
	}

	@Nullable
	@Override
	public World getWorld() {
		return world;
	}

	@NotNull
	@Override
	public String getName() {
		return world == null ? "null" : world.getName();
	}

	@NotNull
	@Override
	public Location getMinimumPoint() {
		return toLocation(min(pos1.getX(), pos2.getX()), min(pos1.getY(), pos2.getY()), min(pos1.getZ(), pos2.getZ()));
	}

	@NotNull
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