package com.github.yuttyann.scriptblockplus.script.option.chat;

import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.script.option.Option;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;

public class Command extends BaseOption {

	public Command() {
		super("command", "@command ");
	}

	@Override
	public Option newInstance() {
		return new Command();
	}

	@Override
	protected boolean isValid() throws Exception {
		return executeCommand(getPlayer(), StringUtils.replaceColorCode(getOptionValue(), true), false);
	}
}