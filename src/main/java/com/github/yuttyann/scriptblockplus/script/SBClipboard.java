package com.github.yuttyann.scriptblockplus.script;

import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

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