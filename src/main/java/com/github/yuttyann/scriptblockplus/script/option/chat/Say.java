package com.github.yuttyann.scriptblockplus.script.option.chat;

import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.script.option.Option;
import org.jetbrains.annotations.NotNull;

public class Say extends BaseOption {

	public Say() {
		super("say", "@say ");
	}

	@NotNull
	@Override
	public Option newInstance() {
		return new Say();
	}

	@Override
	protected boolean isValid() throws Exception {
		return executeCommand(getPlayer(), "say " + getOptionValue(), true);
	}
}