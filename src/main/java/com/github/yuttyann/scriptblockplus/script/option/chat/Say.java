package com.github.yuttyann.scriptblockplus.script.option.chat;

import com.github.yuttyann.scriptblockplus.script.option.BaseOption;

public class Say extends BaseOption {

	public Say() {
		super("say", "@say ");
	}

	@Override
	protected boolean isValid() throws Exception {
		executeCommand(getPlayer(), "say " + getOptionValue(), true);
		return true;
	}
}