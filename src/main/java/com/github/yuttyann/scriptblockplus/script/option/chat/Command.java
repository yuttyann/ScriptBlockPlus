package com.github.yuttyann.scriptblockplus.script.option.chat;

import org.bukkit.entity.Player;

import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;

public class Command extends BaseOption {

	public Command() {
		super("command", "@command ", 0);
	}

	@Override
	public boolean isValid() {
		Player player = getPlayer();
		String command = getOptionValue();
		command = StringUtils.replace(command, "<player>", player.getName());
		command = StringUtils.replaceRandomColor(command, "&rc");
		command = StringUtils.replace(command, "&", "§");
		commandExecute(player, command, false);
		return true;
	}
}