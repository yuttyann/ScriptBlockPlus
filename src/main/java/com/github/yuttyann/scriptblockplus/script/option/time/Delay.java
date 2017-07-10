package com.github.yuttyann.scriptblockplus.script.option.time;

import com.github.yuttyann.scriptblockplus.file.Lang;
import com.github.yuttyann.scriptblockplus.manager.ScriptManager;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public class Delay extends BaseOption implements Runnable {

	public Delay(ScriptManager scriptManager) {
		super(scriptManager, "delay", "@delay:");
	}

	@Override
	public boolean isValid() {
		String fullCoords = blockCoords.getFullCoords();
		if (mapManager.containsDelay(uuid, fullCoords, scriptType)) {
			Utils.sendPluginMessage(player, Lang.getActiveDelayMessage());
		} else {
			mapManager.putDelay(uuid, fullCoords, scriptType);
			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, this, Long.parseLong(optionData));
		}
		return false;
	}

	@Override
	public void run() {
		mapManager.removeDelay(uuid, blockCoords.getFullCoords(), scriptType);
		scriptRead.read(scriptIndex + 1);
	}
}