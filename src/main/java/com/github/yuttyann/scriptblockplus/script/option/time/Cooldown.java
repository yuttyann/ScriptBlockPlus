package com.github.yuttyann.scriptblockplus.script.option.time;

import java.util.Map;
import java.util.UUID;

import com.github.yuttyann.scriptblockplus.file.Lang;
import com.github.yuttyann.scriptblockplus.manager.ScriptManager;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public class Cooldown extends BaseOption {

	public Cooldown(ScriptManager scriptManager) {
		super(scriptManager, "cooldown", "@cooldown:");
	}

	public boolean inCooldown(String fullCoords) {
		long[] params = getParams(fullCoords);
		if (params != null) {
			long currTime = System.currentTimeMillis();
			if (params[2] > currTime) {
				int time = (int) ((params[2] - currTime) / 1000);
				short hour = (short) (time / 3600);
				byte minute = (byte) (time % 3600 / 60);
				byte second = (byte) (time % 3600 % 60);
				Utils.sendPluginMessage(player, Lang.getActiveCooldownMessage(hour, minute, second));
				return true;
			} else {
				mapManager.removeCooldown(uuid, fullCoords, scriptType);
			}
		}
		return false;
	}

	private long[] getParams(String fullCoords) {
		Map<String, Map<UUID, long[]>> cooldownMap = mapManager.getCooldownScripts().get(scriptType);
		Map<UUID, long[]> paramMap = cooldownMap != null ? cooldownMap.get(fullCoords) : null;
		if (paramMap != null) {
			return paramMap.get(uuid);
		}
		return null;
	}

	@Override
	public boolean isValid() {
		String fullCoords = blockCoords.getFullCoords();
		if (inCooldown(fullCoords)) {
			return false;
		}
		long[] params = new long[4];
		params[0] = System.currentTimeMillis();
		params[1] = Integer.parseInt(optionData) * 1000;
		params[2] = params[0] + params[1];
		mapManager.putCooldown(uuid, fullCoords, scriptType, params);
		return true;
	}
}