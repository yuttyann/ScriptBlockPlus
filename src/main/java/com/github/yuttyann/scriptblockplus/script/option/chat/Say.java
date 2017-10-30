package com.github.yuttyann.scriptblockplus.script.option.chat;

import com.github.yuttyann.scriptblockplus.script.option.BaseOption;

public class Say extends BaseOption {

	public Say() {
		super("say", "@say ", 5);
	}

	@Override
	public boolean isValid() {
		commandExecute(getPlayer(), "say " + getOptionValue(), true);
		return true;
	}
}