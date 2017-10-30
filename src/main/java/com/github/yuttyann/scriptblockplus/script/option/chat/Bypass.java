package com.github.yuttyann.scriptblockplus.script.option.chat;

import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;

public class Bypass extends BaseOption {

	public Bypass() {
		super("bypass", "@bypass ", 1);
	}

	@Override
	public boolean isValid() {
		String command = getOptionValue();
		command = StringUtils.replaceRandomColor(command, "&rc");
		command = StringUtils.replace(command, "&", "§");
		commandExecute(getPlayer(), command, true);
		return true;
	}
}