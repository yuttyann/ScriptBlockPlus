package com.github.yuttyann.scriptblockplus.script.option.chat;

import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;

public class Command extends BaseOption {

	public Command() {
		super("command", "@command ", 0);
	}

	@Override
	public boolean isValid() {
		String command = getOptionValue();
		command = StringUtils.replaceRandomColor(command, "&rc");
		command = StringUtils.replace(command, "&", "§");
		commandExecute(getPlayer(), command, false);
		return true;
	}
}