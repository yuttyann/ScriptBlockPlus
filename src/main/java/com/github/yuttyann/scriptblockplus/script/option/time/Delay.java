package com.github.yuttyann.scriptblockplus.script.option.time;

import com.github.yuttyann.scriptblockplus.file.SBConfig;
import com.github.yuttyann.scriptblockplus.manager.EndProcessManager;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.script.option.Option;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public class Delay extends BaseOption implements Runnable {

	private boolean unSaveExec;

	public Delay() {
		super("delay", "@delay:");
	}

	@NotNull
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
		unSaveExec = array.length > 1 && Boolean.parseBoolean(array[1]);
		if (containsDelay()) {
			SBConfig.ACTIVE_DELAY.send(getSBPlayer());
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
			getSBRead().read(getScriptIndex() + 1);
		} else {
			EndProcessManager.getInstance().forEach(e -> e.failed(getSBRead()));
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