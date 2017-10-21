package com.github.yuttyann.scriptblockplus.script.option.chat;

import org.bukkit.Bukkit;

import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;

public class Console extends BaseOption {

	public Console() {
		super("console", "@console ", 2);
	}

	@Override
	public boolean isValid() {
		String command = getOptionValue();
		command = StringUtils.replaceRandomColor(command, "&rc");
		command = StringUtils.replace(command, "&", "§");
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
		return true;
	}
}
