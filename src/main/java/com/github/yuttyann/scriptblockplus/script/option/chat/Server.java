package com.github.yuttyann.scriptblockplus.script.option.chat;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;

public class Server extends BaseOption {

	private Server() {
		super("server", "@server ");
	}

	@Override
	public boolean isValid() {
		String message = getOptionValue();
		message = StringUtils.replace(message, "&rc", getRandomColor());
		message = StringUtils.replace(message, "&", "§");
		Bukkit.broadcastMessage(message);
		return true;
	}

	private String getRandomColor() {
		return ChatColor.getByChar(Integer.toHexString(new Random().nextInt(16))).toString();
	}
}