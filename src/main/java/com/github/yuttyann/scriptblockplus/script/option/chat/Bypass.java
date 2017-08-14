package com.github.yuttyann.scriptblockplus.script.option.chat;

import com.github.yuttyann.scriptblockplus.manager.ScriptManager;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;

public class Bypass extends BaseOption {

	public Bypass(ScriptManager scriptManager) {
		super(scriptManager, "bypass", "@bypass ");
	}

	@Override
	public boolean isValid() {
		if (player.isOnline()) {
			String command = optionData;
			command = StringUtils.replace(command, "<player>", player.getName());
			command = StringUtils.replace(command, "<dplayer>", player.getDisplayName());
			commandExec(player, command, true);
		}
		return true;
	}
}