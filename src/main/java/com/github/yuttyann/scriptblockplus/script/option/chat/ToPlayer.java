package com.github.yuttyann.scriptblockplus.script.option.chat;

import com.github.yuttyann.scriptblockplus.manager.ScriptManager;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public class ToPlayer extends BaseOption {

	public ToPlayer(ScriptManager scriptManager) {
		super(scriptManager, "toplayer", "@player ");
	}

	@Override
	public boolean isValid() {
		if (player.isOnline()) {
			String message = optionData;
			message = StringUtils.replace(message, "&rc", Utils.getRandomColor());
			message = StringUtils.replace(message, "&", "§");
			player.sendMessage(message);
		}
		return true;
	}
}