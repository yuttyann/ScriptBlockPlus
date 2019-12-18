package com.github.yuttyann.scriptblockplus;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Utility;
import org.bukkit.World;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public class BlockCoords extends Location {

	private String coords, fullCoords;

	public BlockCoords(@NotNull final Location location) {
		this(location.getWorld(), location.getX(), location.getY(), location.getZ());
	}

	public BlockCoords(@Nullable final World world, final double x, final double y, final double z) {
		super(world, x, y, z);
	}

	@Override
	public void setWorld(@Nullable World world) {
		super.setWorld(world);
	}

	@Override
	public void setX(double x) {
		super.setX(x);
	}

	@Override
	public void setY(double y) {
		super.setY(y);
	}

	@Override
	public void setZ(double z) {
		super.setZ(z);
	}

	@Override
	public void setYaw(float yaw) {
		super.setYaw(yaw);
	}

	@Override
	public void setPitch(float pitch) {
		super.setPitch(pitch);
	}

	@Override
	@NotNull
	public BlockCoords setDirection(@NotNull Vector vector) {
		return (BlockCoords) super.setDirection(vector);
	}

	@Override
	@NotNull
	public BlockCoords add(@NotNull Location vec) {
		return (BlockCoords) super.add(vec);
	}

	@Override
	@NotNull
	public BlockCoords add(@NotNull Vector vec) {
		return (BlockCoords) super.add(vec);
	}

	@Override
	@NotNull
	public BlockCoords add(double x, double y, double z) {
		return (BlockCoords) super.add(x, y, z);
	}

	@Override
	@NotNull
	public BlockCoords subtract(@NotNull Location vec) {
		return (BlockCoords) super.subtract(vec);
	}

	@Override
	@NotNull
	public BlockCoords subtract(@NotNull Vector vec) {
		return (BlockCoords) super.subtract(vec);
	}

	@Override
	@NotNull
	public BlockCoords subtract(double x, double y, double z) {
		return (BlockCoords) super.subtract(x, y, z);
	}

	@Override
	@NotNull
	public BlockCoords multiply(double m) {
		return (BlockCoords) super.multiply(m);
	}

	@Override
	@NotNull
	public BlockCoords zero() {
		return (BlockCoords) super.zero();
	}

	@NotNull
	public String getCoords() {
		return getCoords(this);
	}

	@NotNull
	public String getFullCoords() {
		return getFullCoords(this);
	}

	@NotNull
	public static String getCoords(@NotNull Location location) {
		return location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ();
	}

	@NotNull
	public static String getFullCoords(@NotNull Location location) {
		return location.getWorld().getName() + ", " + getCoords(location);
	}

	@NotNull
	public static BlockCoords fromString(@NotNull World world, @NotNull String coords) {
		String[] array = StringUtils.split(coords, ",");
		if (array.length != 3) {
			throw new IllegalArgumentException();
		}
		double x = Double.parseDouble(array[1]);
		double y = Double.parseDouble(array[2]);
		double z = Double.parseDouble(array[3]);
		return new BlockCoords(world, x, y, z);
	}

	@NotNull
	public static BlockCoords fromString(@NotNull String fullCoords) {
		String[] array = StringUtils.split(fullCoords, ",");
		if (array.length != 4) {
			throw new IllegalArgumentException();
		}
		World world = Utils.getWorld(array[0]);
		double x = Double.parseDouble(array[1]);
		double y = Double.parseDouble(array[2]);
		double z = Double.parseDouble(array[3]);
		return new BlockCoords(world, x, y, z);
	}

	@Override
	@NotNull
	public BlockCoords clone() {
		BlockCoords blockCoords = new BlockCoords(this);
		blockCoords.coords = this.coords;
		blockCoords.fullCoords = this.fullCoords;
		return blockCoords;
	}

	@NotNull
	public BlockCoords unmodifiable() {
		BlockCoords blockCoords = new UnmodifiableBlockCoords(this);
		blockCoords.coords = getCoords();
		blockCoords.fullCoords = getFullCoords();
		return blockCoords;
	}

	@NotNull
	public static Location unmodifiableLocation(@NotNull Location location) {
		return new UnmodifiableBlockCoords(location);
	}

	private static class UnmodifiableBlockCoords extends BlockCoords {

		private UnmodifiableBlockCoords(Location location) {
			super(location);
		}

		@Override
		public void setWorld(World world) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void setX(double x) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void setY(double y) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void setZ(double z) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void setYaw(float yaw) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void setPitch(float pitch) {
			throw new UnsupportedOperationException();
		}

		@Override
		public BlockCoords setDirection(Vector vector) {
			throw new UnsupportedOperationException();
		}

		@Override
		public BlockCoords add(Location vec) {
			throw new UnsupportedOperationException();
		}

		@Override
		public BlockCoords add(Vector vec) {
			throw new UnsupportedOperationException();
		}

		@Override
		public BlockCoords add(double x, double y, double z) {
			throw new UnsupportedOperationException();
		}

		@Override
		public BlockCoords subtract(Location vec) {
			throw new UnsupportedOperationException();
		}

		@Override
		public BlockCoords subtract(Vector vec) {
			throw new UnsupportedOperationException();
		}

		@Override
		public BlockCoords subtract(double x, double y, double z) {
			throw new UnsupportedOperationException();
		}

		@Override
		public BlockCoords multiply(double m) {
			throw new UnsupportedOperationException();
		}

		@Override
		public BlockCoords zero() {
			throw new UnsupportedOperationException();
		}

		@Override
		public BlockCoords clone() {
			return unmodifiable();
		}
	}

	@Override
	@Utility
	@NotNull
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("world", getWorld().getName());
		map.put("x", getX());
		map.put("y", getY());
		map.put("z", getZ());
		map.put("coords", getCoords());
		map.put("fullcoords", getFullCoords());
		return map;
	}

	@NotNull
	public static Location deserialize(@NotNull Map<String, Object> args) {
		World world = Utils.getWorld((String) args.get("world"));
		if (world == null) {
			throw new IllegalArgumentException("unknown world");
		}
		double x = (double) args.get("x");
		double y = (double) args.get("y");
		double z = (double) args.get("z");
		BlockCoords blockCoords = new BlockCoords(world, x, y, z);
		blockCoords.coords = (String) args.get("coords");
		blockCoords.fullCoords = (String) args.get("fullcoords");
		return blockCoords;
	}
}