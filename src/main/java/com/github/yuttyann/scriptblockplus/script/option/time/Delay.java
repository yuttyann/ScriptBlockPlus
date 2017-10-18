package com.github.yuttyann.scriptblockplus.script.option.time;

import org.bukkit.Bukkit;

import com.github.yuttyann.scriptblockplus.file.SBConfig;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public class Delay extends BaseOption implements Runnable {

	public Delay() {
		super("delay", "@delay:", 8);
	}

	@Override
	public boolean isValid() {
		if (getMapManager().containsDelay(getScriptType(), getFullCoords(), getUniqueId())) {
			Utils.sendMessage(getPlayer(), SBConfig.getActiveDelayMessage());
		} else {
			getMapManager().putDelay(getScriptType(), getFullCoords(), getUniqueId());
			Bukkit.getScheduler().runTaskLater(getPlugin(), this, Long.parseLong(getOptionValue()));
		}
		return false;
	}

	@Override
	public boolean isFailedIgnore() {
		return true;
	}

	@Override
	public void run() {
		getScriptRead().read(getScriptIndex() + 1);
		getMapManager().removeDelay(getScriptType(), getFullCoords(), getUniqueId());
	}
}