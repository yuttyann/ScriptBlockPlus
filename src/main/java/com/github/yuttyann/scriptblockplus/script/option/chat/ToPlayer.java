package com.github.yuttyann.scriptblockplus.script.option.chat;

import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public class ToPlayer extends BaseOption {

	public ToPlayer() {
		super("toplayer", "@player ");
	}

	@Override
	public boolean isValid() throws Exception {
		String message = StringUtils.replaceColorCode(getOptionValue(), true);
		Utils.sendMessage(getPlayer(), message);
		return true;
	}
}