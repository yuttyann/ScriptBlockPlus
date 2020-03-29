package com.github.yuttyann.scriptblockplus.script.option.chat;

import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.script.option.Option;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public class Server extends BaseOption {

	public Server() {
		super("server", "@server ");
	}

	@Override
	@NotNull
	public Option newInstance() {
		return new Server();
	}

	@Override
	protected boolean isValid() throws Exception {
		Bukkit.broadcastMessage(StringUtils.setColor(getOptionValue(), true));
		return true;
	}
}