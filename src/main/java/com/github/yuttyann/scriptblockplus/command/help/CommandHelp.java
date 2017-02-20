package com.github.yuttyann.scriptblockplus.command.help;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.file.PluginYaml;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;

public class CommandHelp {

	public List<CommandData> putCommands(String commandName, CommandData... args) {
		List<CommandData> datas = new ArrayList<CommandData>();
		String[] array = StringUtils.split(ScriptBlock.getInstance().getCommand(commandName).getUsage(), "/<command>");
		for (int i = 0, j = 1, l = args.length; i < l; i++, j++) {
			CommandData temp = args[i];
			if (array.length > j && array[j].length() > 0) {
				temp = temp.setMessage(array[j].trim());
			}
			datas.add(temp);
		}
		return ScriptBlock.getInstance().getCommandHelp().put(commandName.toLowerCase(), datas);
	}

	public List<CommandData> put(String commandName, CommandData... args) {
		List<CommandData> datas = new ArrayList<CommandData>();
		for (CommandData data : args) {
			datas.add(data);
		}
		return ScriptBlock.getInstance().getCommandHelp().put(commandName.toLowerCase(), datas);
	}

	public List<CommandData> get(int index) {
		return ScriptBlock.getInstance().getCommandHelp().get(index);
	}

	public List<CommandData> remove(String label) {
		return ScriptBlock.getInstance().getCommandHelp().remove(label);
	}

	public void clear() {
		ScriptBlock.getInstance().getCommandHelp().clear();
	}

	public static void sendHelpMessage(CommandSender sender, Command command, boolean isName) {
		List<CommandData> temps = new ArrayList<CommandData>();
		String commandName = command.getName();
		for (CommandData data : ScriptBlock.getInstance().getCommandHelp().get(commandName)) {
			if (data.hasPermission(sender)) {
				temps.add(data);
			}
		}
		if (temps.isEmpty()) {
			sender.sendMessage("Unknown command. Type \"/help\" for help.");
			return;
		}
		if (!isName && command.getAliases().size() > 0) {
			commandName = command.getAliases().get(0).toLowerCase();
		}
		sender.sendMessage("§d==== " + PluginYaml.getName() + " Commands ====");
		for (CommandData data : temps) {
			if (data.isHelp()) {
				sender.sendMessage("§b/" + commandName + " " + data.getMessage());
			} else {
				sender.sendMessage(data.getMessage());
			}
		}
	}
}