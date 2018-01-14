package com.github.yuttyann.scriptblockplus;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public class BlockCoords extends Location {

	private String coords, fullCoords;
	private boolean isModified1, isModified2;

	public BlockCoords(Location location) {
		this(location.getWorld(), location.getX(), location.getY(), location.getZ());
	}

	public BlockCoords(World world, double x, double y, double z) {
		super(world, x, y, z);
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
		return coords == null || isModified(false) ? coords = getCoords(this) : coords;
	}

	public String getFullCoords() {
		return fullCoords == null || isModified(true) ? fullCoords = getFullCoords(this) : fullCoords;
	}

	private void setModified(boolean flag) {
		isModified1 = true;
		isModified2 = true;
	}

	private boolean isModified(boolean isFull) {
		return isFull ? isModified2 && !(isModified2 = false) : isModified1 && !(isModified1 = false);
	}

	public static String getCoords(Location location) {
		return location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ();
	}

	public static String getFullCoords(Location location) {
		return location.getWorld().getName() + ", " + getCoords(location);
	}

	public static BlockCoords fromString(String fullCoords) {
		String[] array = StringUtils.split(fullCoords, ", ");
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
	public BlockCoords clone() {
		BlockCoords blockCoords = new BlockCoords(this);
		blockCoords.coords = this.coords;
		blockCoords.fullCoords = this.fullCoords;
		blockCoords.isModified1 = this.isModified1;
		blockCoords.isModified2 = this.isModified2;
		return blockCoords;
	}

	public BlockCoords unmodifiable() {
		BlockCoords blockCoords = new UnmodifiableBlockCoords(this);
		blockCoords.coords = getCoords();
		blockCoords.fullCoords = getFullCoords();
		return blockCoords;
	}

	private class UnmodifiableBlockCoords extends BlockCoords {

		public UnmodifiableBlockCoords(BlockCoords blockCoords) {
			super(blockCoords);
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
}