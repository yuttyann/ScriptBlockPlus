package com.github.yuttyann.scriptblockplus.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.github.yuttyann.scriptblockplus.enums.Permission;
import com.github.yuttyann.scriptblockplus.utils.StreamUtils;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;

public class CommandData {

	private String message;
	private boolean isPrefix;

	private List<String> permissions = new ArrayList<>();

	public CommandData(@Nullable String message, boolean isPrefix) {
		this(message, isPrefix, (String) null);
	}

	public CommandData(@Nullable String message, @Nullable String... permission) {
		this(message, true, permission);
	}

	public CommandData(@Nullable String message, boolean isPrefix, @Nullable String... permission) {
		setMessage(message);
		setPrefix(isPrefix);
		addPermission(permission);
	}

	@NotNull
	public CommandData setMessage(@Nullable String message) {
		this.message = message;
		return this;
	}

	@NotNull
	public CommandData setPrefix(boolean isPrefix) {
		this.isPrefix = isPrefix;
		return this;
	}

	@NotNull
	public CommandData addPermission(@Nullable String... permission) {
		if (permission != null && permission.length > 0) {
			StreamUtils.forEach(permission, permissions::add);
		}
		return this;
	}

	@NotNull
	public String getMessage() {
		return message;
	}

	@NotNull
	public List<String> getPermissions() {
		return permissions;
	}

	public boolean isPrefix() {
		return isPrefix;
	}

	public final boolean hasMessage() {
		return StringUtils.isNotEmpty(message);
	}

	public boolean hasPermission(@NotNull CommandSender sender) {
		if (permissions == null || permissions.size() == 0) {
			return true;
		}
		return StreamUtils.anyMatch(permissions, s -> Permission.has(sender, s));
	}
}