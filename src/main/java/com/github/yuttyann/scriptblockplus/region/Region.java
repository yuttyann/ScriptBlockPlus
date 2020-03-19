package com.github.yuttyann.scriptblockplus.region;

import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Region {

	@Nullable
	public World getWorld();

	@Nullable
	public String getName();

	@NotNull
	public Location getMinimumPoint();

	@NotNull
	public Location getMaximumPoint();

	public boolean hasPositions();
}