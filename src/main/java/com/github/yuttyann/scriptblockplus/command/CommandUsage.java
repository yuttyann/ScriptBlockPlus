package com.github.yuttyann.scriptblockplus.command;

import com.github.yuttyann.scriptblockplus.file.config.SBConfig;
import com.github.yuttyann.scriptblockplus.utils.StreamUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * ScriptBlockPlus CommandUsage クラス
 * @author yuttyann44581
 */
public abstract class CommandUsage {

	private final List<CommandData> usages = new ArrayList<CommandData>();

	public final void setUsage(@NotNull CommandData... args) {
		usages.clear();
		StreamUtils.forEach(args, usages::add);
	}

	public final void addUsage(@NotNull CommandData... args) {
		StreamUtils.forEach(args, usages::add);
	}

	protected final void sendUsage(@NotNull BaseCommand baseCommand, @NotNull CommandSender sender, @NotNull Command command) {
		if (usages.isEmpty()) {
			return;
		}
		List<CommandData> list = new ArrayList<>(usages.size());
		StreamUtils.fForEach(usages, c -> c.hasPermission(sender), list::add);
		if (list.isEmpty()) {
			SBConfig.NOT_PERMISSION.send(sender);
			return;
		}
		String commandName = command.getName();
		if (baseCommand.isAliases() && command.getAliases().size() > 0) {
			commandName = command.getAliases().get(0).toLowerCase();
		}
		sender.sendMessage("§d========== " + baseCommand.getCommandName() + " Commands ==========");
		String prefix = "§b/" + commandName + " ";
		StreamUtils.fForEach(list, CommandData::hasMessage, c -> sender.sendMessage(text(c, prefix)));
	}

	private String text(@NotNull CommandData commandData, @NotNull String prefix) {
		return commandData.isPrefix() ? prefix + commandData.getMessage() : commandData.getMessage();
	}
}