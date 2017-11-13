package com.github.yuttyann.scriptblockplus.script.option.chat;

import org.bukkit.Bukkit;

import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;

public class Server extends BaseOption {

	private Server() {
		super("server", "@server ");
	}

	@Override
	public boolean isValid() throws Exception {
		String message = StringUtils.replaceColorCode(getOptionValue(), true);
		Bukkit.broadcastMessage(message);
		return true;
	}
}