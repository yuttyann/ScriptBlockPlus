package com.github.yuttyann.scriptblockplus.script;

import org.bukkit.Location;

import com.github.yuttyann.scriptblockplus.player.SBPlayer;

public interface SBClipboard {

	public void save();

	public ScriptType getScriptType();

	public boolean copy(SBPlayer sbPlayer);

	public boolean paste(Location location, boolean overwrite);

	public boolean lightPaste(Location location, boolean overwrite, boolean updateTime);
}