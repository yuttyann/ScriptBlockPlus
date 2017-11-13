package com.github.yuttyann.scriptblockplus.script.option.chat;

import org.bukkit.Bukkit;

import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;

public class Console extends BaseOption {

	public Console() {
		super("console", "@console ");
	}

	@Override
	public boolean isValid() throws Exception {
		String command = StringUtils.replaceColorCode(getOptionValue(), true);
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
		return true;
	}
}
