package com.github.yuttyann.scriptblockplus.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ProxiedCommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.Updater;
import com.github.yuttyann.scriptblockplus.command.help.CommandData;
import com.github.yuttyann.scriptblockplus.command.help.CommandUsage;
import com.github.yuttyann.scriptblockplus.enums.Permission;
import com.github.yuttyann.scriptblockplus.file.SBConfig;
import com.github.yuttyann.scriptblockplus.manager.MapManager;
import com.github.yuttyann.scriptblockplus.utils.StreamUtils;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public abstract class BaseCommand extends CommandUsage implements TabExecutor {

	private ScriptBlock plugin;
	private String commandName;
	private boolean isAliases;
	private boolean isIgnoreHelp;

	public BaseCommand(ScriptBlock plugin, String commandName, CommandData... args) {
		this(plugin, commandName, false, args);
	}

	public BaseCommand(ScriptBlock plugin, String commandName, boolean isAliases, CommandData... args) {
		this.plugin = plugin;
		this.commandName = commandName;
		this.isAliases = isAliases;
		addUsage(args);
	}

	public Plugin getPlugin() {
		return plugin;
	}

	public Updater getUpdater() {
		return plugin.getUpdater();
	}

	public MapManager getMapManager() {
		return plugin.getMapManager();
	}

	public String getCommandName() {
		return commandName;
	}

	public boolean isAliases() {
		return isAliases;
	}

	public boolean isIgnoreHelp() {
		return isIgnoreHelp;
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
			if (!runCommand(sender, label, args) && !isIgnoreHelp) {
				sendUsageMessage(plugin, sender, command, isAliases);
			}
		} finally {
			isIgnoreHelp = false;
		}
		return true;
	}

	@Override
	public final List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		return tabComplete(sender, label, args, new ArrayList<String>());
	}

	protected abstract boolean runCommand(CommandSender sender, String label, String[] args);

	protected abstract List<String> tabComplete(CommandSender sender, String label, String[] args, List<String> emptyList);

	protected String permString(CommandSender sender, Permission permission, String source) {
		return StringUtils.isNotEmpty(permission.getNode()) && permission.has(sender) ? source : null;
	}

	protected boolean hasPermission(CommandSender sender, Permission permission) {
		return hasPermission(sender, permission, true);
	}

	protected boolean hasPermission(CommandSender sender, Permission permission, boolean checkPlayer) {
		if (checkPlayer && !checkPlayer(sender)) {
			return false;
		}
		boolean has = permission.has(sender);
		if (!has) {
			isIgnoreHelp = true;
		}
		Utils.sendMessage(sender, has ? null : SBConfig.getNotPermissionMessage());
		return has;
	}

	protected boolean checkPlayer(CommandSender sender) {
		if (!(sender instanceof Player)) {
			Utils.sendMessage(SBConfig.getSenderNoPlayerMessage());
			isIgnoreHelp = true;
			return false;
		}
		return true;
	}

	protected boolean equals(String source, String... anothers) {
		return StreamUtils.anyMatch(anothers, s -> s != null && s.equalsIgnoreCase(source));
	}
}