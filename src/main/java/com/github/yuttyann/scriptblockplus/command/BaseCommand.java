package com.github.yuttyann.scriptblockplus.command;

import com.github.yuttyann.scriptblockplus.enums.Permission;
import com.github.yuttyann.scriptblockplus.file.SBConfig;
import com.github.yuttyann.scriptblockplus.utils.StreamUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ProxiedCommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseCommand extends CommandUsage implements TabExecutor {

	private Plugin plugin;
	private boolean isIgnoreUsage;

	public BaseCommand(@NotNull Plugin plugin) {
		this.plugin = plugin;
		setUsage(getUsages());
	}

	@NotNull
	public final Plugin getPlugin() {
		return plugin;
	}

	@NotNull
	public abstract String getCommandName();

	@Nullable
	public abstract CommandData[] getUsages();

	public abstract boolean isAliases();

	public final boolean isIgnoreUsage() {
		return isIgnoreUsage;
	}

	@Override
	public final boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		if (Utils.isCBXXXorLater("1.8.3") && sender instanceof ProxiedCommandSender) {
			CommandSender proxiedCommandSender = ((ProxiedCommandSender) sender).getCallee();
			if (proxiedCommandSender instanceof Player) {
				sender = proxiedCommandSender;
			}
		}
		try {
			if (!runCommand(sender, command, label, args) && !isIgnoreUsage) {
				sendUsage(this, sender, command);
			}
		} finally {
			isIgnoreUsage = false;
		}
		return true;
	}

	@Override
	@NotNull
	public final List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		List<String> completeList = new ArrayList<String>();
		tabComplete(sender, command, label, args, completeList);
		return completeList;
	}

	protected abstract boolean runCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args);

	protected abstract void tabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args, @NotNull List<String> empty);

	protected final boolean hasPermission(@NotNull CommandSender sender, @NotNull Permission permission) {
		return hasPermission(sender, permission, true);
	}

	protected final boolean hasPermission(@NotNull CommandSender sender, @NotNull Permission permission, boolean isPlayer) {
		if (isPlayer && !isPlayer(sender)) {
			return false;
		}
		boolean has = permission.has(sender);
		if (!has) {
			isIgnoreUsage = true;
			SBConfig.NOT_PERMISSION.send(sender, true);
		}
		return has;
	}

	protected final boolean isPlayer(@Nullable CommandSender sender) {
		if (sender instanceof Player) {
			return true;
		}
		SBConfig.SENDER_NO_PLAYER.send(sender, true);
		isIgnoreUsage = true;
		return false;
	}

	protected final boolean equals(@NotNull String source, @NotNull String another) {
		return another.equalsIgnoreCase(source);
	}

	protected final boolean equals(@NotNull String source, @NotNull String... anothers) {
		return StreamUtils.anyMatch(anothers, s -> equals(source, s));
	}
}