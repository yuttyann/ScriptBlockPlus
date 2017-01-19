package com.github.yuttyann.scriptblockplus.enums;

import org.bukkit.command.CommandSender;

public enum Permission {
	SCRIPTBLOCKPLUS_COMMAND_TOOL("scriptblockplus.command.tool"),
	SCRIPTBLOCKPLUS_COMMAND_RELOAD("scriptblockplus.command.reload"),
	SCRIPTBLOCKPLUS_COMMAND_INTERACT("scriptblockplus.command.interact"),
	SCRIPTBLOCKPLUS_COMMAND_WALK("scriptblockplus.command.walk"),
	SCRIPTBLOCKPLUS_COMMAND_WORLDEDIT("scriptblockplus.command.worldedit"),
	SCRIPTBLOCKPLUS_INTERACT_USE("scriptblockplus.interact.use"),
	SCRIPTBLOCKPLUS_WALK_USE("scriptblockplus.walk.use"),
	SCRIPTBLOCKPLUS_TOOL_SCRIPTEDITOR("scriptblockplus.tool.scripteditor");

	public static final String COMMAND_PERM_PREFIX = "scriptblockplus.command.";

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

	public static Boolean has(Permission permission, CommandSender sender) {
		return sender.hasPermission(getPermission(permission));
	}

	public static Boolean has(String permission, CommandSender sender) {
		return sender.hasPermission(permission);
	}
}