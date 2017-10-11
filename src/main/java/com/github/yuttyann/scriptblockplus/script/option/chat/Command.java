package com.github.yuttyann.scriptblockplus.script.option.chat;

import org.bukkit.entity.Player;

import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;

public class Command extends BaseOption {

	public Command() {
		super("command", "@command /");
	}

	@Override
	public boolean isValid() {
		Player player = getPlayer();
		if (player.isOnline()) {
			String command = getOptionValue();
			command = StringUtils.replace(command, "<player>", player.getName());
			command = StringUtils.replace(command, "<dplayer>", player.getDisplayName());
			commandExecute(player, command, false);
		}
		return true;
	}
}