package com.github.yuttyann.scriptblockplus;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;

import com.github.yuttyann.scriptblockplus.utils.Utils;

public class BlockLocation extends Location {

	private int radius;

	public BlockLocation(World world, double x, double y, double z) {
		this(world, 0, x, y, z);
	}

	public BlockLocation(World world, int radius, double x, double y, double z) {
		super(world, x, y, z);
		this.radius = radius;
	}

	public static BlockLocation fromLocation(Location location) {
		return new BlockLocation(location.getWorld(), location.getX(), location.getY(), location.getZ());
	}

	public static BlockLocation fromLocation(Location location, int radius) {
		return new BlockLocation(location.getWorld(), radius, location.getX(), location.getY(), location.getZ());
	}

	public String getCoords(boolean isFull) {
		return isFull ? getFullCoords() : getCoords();
	}

	public String getFullCoords() {
		return getWorld().getName() + ", " + getCoords();
	}

	public String getCoords() {
		return getBlockX() + ", " + getBlockY() + ", " + getBlockZ();
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}

	public int getRadius() {
		return radius;
	}

	public void setXYZ(double x, double y, double z) {
		setX(x);
		setY(y);
		setZ(z);
	}

	@Override
	@Deprecated
	public void setYaw(float yaw) {
		super.setYaw(yaw);
	}

	@Override
	@Deprecated
	public float getYaw() {
		return super.getYaw();
	}

	@Override
	@Deprecated
	public void setPitch(float pitch) {
		super.setPitch(pitch);
	}

	@Override
	@Deprecated
	public float getPitch() {
		return super.getPitch();
	}

	@Override
	@Deprecated
	public Vector getDirection() {
		return super.getDirection();
	}

	@Override
	@Deprecated
	public Location setDirection(Vector vector) {
		return super.setDirection(vector);
	}

	public BlockLocation getAllCenter() {
		BlockLocation location = clone();
		location.setX(location.getBlockX() + 0.5D);
		location.setY(location.getBlockY() + 0.5D);
		location.setZ(location.getBlockZ() + 0.5D);
		return location;
	}

	public BlockLocation getCenter() {
		BlockLocation location = clone();
		location.setX(location.getBlockX() + 0.5D);
		location.setZ(location.getBlockZ() + 0.5D);
		return location;
	}

	public BlockLocation getMaximum() {
		return clone().add(radius, radius, radius);
	}

	public BlockLocation getMinimum() {
		return clone().subtract(radius, radius, radius);
	}

	@Override
	public BlockLocation add(Location location) {
		if (location == null || location.getWorld() != getWorld()) {
			throw new IllegalArgumentException("Cannot add Locations of differing worlds");
		}
		add(location.getX(), location.getY(), location.getZ());
		return this;
	}

	@Override
	public BlockLocation add(Vector vector) {
		add(vector.getX(), vector.getY(), vector.getZ());
		return this;
	}

	@Override
	public BlockLocation add(double x, double y, double z) {
		super.add(x, y, z);
		return this;
	}

	@Override
	public BlockLocation subtract(Location location) {
		if (location == null || location.getWorld() != getWorld()) {
			throw new IllegalArgumentException("Cannot add Locations of differing worlds");
		}
		subtract(location.getX(), location.getY(), location.getZ());
		return this;
	}

	@Override
	public BlockLocation subtract(Vector vector) {
		subtract(vector.getX(), vector.getY(), vector.getZ());
		return this;
	}

	@Override
	public BlockLocation subtract(double x, double y, double z) {
		super.subtract(x, y, z);
		return this;
	}

	@Override
	public BlockLocation multiply(double m) {
		super.multiply(m);
		return this;
	}

	public BlockLocation divide(double d) {
		setX(getX() / d);
		setY(getY() / d);
		setZ(getZ() / d);
		return this;
	}

	@Override
	public BlockLocation zero() {
		setX(0.0D);
		setY(0.0D);
		setZ(0.0D);
		return this;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		Location location = (Location) obj;
		World world = location.getWorld();
		if (getWorld() != world && (getWorld() == null || !getWorld().equals(world))) {
			return false;
		}
		if (Double.doubleToLongBits(getX()) != Double.doubleToLongBits(location.getX())) {
			return false;
		}
		if (Double.doubleToLongBits(getY()) != Double.doubleToLongBits(location.getY())) {
			return false;
		}
		if (Double.doubleToLongBits(getZ()) != Double.doubleToLongBits(location.getZ())) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 19 * hash + (getWorld() != null ? getWorld().hashCode() : 0);
		hash = 19 * hash + (int) (Double.doubleToLongBits(getX()) ^ Double.doubleToLongBits(getX()) >>> 32);
		hash = 19 * hash + (int) (Double.doubleToLongBits(getY()) ^ Double.doubleToLongBits(getY()) >>> 32);
		hash = 19 * hash + (int) (Double.doubleToLongBits(getZ()) ^ Double.doubleToLongBits(getZ()) >>> 32);
		return hash;
	}

	@Override
	public String toString() {
		return "BlockLocation{world=" + getWorld().getName() + ", radius=" + radius + ", x=" + getBlockX() + ", y=" + getBlockY() + ", z=" + getBlockZ() + "}";
	}

	@Override
	public BlockLocation clone() {
		return new BlockLocation(getWorld(), radius, getX(), getY(), getZ());
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("world", getWorld().getName());
		data.put("radius", radius);
		data.put("x", getX());
		data.put("y", getY());
		data.put("z", getZ());
		return data;
	}

	public static BlockLocation deserialize(Map<String, Object> args) {
		World world = Utils.getWorld(args.get("world").toString());
		if (world == null) {
			throw new IllegalArgumentException("unknown world");
		}
		int radius = NumberConversions.toInt(args.get("radius"));
		double x = NumberConversions.toDouble(args.get("x"));
		double y = NumberConversions.toDouble(args.get("y"));
		double z = NumberConversions.toDouble(args.get("z"));
		return new BlockLocation(world, radius, x, y, z);
	}
}