package com.github.yuttyann.scriptblockplus.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import com.github.yuttyann.scriptblockplus.file.SBConfig;
import com.github.yuttyann.scriptblockplus.utils.StreamUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import org.jetbrains.annotations.Nullable;

public abstract class CommandUsage {

	private final List<CommandData> usages = new ArrayList<CommandData>();

	public final void setUsage(@Nullable CommandData... args) {
		if (args == null) {
			return;
		}
		usages.clear();
		StreamUtils.forEach(args, usages::add);
	}

	public final void addUsage(@Nullable CommandData... args) {
		if (args == null) {
			return;
		}
		StreamUtils.forEach(args, usages::add);
	}

	protected final void sendUsage(@NotNull BaseCommand baseCommand, @NotNull CommandSender sender, @NotNull Command command) {
		if (usages.isEmpty()) {
			return;
		}
		List<CommandData> list = new ArrayList<>(usages.size());
		StreamUtils.fForEach(usages, c -> c.hasPermission(sender), list::add);
		if (list.isEmpty()) {
			Utils.sendMessage(sender, SBConfig.getNotPermissionMessage());
			return;
		}
		String commandName = command.getName();
		if (baseCommand.isAliases() && command.getAliases().size() > 0) {
			commandName = command.getAliases().get(0).toLowerCase();
		}
		sender.sendMessage("§d========== " + baseCommand.getCommandName() + " Commands ==========");
		String prefix = "§b/" + commandName + " ";
		StreamUtils.fForEach(list, c -> c.hasMessage(), c -> sender.sendMessage(text(c, prefix)));
	}

	private String text(@NotNull CommandData commandData, @NotNull String prefix) {
		return commandData.isPrefix() ? prefix + commandData.getMessage() : commandData.getMessage();
	}
}