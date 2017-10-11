package com.github.yuttyann.scriptblockplus.script.option.chat;

import org.bukkit.entity.Player;

import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;

public class Say extends BaseOption {

	public Say() {
		super("say", "@say ");
	}

	@Override
	public boolean isValid() {
		Player player = getPlayer();
		if (player.isOnline()) {
			String message = getOptionValue();
			message = StringUtils.replace(message, "<player>", player.getName());
			message = StringUtils.replace(message, "<dplayer>", player.getDisplayName());
			commandExecute(player, "say " + message, true);
		}
		return true;
	}
}