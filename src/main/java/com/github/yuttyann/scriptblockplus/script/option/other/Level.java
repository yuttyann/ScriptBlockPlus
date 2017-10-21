package com.github.yuttyann.scriptblockplus.script.option.other;

import com.github.yuttyann.scriptblockplus.file.SBConfig;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public class Level extends BaseOption {

	public Level() {
		super("level", "@level:", 7);
	}

	@Override
	public boolean isValid() {
		String[] array = StringUtils.split(getOptionValue(), "/");
		int playerLevel = getPlayer().getLevel();
		int requiredLevel = Integer.parseInt(array[1]);

		boolean result = false;
		switch (array[0].toUpperCase()) {
		case "UP":
			result = playerLevel >= requiredLevel;
			if (!result) {
				Utils.sendMessage(getPlayer(), SBConfig.getErrorUPLevelMessage(playerLevel, requiredLevel));
			}
			return result;
		case "UNDER":
			result = playerLevel <= requiredLevel;
			if (!result) {
				Utils.sendMessage(getPlayer(), SBConfig.getErrorUNDERLevelMessage(playerLevel, requiredLevel));
			}
			return result;
		case "EQUAL":
			result = playerLevel == requiredLevel;
			if (!result) {
				Utils.sendMessage(getPlayer(), SBConfig.getErrorEQUALLevelMessage(playerLevel, requiredLevel));
			}
			return result;
		default:
			return result;
		}
	}
}