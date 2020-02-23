package com.github.yuttyann.scriptblockplus.script;

import org.bukkit.Location;

import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface SBClipboard {

	public void save();

	@NotNull
	public Location getLocation();

	@NotNull
	public ScriptType getScriptType();

	public boolean copy(@NotNull SBPlayer sbPlayer);

	public boolean paste(@NotNull Location location, boolean overwrite);

	public boolean lightPaste(@NotNull Location location, boolean overwrite, boolean updateTime);
}