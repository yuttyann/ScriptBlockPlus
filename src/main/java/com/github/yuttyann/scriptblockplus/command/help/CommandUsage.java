package com.github.yuttyann.scriptblockplus.command.help;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import com.github.yuttyann.scriptblockplus.file.SBConfig;
import com.github.yuttyann.scriptblockplus.utils.StreamUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public abstract class CommandUsage {

	private List<CommandData> usages;

	protected void addUsage(CommandData... args) {
		usages = new ArrayList<CommandData>(args.length);
		StreamUtils.forEach(args, usages::add);
	}

	protected void sendUsageMessage(Plugin plugin, CommandSender sender, Command command, boolean isAliases) {
		if (usages == null || usages.isEmpty()) {
			return;
		}
		List<CommandData> list = new ArrayList<CommandData>(usages.size());
		StreamUtils.filterForEach(usages, c -> c.hasPermission(sender), list::add);
		if (list.isEmpty()) {
			Utils.sendMessage(sender, SBConfig.getNotPermissionMessage());
			return;
		}
		String commandName = command.getName();
		if (isAliases && command.getAliases().size() > 0) {
			commandName = command.getAliases().get(0).toLowerCase();
		}
		sender.sendMessage("§d==== " + plugin.getName() + " Commands ====");
		String prefix = "§b/" + commandName + " ";
		StreamUtils.filterForEach(list, c -> c.hasMessage(), c -> sender.sendMessage(text(c, prefix)));
	}

	private String text(CommandData commandData, String prefix) {
		String message = commandData.getMessage();
		return commandData.isPrefix() ? prefix + message : message;
	}
}