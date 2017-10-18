package com.github.yuttyann.scriptblockplus.script.option.chat;

import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public class ToPlayer extends BaseOption {

	public ToPlayer() {
		super("toplayer", "@player ", 3);
	}

	@Override
	public boolean isValid() {
		String message = getOptionValue();
		message = StringUtils.replaceRandomColor(message, "&rc");
		message = StringUtils.replace(message, "&", "§");
		Utils.sendMessage(getPlayer(), message);
		return true;
	}
}