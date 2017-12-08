package com.github.yuttyann.scriptblockplus.script.option.chat;

import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;

public class Command extends BaseOption {

	public Command() {
		super("command", "@command ");
	}

	@Override
	protected boolean isValid() throws Exception {
		String command = StringUtils.replaceColorCode(getOptionValue(), true);
		executeCommand(getPlayer(), command, false);
		return true;
	}
}