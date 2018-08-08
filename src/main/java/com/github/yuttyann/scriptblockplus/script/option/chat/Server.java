package com.github.yuttyann.scriptblockplus.script.option.chat;

import org.bukkit.Bukkit;

import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;

public class Server extends BaseOption {

	private Server() {
		super("server", "@server ");
	}

	@Override
	protected boolean isValid() throws Exception {
		Bukkit.broadcastMessage(StringUtils.replaceColorCode(getOptionValue(), true));
		return true;
	}
}