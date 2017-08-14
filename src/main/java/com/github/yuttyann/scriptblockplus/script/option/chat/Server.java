package com.github.yuttyann.scriptblockplus.script.option.chat;

import org.bukkit.Bukkit;

import com.github.yuttyann.scriptblockplus.manager.ScriptManager;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public class Server extends BaseOption {

	public Server(ScriptManager scriptManager) {
		super(scriptManager, "server", "@server ");
	}

	@Override
	public boolean isValid() {
		String message = optionData;
		message = StringUtils.replace(message, "&rc", Utils.getRandomColor());
		message = StringUtils.replace(message, "&", "§");
		Bukkit.broadcastMessage(message);
		return true;
	}
}