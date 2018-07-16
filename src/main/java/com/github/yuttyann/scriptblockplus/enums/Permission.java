package com.github.yuttyann.scriptblockplus.enums;

import org.bukkit.permissions.Permissible;

import com.github.yuttyann.scriptblockplus.utils.StringUtils;

public enum Permission {
	COMMAND_TOOL("scriptblockplus.command.tool"),
	COMMAND_RELOAD("scriptblockplus.command.reload"),
	COMMAND_BACKUP("scriptblockplus.command.backup"),
	COMMAND_CHECKVER("scriptblockplus.command.checkver"),
	COMMAND_DATAMIGR("scriptblockplus.command.datamigr"),
	/*
	COMMAND_INTERACT("scriptblockplus.command.interact"),
	COMMAND_BREAK("scriptblockplus.command.break"),
	COMMAND_WALK("scriptblockplus.command.walk"),
	*/
	COMMAND_WORLDEDIT("scriptblockplus.command.worldedit"),
	/*
	INTERACT_USE("scriptblockplus.interact.use"),
	BREAK_USE("scriptblockplus.break.use"),
	WALK_USE("scriptblockplus.walk.use"),
	*/
	TOOL_SCRIPTEDITOR("scriptblockplus.tool.scripteditor");

	private final String node;

	private Permission(String node) {
		this.node = node;
	}

	public String getNode() {
		return node;
	}

	@Override
	public String toString() {
		return node;
	}

	public boolean has(Permissible permissible) {
		return has(permissible, node);
	}

	public static boolean has(Permissible permissible, String permission) {
		return StringUtils.isNotEmpty(permission) ? permissible.hasPermission(permission) : false;
	}
}