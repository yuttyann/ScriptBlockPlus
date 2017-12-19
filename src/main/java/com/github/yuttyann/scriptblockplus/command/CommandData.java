package com.github.yuttyann.scriptblockplus.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

import com.github.yuttyann.scriptblockplus.enums.Permission;
import com.github.yuttyann.scriptblockplus.utils.StreamUtils;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;

public final class CommandData {

	private String message;
	private boolean prefix;
	private List<Permission> permissions;

	public CommandData(String message, boolean isPrefix) {
		this(message, isPrefix, (Permission[]) null);
	}

	public CommandData(String message, Permission... permission) {
		this(message, true, permission);
	}

	public CommandData(String message, boolean isPrefix, Permission... permission) {
		setMessage(message);
		setIsPrefix(isPrefix);
		addPermission(permission);
	}

	public CommandData setMessage(String message) {
		this.message = message;
		return this;
	}

	public CommandData setIsPrefix(boolean isPrefix) {
		this.prefix = isPrefix;
		return this;
	}

	public CommandData addPermission(Permission... permission) {
		if (permission != null && permission.length > 0) {
			if (permissions == null) {
				permissions = new ArrayList<Permission>(permission.length);
			}
			StreamUtils.forEach(permission, permissions::add);
		}
		return this;
	}

	public String getMessage() {
		return message;
	}

	public boolean isPrefix() {
		return prefix;
	}

	public List<Permission> getPermissions() {
		return permissions;
	}

	public boolean hasMessage() {
		return StringUtils.isNotEmpty(message);
	}

	public boolean hasPermission(CommandSender sender) {
		if (permissions == null || permissions.size() == 0) {
			return true;
		}
		return StreamUtils.anyMatch(permissions, s -> s.has(sender));
	}
}