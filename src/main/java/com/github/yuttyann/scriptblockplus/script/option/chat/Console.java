package com.github.yuttyann.scriptblockplus.script.option.chat;

import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.script.option.Option;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import org.jetbrains.annotations.NotNull;

public class Console extends BaseOption {

	public Console() {
		super("console", "@console ");
	}

	@NotNull
	@Override
	public Option newInstance() {
		return new Console();
	}

	@Override
	protected boolean isValid() throws Exception {
		return executeConsoleCommand(StringUtils.setColor(getOptionValue(), true));
	}
}