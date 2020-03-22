package com.github.yuttyann.scriptblockplus.script.option.time;

import com.github.yuttyann.scriptblockplus.file.config.SBConfig;
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

	@Override
	@NotNull
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
		if (!unSaveExec && getMapManager().containsDelay(getUniqueId(), getScriptType(), getFullCoords())) {
			SBConfig.ACTIVE_DELAY.send(getSBPlayer());
		} else {
			if (!unSaveExec) {
				getMapManager().putDelay(getUniqueId(), getScriptType(), getFullCoords());
			}
			Bukkit.getScheduler().runTaskLater(getPlugin(), this, Long.parseLong(array[0]));
		}
		return false;
	}

	@Override
	public void run() {
		if (!unSaveExec) {
			getMapManager().removeDelay(getUniqueId(), getScriptType(), getFullCoords());
		}
		if (getSBPlayer().isOnline()) {
			getSBRead().read(getScriptIndex() + 1);
		} else {
			EndProcessManager.getInstance().forEach(e -> e.failed(getSBRead()));
		}
	}
}