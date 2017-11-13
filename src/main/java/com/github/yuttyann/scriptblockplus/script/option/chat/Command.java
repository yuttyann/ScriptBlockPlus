package com.github.yuttyann.scriptblockplus.script.option.chat;

import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;

public class Command extends BaseOption {

	public Command() {
		super("command", "@command ");
	}

	@Override
	public boolean isValid() throws Exception {
		String command = StringUtils.replaceColorCode(getOptionValue(), true);
		commandExecute(getPlayer(), command, false);
		return true;
	}
}