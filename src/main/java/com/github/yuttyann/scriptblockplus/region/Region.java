package com.github.yuttyann.scriptblockplus.region;

import org.bukkit.Location;
import org.bukkit.World;

public interface Region {

	public World getWorld();

	public Location getMinimumPoint();

	public Location getMaximumPoint();

	public boolean hasPositions();
}