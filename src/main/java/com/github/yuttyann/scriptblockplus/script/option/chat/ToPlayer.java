package com.github.yuttyann.scriptblockplus.script.option.chat;

import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.script.option.Option;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public class ToPlayer extends BaseOption {

	public ToPlayer() {
		super("toplayer", "@player ");
	}

	@Override
	public Option newInstance() {
		return new ToPlayer();
	}

	@Override
	protected boolean isValid() throws Exception {
		Utils.sendMessage(getPlayer(), StringUtils.replaceColorCode(getOptionValue(), true));
		return true;
	}
}