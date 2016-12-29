package com.github.yuttyann.scriptblockplus.command.help;

import com.github.yuttyann.scriptblockplus.Permission;

public class CommandView {

	private String message;
	private String permission;
	private boolean isCommandHelp;

	public CommandView setAll(String message, String permission, boolean isCommandHelp) {
		this.message = message;
		this.permission = permission;
		this.isCommandHelp = isCommandHelp;
		return this;
	}

	public CommandView setAll(String message, Permission permission, boolean isCommandHelp) {
		this.message = message;
		this.permission = permission.getNode();
		this.isCommandHelp = isCommandHelp;
		return this;
	}

	public CommandView setMessage(String message, boolean isCommandHelp) {
		this.message = message;
		this.isCommandHelp = isCommandHelp;
		return this;
	}

	public CommandView setPermission(String permission) {
		this.permission = permission;
		return this;
	}

	public CommandView setPermission(Permission permission) {
		this.permission = permission.getNode();
		return this;
	}

	public String getMessage() {
		return message;
	}

	public String getPermission() {
		return permission;
	}

	public boolean isCommandHelp() {
		return isCommandHelp;
	}

	public boolean hasMessage() {
		return message != null;
	}

	public boolean hasPermission() {
		return permission != null;
	}
}
