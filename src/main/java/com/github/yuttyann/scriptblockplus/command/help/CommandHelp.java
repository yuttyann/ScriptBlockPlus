package com.github.yuttyann.scriptblockplus.command.help;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.file.PluginYaml;

public class CommandHelp {

	public List<CommandData> putCommands(String commandName, CommandData... args) {
		String[] split = ScriptBlock.instance.getCommand(commandName).getUsage().split("/<command>");
		List<CommandData> datas = new ArrayList<CommandData>();
		CommandData temp;
		String temp2;
		for (int i = 0, j = 1, l = args.length; i < l; i++, j++) {
			temp = args[i];
			if (split.length > j && (temp2 = split[j].trim()).length() > 0) {
				temp = temp.setMessage(temp2);
			}
			datas.add(temp);
		}
		return ScriptBlock.instance.getCommandHelp().put(commandName.toLowerCase(), datas);
	}

	public List<CommandData> put(String commandName, CommandData... args) {
		List<CommandData> datas = new ArrayList<CommandData>();
		for (CommandData data : args) {
			datas.add(data);
		}
		return ScriptBlock.instance.getCommandHelp().put(commandName.toLowerCase(), datas);
	}

	public List<CommandData> get(int index) {
		return ScriptBlock.instance.getCommandHelp().get(index);
	}

	public List<CommandData> remove(String label) {
		return ScriptBlock.instance.getCommandHelp().remove(label);
	}

	public void clear() {
		ScriptBlock.instance.getCommandHelp().clear();
	}

	public static void sendHelpMessage(CommandSender sender, Command command) {
		String name = command.getName().toLowerCase();
		List<CommandData> temps = new ArrayList<CommandData>();
		for (CommandData data : ScriptBlock.instance.getCommandHelp().get(name)) {
			if (data.hasPermission() && sender.hasPermission(data.getPermission())) {
				temps.add(data);
			}
		}
		if (temps.isEmpty()) {
			sender.sendMessage("Unknown command. Type \"/help\" for help.");
			return;
		}
		StringBuilder builder = new StringBuilder();
		builder.append("§d==== ").append(PluginYaml.getName()).append(" Commands ====");
		sender.sendMessage(builder.toString());
		for (CommandData data : temps) {
			if (data.isHelp()) {
				builder.setLength(0);
				builder.append("§b/").append(name).append(" ").append(data.getMessage());
				sender.sendMessage(builder.toString());
			} else {
				sender.sendMessage(data.getMessage());
			}
		}
	}
}