package com.github.yuttyann.scriptblockplus.script;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

public interface SBClipboard {

	@NotNull
	ScriptType getScriptType();

	void save();

	boolean copy();

	boolean paste(@NotNull Location location, boolean overwrite);

	boolean lightPaste(@NotNull Location location, boolean overwrite);
}