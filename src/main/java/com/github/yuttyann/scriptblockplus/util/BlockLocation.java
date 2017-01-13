package com.github.yuttyann.scriptblockplus.util;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;

public class BlockLocation implements Cloneable, ConfigurationSerializable {

	private World world;
	private int radius;
	private double x;
	private double y;
	private double z;

	public BlockLocation(World world, double x, double y, double z) {
		this(world, 0, x, y, z);
	}

	public BlockLocation(World world, int radius, double x, double y, double z) {
		this.world = world;
		this.radius = radius;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public static BlockLocation fromLocation(Location location) {
		return new BlockLocation(location.getWorld(), 0, location.getX(), location.getY(), location.getZ());
	}

	public static BlockLocation fromLocation(Location location, int radius) {
		return new BlockLocation(location.getWorld(), radius, location.getX(), location.getY(), location.getZ());
	}

	public String getCoords(boolean isFull) {
		return isFull ? getFullCoords() : getCoords();
	}

	public String getFullCoords() {
		return world.getName() + ", " + getCoords();
	}

	public String getCoords() {
		return getBlockX() + ", " + getBlockY() + ", " + getBlockZ();
	}

	public World getWorld() {
		return world;
	}

	public Block getBlock() {
		return world.getBlockAt(getBlockX(), getBlockY(), getBlockZ());
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}

	public int getRadius() {
		return radius;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getX() {
		return x;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getY() {
		return y;
	}

	public void setZ(double z) {
		this.z = z;
	}

	public double getZ() {
		return z;
	}

	public int getBlockX() {
		return NumberConversions.floor(x);
	}

	public int getBlockY() {
		return NumberConversions.floor(y);
	}

	public int getBlockZ() {
		return NumberConversions.floor(z);
	}

	public BlockLocation getMaximum() {
		return clone().add(radius, radius, radius);
	}

	public BlockLocation getMinimum() {
		return clone().subtract(radius, radius, radius);
	}

	public BlockLocation add(BlockLocation blockLocation) {
		if (blockLocation == null || blockLocation.getWorld() != this.world) {
			throw new IllegalArgumentException("Cannot add Locations of differing worlds");
		}
		add(blockLocation.getX(), blockLocation.getY(), blockLocation.getZ());
		return this;
	}

	public BlockLocation add(Location location) {
		if (location == null || location.getWorld() != this.world) {
			throw new IllegalArgumentException("Cannot add Locations of differing worlds");
		}
		add(location.getX(), location.getY(), location.getZ());
		return this;
	}

	public BlockLocation add(Vector vector) {
		add(vector.getX(), vector.getY(), vector.getZ());
		return this;
	}

	public BlockLocation add(double x, double y, double z) {
		setX(this.x + x);
		setY(this.y + y);
		setZ(this.z + z);
		return this;
	}

	public BlockLocation subtract(BlockLocation blockLocation) {
		if (blockLocation == null || blockLocation.getWorld() != this.world) {
			throw new IllegalArgumentException("Cannot add Locations of differing worlds");
		}
		subtract(blockLocation.getX(), blockLocation.getY(), blockLocation.getZ());
		return this;
	}

	public BlockLocation subtract(Location location) {
		if (location == null || location.getWorld() != this.world) {
			throw new IllegalArgumentException("Cannot add Locations of differing worlds");
		}
		subtract(location.getX(), location.getY(), location.getZ());
		return this;
	}

	public BlockLocation subtract(Vector vector) {
		subtract(vector.getX(), vector.getY(), vector.getZ());
		return this;
	}

	public BlockLocation subtract(double x, double y, double z) {
		setX(this.x - x);
		setY(this.y - y);
		setZ(this.z - z);
		return this;
	}

	public BlockLocation multiply(double m) {
		setX(x * m);
		setY(y * m);
		setZ(z * m);
		return this;
	}

	public BlockLocation divide(double d) {
		setX(x / d);
		setY(y / d);
		setZ(z / d);
		return this;
	}

	public BlockLocation reset() {
		setX(0.0D);
		setY(0.0D);
		setZ(0.0D);
		return this;
	}

	public double length() {
		return Math.sqrt(NumberConversions.square(x) + NumberConversions.square(y) + NumberConversions.square(z));
	}

	public double lengthSquared() {
		return NumberConversions.square(x) + NumberConversions.square(y) + NumberConversions.square(z);
	}

	public double distance(BlockLocation location) {
		return Math.sqrt(distanceSquared(location));
	}

	public double distance(Location location) {
		return Math.sqrt(distanceSquared(location));
	}

	private double distanceSquared(Object object) {
		if (object == null) {
			throw new IllegalArgumentException("Cannot measure distance to a null location");
		}
		BlockLocation location;
		if (object instanceof Location) {
			location = fromLocation((Location) object, radius);
		} else if (object instanceof BlockLocation) {
			location = (BlockLocation) object;
		} else {
			return 0.0D;
		}
		World world = location.getWorld();
		if (world == null || this.world == null) {
			throw new IllegalArgumentException("Cannot measure distance to a null world");
		}
		if (world != this.world) {
			throw new IllegalArgumentException("Cannot measure distance between " + this.world.getName() + " and " + world.getName());
		}
		return NumberConversions.square(x - location.getX()) + NumberConversions.square(y - location.getY()) + NumberConversions.square(z - location.getZ());
	}

	@Override
	public boolean equals(Object object) {
		if (object == null || getClass() != object.getClass()) {
			return false;
		}
		Location location = (Location) object;
		World world = location.getWorld();
		if (this.world != world && (this.world == null || !this.world.equals(world))) {
			return false;
		}
		if (Double.doubleToLongBits(x) != Double.doubleToLongBits(location.getX())) {
			return false;
		}
		if (Double.doubleToLongBits(y) != Double.doubleToLongBits(location.getY())) {
			return false;
		}
		if (Double.doubleToLongBits(z) != Double.doubleToLongBits(location.getZ())) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 19 * hash + (world != null ? world.hashCode() : 0);
		hash = 19 * hash + (int) (Double.doubleToLongBits(x) ^ Double.doubleToLongBits(x) >>> 32);
		hash = 19 * hash + (int) (Double.doubleToLongBits(y) ^ Double.doubleToLongBits(y) >>> 32);
		hash = 19 * hash + (int) (Double.doubleToLongBits(z) ^ Double.doubleToLongBits(z) >>> 32);
		return hash;
	}

	public Location toLocation() {
		return new Location(world, x, y, z);
	}

	public Vector toVector() {
		return new Vector(x, y, z);
	}

	@Override
	public String toString() {
		return "BlockLocation{world=" + world.getName() + ", radius=" + radius + ", x=" + x + ", y=" + y + ", z=" + z + "}";
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
		data.put("radius", radius);
		data.put("x", Double.valueOf(x));
		data.put("y", Double.valueOf(y));
		data.put("z", Double.valueOf(z));
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