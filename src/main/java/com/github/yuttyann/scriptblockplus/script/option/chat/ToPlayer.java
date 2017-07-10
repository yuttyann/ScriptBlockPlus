package com.github.yuttyann.scriptblockplus.script.option.chat;

import org.bukkit.entity.Player;

import com.github.yuttyann.scriptblockplus.manager.ScriptManager;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public class ToPlayer extends BaseOption {

	public ToPlayer(ScriptManager scriptManager) {
		super(scriptManager, "toplayer", "@player ");
	}

	@Override
	public boolean isValid() {
		if (player.isOnline()) {
			player.sendMessage(replace(player, optionData));
		}
		return true;
	}

	private String replace(Player player, String text) {
		text = StringUtils.replace(text, "&rc", Utils.getRandomColor());
		text = StringUtils.replace(text, "&", "§");
		return text;
	}
}