package com.github.yuttyann.scriptblockplus.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ProxiedCommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.github.yuttyann.scriptblockplus.enums.Permission;
import com.github.yuttyann.scriptblockplus.file.SBConfig;
import com.github.yuttyann.scriptblockplus.utils.StreamUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public abstract class BaseCommand extends CommandUsage implements TabExecutor {

	private Plugin plugin;
	private boolean isIgnoreUsage;

	public BaseCommand(Plugin plugin) {
		this.plugin = plugin;
	}

	public final Plugin getPlugin() {
		return plugin;
	}

	public abstract String getCommandName();

	public abstract boolean isAliases();

	public final boolean isIgnoreUsage() {
		return isIgnoreUsage;
	}

	@Override
	public final boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof BlockCommandSender) {
			String senderName = ((BlockCommandSender) sender).getName();
			System.out.println(sender.getClass().getSimpleName() + " : " + senderName);
			Player player = Utils.getPlayer(senderName);
			if (player != null) {
				sender = player;
			}
		} else if (Utils.isCB183orLater() && sender instanceof ProxiedCommandSender) {
			CommandSender proxiedCommandSender = ((ProxiedCommandSender) sender).getCallee();
			if (proxiedCommandSender instanceof Player) {
				sender = proxiedCommandSender;
			}
		}
		try {
			if (!runCommand(sender, command, label, args) && !isIgnoreUsage) {
				sendUsage(plugin, sender, command, isAliases());
			}
		} finally {
			isIgnoreUsage = false;
		}
		return true;
	}

	@Override
	public final List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		List<String> completeList = new ArrayList<String>();
		tabComplete(sender, command, label, args, completeList);
		return completeList;
	}

	protected abstract boolean runCommand(CommandSender sender, Command command, String label, String[] args);

	protected abstract void tabComplete(CommandSender sender, Command command, String label, String[] args, List<String> emptyList);

	protected final boolean hasPermission(CommandSender sender, Permission permission) {
		return hasPermission(sender, permission, true);
	}

	protected final boolean hasPermission(CommandSender sender, Permission permission, boolean checkPlayer) {
		if (checkPlayer && !checkPlayer(sender)) {
			return false;
		}
		boolean has = permission.has(sender);
		if (!has) {
			isIgnoreUsage = true;
		}
		Utils.sendMessage(sender, has ? null : SBConfig.getNotPermissionMessage());
		return has;
	}

	protected final boolean checkPlayer(CommandSender sender) {
		if (!(sender instanceof Player)) {
			Utils.sendMessage(SBConfig.getSenderNoPlayerMessage());
			isIgnoreUsage = true;
			return false;
		}
		return true;
	}

	protected final boolean equals(String source, String another) {
		return another != null && another.equalsIgnoreCase(source);
	}

	protected final boolean equals(String source, String... anothers) {
		return StreamUtils.anyMatch(anothers, s -> equals(source, s));
	}
}