package com.github.yuttyann.scriptblockplus.script.option.chat;

import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.script.option.Option;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import org.jetbrains.annotations.NotNull;

public class Bypass extends BaseOption {

	public Bypass() {
		super("bypass", "@bypass ");
	}

	@NotNull
	@Override
	public Option newInstance() {
		return new Bypass();
	}

	@Override
	protected boolean isValid() throws Exception {
		return executeCommand(getPlayer(), StringUtils.setColor(getOptionValue(), true), true);
	}
}