package com.github.yuttyann.scriptblockplus.script.option.chat;

import java.util.Random;

import org.bukkit.ChatColor;

import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public class ToPlayer extends BaseOption {

	public ToPlayer() {
		super("toplayer", "@player ");
	}

	@Override
	public boolean isValid() {
		String message = getOptionValue();
		message = StringUtils.replace(message, "&rc", getRandomColor());
		message = StringUtils.replace(message, "&", "§");
		Utils.sendMessage(getPlayer(), message);
		return true;
	}

	private String getRandomColor() {
		return ChatColor.getByChar(Integer.toHexString(new Random().nextInt(16))).toString();
	}
}