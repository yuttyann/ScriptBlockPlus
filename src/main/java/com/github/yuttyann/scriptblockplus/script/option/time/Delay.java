package com.github.yuttyann.scriptblockplus.script.option.time;

import org.bukkit.Bukkit;

import com.github.yuttyann.scriptblockplus.file.SBConfig;
import com.github.yuttyann.scriptblockplus.manager.EndProcessManager;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public class Delay extends BaseOption implements Runnable {

	private boolean unsafe;

	public Delay() {
		super("delay", "@delay:");
	}

	@Override
	public boolean isValid() throws Exception {
		String[] array = StringUtils.split(getOptionValue(), "/");
		unsafe = array.length > 1 ? Boolean.valueOf(array[1]) : false;
		if (containsDelay()) {
			Utils.sendMessage(getPlayer(), SBConfig.getActiveDelayMessage());
		} else {
			putDelay();
			Bukkit.getScheduler().runTaskLater(getPlugin(), this, Long.parseLong(array[0]));
		}
		return false;
	}

	@Override
	public boolean isFailedIgnore() {
		return true;
	}

	@Override
	public void run() {
		removeDelay();
		if (getSBPlayer().isOnline()) {
			getScriptRead().read(getScriptIndex() + 1);
		} else {
			new EndProcessManager().forEach(e -> e.failed(getScriptRead()));
		}
	}

	private void putDelay() {
		if (!unsafe) {
			getMapManager().putDelay(getScriptType(), getFullCoords(), getUniqueId());
		}
	}

	private void removeDelay() {
		if (!unsafe) {
			getMapManager().removeDelay(getScriptType(), getFullCoords(), getUniqueId());
		}
	}

	private boolean containsDelay() {
		if (!unsafe) {
			return getMapManager().containsDelay(getScriptType(), getFullCoords(), getUniqueId());
		}
		return false;
	}
}