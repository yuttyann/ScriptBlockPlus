package com.github.yuttyann.scriptblockplus.region;

import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Region {

	@Nullable
	World getWorld();

	@Nullable
	String getName();

	@NotNull
	Location getMinimumPoint();

	@NotNull
	Location getMaximumPoint();

	boolean hasPositions();
}