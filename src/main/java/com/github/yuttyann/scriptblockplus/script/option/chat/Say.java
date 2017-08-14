package com.github.yuttyann.scriptblockplus.script.option.chat;

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
			String message = optionData;
			message = StringUtils.replace(message, "<player>", player.getName());
			message = StringUtils.replace(message, "<dplayer>", player.getDisplayName());
			commandExec(player, "say " + message, true);
		}
		return true;
	}
}