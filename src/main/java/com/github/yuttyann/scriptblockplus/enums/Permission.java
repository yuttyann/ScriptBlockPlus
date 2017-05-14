package com.github.yuttyann.scriptblockplus.enums;

import org.bukkit.command.CommandSender;

public enum Permission {
	SCRIPTBLOCKPLUS_COMMAND_TOOL("scriptblockplus.command.tool"),
	SCRIPTBLOCKPLUS_COMMAND_RELOAD("scriptblockplus.command.reload"),
	SCRIPTBLOCKPLUS_COMMAND_CHECKVER("scriptblockplus.command.checkver"),
	SCRIPTBLOCKPLUS_COMMAND_DATAMIGR("scriptblockplus.command.datamigr"),
	SCRIPTBLOCKPLUS_COMMAND_INTERACT("scriptblockplus.command.interact"),
	SCRIPTBLOCKPLUS_COMMAND_BREAK("scriptblockplus.command.break"),
	SCRIPTBLOCKPLUS_COMMAND_WALK("scriptblockplus.command.walk"),
	SCRIPTBLOCKPLUS_COMMAND_WORLDEDIT("scriptblockplus.command.worldedit"),
	SCRIPTBLOCKPLUS_INTERACT_USE("scriptblockplus.interact.use"),
	SCRIPTBLOCKPLUS_BREAK_USE("scriptblockplus.break.use"),
	SCRIPTBLOCKPLUS_WALK_USE("scriptblockplus.walk.use"),
	SCRIPTBLOCKPLUS_TOOL_SCRIPTEDITOR("scriptblockplus.tool.scripteditor");

	private String node;

	private Permission(String node) {
		this.node = node;
	}

	public String getNode() {
		return node;
	}

	public static String getPermission(Permission permission) {
		return permission.getNode();
	}

	public static boolean has(Permission permission, CommandSender sender) {
		return sender.hasPermission(getPermission(permission));
	}

	public static boolean has(String permission, CommandSender sender) {
		return sender.hasPermission(permission);
	}
}