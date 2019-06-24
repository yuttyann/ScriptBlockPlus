package com.github.yuttyann.scriptblockplus.script.option.time;

import org.bukkit.Bukkit;

import com.github.yuttyann.scriptblockplus.file.SBConfig;
import com.github.yuttyann.scriptblockplus.manager.EndProcessManager;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.script.option.Option;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public class Delay extends BaseOption implements Runnable {

	private boolean unSaveExec;

	public Delay() {
		super("delay", "@delay:");
	}

	@Override
	public Option newInstance() {
		return new Delay();
	}

	@Override
	public boolean isFailedIgnore() {
		return true;
	}

	@Override
	protected boolean isValid() throws Exception {
		String[] array = StringUtils.split(getOptionValue(), "/");
		unSaveExec = array.length > 1 ? Boolean.valueOf(array[1]) : false;
		if (containsDelay()) {
			Utils.sendMessage(getPlayer(), SBConfig.getActiveDelayMessage());
		} else {
			putDelay();
			Bukkit.getScheduler().runTaskLater(getPlugin(), this, Long.parseLong(array[0]));
		}
		return false;
	}

	@Override
	public void run() {
		removeDelay();
		if (getSBPlayer().isOnline()) {
			getScriptRead().read(getScriptIndex() + 1);
		} else {
			EndProcessManager.getInstance().forEach(e -> e.failed(getScriptRead()));
		}
	}

	private void putDelay() {
		if (!unSaveExec) {
			getMapManager().putDelay(getUniqueId(), getFullCoords(), getScriptType());
		}
	}

	private void removeDelay() {
		if (!unSaveExec) {
			getMapManager().removeDelay(getUniqueId(), getFullCoords(), getScriptType());
		}
	}

	private boolean containsDelay() {
		if (!unSaveExec) {
			return getMapManager().containsDelay(getUniqueId(), getFullCoords(), getScriptType());
		}
		return false;
	}
}