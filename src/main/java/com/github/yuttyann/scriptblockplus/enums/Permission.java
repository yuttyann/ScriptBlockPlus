package com.github.yuttyann.scriptblockplus.enums;

import org.bukkit.permissions.Permissible;

import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import org.jetbrains.annotations.NotNull;

public enum Permission {
	COMMAND_TOOL("scriptblockplus.command.tool"),
	COMMAND_RELOAD("scriptblockplus.command.reload"),
	COMMAND_BACKUP("scriptblockplus.command.backup"),
	COMMAND_CHECKVER("scriptblockplus.command.checkver"),
	COMMAND_DATAMIGR("scriptblockplus.command.datamigr"),
	COMMAND_EXPORT("scriptblockplus.command.export"),
	/*
	COMMAND_INTERACT("scriptblockplus.command.interact"),
	COMMAND_BREAK("scriptblockplus.command.break"),
	COMMAND_WALK("scriptblockplus.command.walk"),
	*/
	COMMAND_SELECTOR("scriptblockplus.command.selector"),
	/*
	INTERACT_USE("scriptblockplus.interact.use"),
	BREAK_USE("scriptblockplus.break.use"),
	WALK_USE("scriptblockplus.walk.use"),
	*/
	TOOL_BLOCKSELECTOR("scriptblockplus.tool.blockselector"),
	TOOL_SCRIPTEDITOR("scriptblockplus.tool.scripteditor");

	private final String node;

	private Permission(@NotNull String node) {
		this.node = node;
	}

	@NotNull
	public String getNode() {
		return node;
	}

	@Override
	public String toString() {
		return node;
	}

	public boolean has(@NotNull Permissible permissible) {
		return has(permissible, node);
	}

	public static boolean has(@NotNull Permissible permissible, @NotNull String permission) {
		return StringUtils.isNotEmpty(permission) && permissible.hasPermission(permission);
	}
}