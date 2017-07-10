package com.github.yuttyann.scriptblockplus.script.option.chat;

import org.bukkit.entity.Player;

import com.github.yuttyann.scriptblockplus.manager.ScriptManager;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;

public class Command extends BaseOption {

	public Command(ScriptManager scriptManager) {
		super(scriptManager, "command", "@command ");
	}

	@Override
	public boolean isValid() {
		if (player.isOnline()) {
			commandExec(player, replace(player, optionData), false);
		}
		return true;
	}

	private String replace(Player player, String text) {
		text = StringUtils.replace(text, "<player>", player.getName());
		text = StringUtils.replace(text, "<dplayer>", player.getDisplayName());
		return text;
	}
}