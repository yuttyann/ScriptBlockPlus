package com.github.yuttyann.scriptblockplus.command.help;

import com.github.yuttyann.scriptblockplus.enums.Permission;

public class CommandData {

	private String message;
	private String permission;
	private boolean isHelp;

	public CommandData(boolean isHelp) {
		this(null, null, isHelp);
	}

	public CommandData(Object permission) {
		this(null, permission, true);
	}

	public CommandData(String message, boolean isHelp) {
		this(message, null, isHelp);
	}

	public CommandData(String message, Object permission) {
		this(message, permission, true);
	}

	public CommandData(String message, Object permission, boolean isHelp) {
		setMessage(message);
		setPermission(permission);
		setHelp(isHelp);
	}

	public CommandData setMessage(String message) {
		this.message = message;
		return this;
	}

	public CommandData setPermission(Object permission) {
		if (permission == null) {
			return this;
		}
		this.permission = (permission instanceof Permission ? ((Permission) permission).getNode() : permission.toString());
		return this;
	}

	public CommandData setHelp(boolean isHelp) {
		this.isHelp = isHelp;
		return this;
	}

	public String getMessage() {
		return message;
	}

	public String getPermission() {
		return permission;
	}

	public boolean isHelp() {
		return isHelp;
	}

	public boolean hasMessage() {
		return message != null;
	}

	public boolean hasPermission() {
		return permission != null;
	}
}