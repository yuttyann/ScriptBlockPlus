package com.github.yuttyann.scriptblockplus.script.option.chat;

import org.bukkit.Bukkit;

import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;

public class Server extends BaseOption {

	private Server() {
		super("server", "@server ", 4);
	}

	@Override
	public boolean isValid() {
		String message = getOptionValue();
		message = StringUtils.replaceRandomColor(message, "&rc");
		message = StringUtils.replace(message, "&", "§");
		Bukkit.broadcastMessage(message);
		return true;
	}
}