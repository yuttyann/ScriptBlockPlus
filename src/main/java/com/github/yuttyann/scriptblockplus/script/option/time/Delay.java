package com.github.yuttyann.scriptblockplus.script.option.time;

import org.bukkit.Bukkit;

import com.github.yuttyann.scriptblockplus.file.SBConfig;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public class Delay extends BaseOption implements Runnable {

	public Delay() {
		super("delay", "@delay:");
	}

	@Override
	public boolean isValid() {
		String fullCoords = getFullCoords();
		if (getMapManager().containsDelay(getScriptType(), fullCoords, getUniqueId())) {
			Utils.sendMessage(getPlayer(), SBConfig.getActiveDelayMessage());
		} else {
			getMapManager().putDelay(getScriptType(), fullCoords, getUniqueId());
			Bukkit.getScheduler().runTaskLater(getPlugin(), this, Long.parseLong(getOptionValue()));
		}
		return false;
	}

	@Override
	public void run() {
		getMapManager().removeDelay(getScriptType(), getFullCoords(), getUniqueId());
		getScriptRead().read(getScriptIndex() + 1);
	}
}