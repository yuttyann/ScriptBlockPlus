package com.github.yuttyann.scriptblockplus.script.option.chat;

import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;

public class Console extends BaseOption {

	public Console() {
		super("console", "@console ");
	}

	@Override
	protected boolean isValid() throws Exception {
		return executeConsoleCommand(StringUtils.replaceColorCode(getOptionValue(), true));
	}
}