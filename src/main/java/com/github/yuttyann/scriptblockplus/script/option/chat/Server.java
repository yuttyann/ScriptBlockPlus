package com.github.yuttyann.scriptblockplus.script.option.chat;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

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
		Bukkit.broadcastMessage(replace(player, optionData));
		return true;
	}

	private String replace(Player player, String text) {
		text = StringUtils.replace(text, "&rc", Utils.getRandomColor());
		text = StringUtils.replace(text, "&", "§");
		return text;
	}
}