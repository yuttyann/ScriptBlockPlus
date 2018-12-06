package com.github.yuttyann.scriptblockplus.script.option.chat;

import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.script.option.Option;

public class Say extends BaseOption {

	public Say() {
		super("say", "@say ");
	}

	@Override
	public Option newInstance() {
		return new Say();
	}

	@Override
	protected boolean isValid() throws Exception {
		return executeCommand(getPlayer(), "say " + getOptionValue(), true);
	}
}