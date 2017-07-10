package com.github.yuttyann.scriptblockplus.script.option.chat;

import org.bukkit.entity.Player;

import com.github.yuttyann.scriptblockplus.manager.ScriptManager;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;

public class Say extends BaseOption {

	public Say(ScriptManager scriptManager) {
		super(scriptManager, "say", "@say ");
	}

	@Override
	public boolean isValid() {
		if (player.isOnline()) {
			commandExec(player, "say " + replace(player, optionData), true);
		}
		return true;
	}

	private String replace(Player player, String text) {
		text = StringUtils.replace(text, "<player>", player.getName());
		text = StringUtils.replace(text, "<dplayer>", player.getDisplayName());
		return text;
	}
}