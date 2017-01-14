package com.github.yuttyann.scriptblockplus.command.help;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.file.PluginYaml;

public class CommandHelp {

	public CommandView getView() {
		return new CommandView();
	}

	public List<CommandView> put(String label, CommandView... args) {
		List<CommandView> views = new ArrayList<CommandView>();
		for (CommandView view : args) {
			views.add(view);
		}
		return ScriptBlock.instance.getCommandHelp().put(label.toLowerCase(), views);
	}

	public List<CommandView> get(int index) {
		return ScriptBlock.instance.getCommandHelp().get(index);
	}

	public List<CommandView> remove(String label) {
		return ScriptBlock.instance.getCommandHelp().remove(label);
	}

	public void clear() {
		ScriptBlock.instance.getCommandHelp().clear();
	}

	public static void sendHelpMessage(CommandSender sender, Command command) {
		String name = command.getName().toLowerCase();
		List<CommandView> views = ScriptBlock.instance.getCommandHelp().get(name);
		int viewcount = 0;
		for (CommandView view : views) {
			if (view.hasPermission() && !sender.hasPermission(view.getPermission())) {
				views.remove(view.getMessage());
				continue;
			} else {
				viewcount++;
			}
		}
		if (!(viewcount > 0)) {
			sender.sendMessage("Unknown command. Type \"/help\" for help.");
			return;
		}
		StringBuilder builder = new StringBuilder();
		builder.append("§d==== ").append(PluginYaml.getName()).append(" Commands ====");
		sender.sendMessage(builder.toString());
		for (CommandView view : views) {
			if (view.isHelp()) {
				builder.setLength(0);
				builder.append("§b/").append(name).append(" ").append(view.getMessage());
				sender.sendMessage(builder.toString());
			} else {
				sender.sendMessage(view.getMessage());
			}
		}
	}
}
