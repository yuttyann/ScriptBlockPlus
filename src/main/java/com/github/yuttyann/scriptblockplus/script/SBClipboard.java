package com.github.yuttyann.scriptblockplus.script;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

public interface SBClipboard {

	@NotNull
	public ScriptType getScriptType();

	public void save();

	public boolean copy();

	public boolean paste(@NotNull Location location, boolean overwrite);

	public boolean lightPaste(@NotNull Location location, boolean overwrite);
}