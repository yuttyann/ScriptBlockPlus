package com.github.yuttyann.scriptblockplus.command;

import java.util.ArrayList;
import java.util.List;

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

public abstract class AbstractCommand extends CommandUsage implements TabExecutor {

	private Plugin plugin;
	private String commandName;
	private boolean isIgnoreUsage;

	public AbstractCommand(Plugin plugin, String commandName, CommandData... args) {
		this.plugin = plugin;
		this.commandName = commandName;
		addUsage(args);
	}

	public Plugin getPlugin() {
		return plugin;
	}

	public String getCommandName() {
		return commandName;
	}

	public boolean isIgnoreUsage() {
		return isIgnoreUsage;
	}

	@Override
	public final boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (Utils.isCB18orLater() && sender instanceof ProxiedCommandSender) {
			CommandSender pxSender = ((ProxiedCommandSender) sender).getCallee();
			if (pxSender instanceof Player) {
				sender = pxSender;
			}
		}
		try {
			if (!runCommand(sender, command, label, args) && !isIgnoreUsage) {
				sendUsage(plugin, sender, command);
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