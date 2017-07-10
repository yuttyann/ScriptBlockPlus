package com.github.yuttyann.scriptblockplus.script.option.time;

import java.util.Calendar;
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
		int[] params = getParams(fullCoords);
		if (params != null) {
			int currSecond = Utils.getTime(Calendar.SECOND);
			if (params[2] > currSecond) {
				int time = params[2] - currSecond;
				short hour = (short) (time / 3600);
				byte minute = (byte) (time % 3600 / 60);
				byte second = (byte) (time % 3600 % 60);
				Utils.sendPluginMessage(player, Lang.getActiveCooldownMessage(hour, minute, second));
			} else {
				mapManager.removeCooldown(uuid, fullCoords, scriptType);
			}
			return true;
		}
		return false;
	}

	private int[] getParams(String fullCoords) {
		Map<String, Map<UUID, int[]>> cooldownMap = mapManager.getCooldownScripts().get(scriptType);
		Map<UUID, int[]> paramMap = cooldownMap != null ? cooldownMap.get(fullCoords) : null;
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
		int[] params = new int[4];
		params[0] = Utils.getTime(Calendar.SECOND);
		params[1] = Integer.parseInt(optionData);
		params[2] = params[0] + params[1];
		mapManager.putCooldown(uuid, fullCoords, scriptType, params);
		return true;
	}
}