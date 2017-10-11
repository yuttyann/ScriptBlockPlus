package com.github.yuttyann.scriptblockplus.command.help;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.file.SBConfig;
import com.github.yuttyann.scriptblockplus.utils.StreamUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public abstract class CommandHelp {

	protected void put(String commandName, CommandData... args) {
		List<CommandData> commands = new ArrayList<CommandData>();
		Arrays.stream(args).forEach(commands::add);
		getHelp().put(commandName.toLowerCase(), commands);
	}

	protected void sendHelpMessage(Plugin plugin, CommandSender sender, Command command, boolean isAliases) {
		String commandName = command.getName();
		if (getHelp().containsKey(commandName)) {
			List<CommandData> help = getHelp().get(commandName);
			List<CommandData> commands = new ArrayList<CommandData>();
			StreamUtils.filterForEach(help, c -> c.hasPermission(sender), commands::add);
			if (commands.isEmpty()) {
				Utils.sendMessage(sender, SBConfig.getNotPermissionMessage());
				return;
			}
			if (isAliases && command.getAliases().size() > 0) {
				commandName = command.getAliases().get(0).toLowerCase();
			}
			sender.sendMessage("§d==== " + plugin.getName() + " Commands ====");
			String prefix = "§b/" + commandName + " ";
			StreamUtils.filterForEach(commands, c -> c.hasMessage(), c -> sender.sendMessage(helpText(c, prefix)));
		}
	}

	private String helpText(CommandData commandData, String prefix) {
		String message = commandData.getMessage();
		return commandData.isPrefix() ? prefix + message : message;
	}

	private Map<String, List<CommandData>> getHelp() {
		return ScriptBlock.getInstance().getCommandHelp();
	}
}