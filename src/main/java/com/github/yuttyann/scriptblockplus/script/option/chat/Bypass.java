package com.github.yuttyann.scriptblockplus.script.option.chat;

import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;

public class Bypass extends BaseOption {

	public Bypass() {
		super("bypass", "@bypass ");
	}

	@Override
	public boolean isValid() throws Exception {
		String command = StringUtils.replaceColorCode(getOptionValue(), true);
		commandExecute(getPlayer(), command, true);
		return true;
	}
}