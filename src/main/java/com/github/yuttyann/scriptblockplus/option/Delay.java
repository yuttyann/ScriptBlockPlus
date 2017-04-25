package com.github.yuttyann.scriptblockplus.option;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.enums.ScriptType;
import com.github.yuttyann.scriptblockplus.manager.MapManager;

public class Delay {

	private long tick;
	private MapManager mapManager;

	public Delay(ScriptBlock plugin, String tick) {
		this.tick = Long.parseLong(tick);
		this.mapManager = plugin.getMapManager();
	}

	public long getTick() {
		return tick;
	}

	public void put(ScriptType scriptType, String fullCoords, UUID uuid) {
		Map<String, List<UUID>> delay = mapManager.getDelayScripts().get(scriptType);
		if (delay == null) {
			delay = new HashMap<String, List<UUID>>();
		}
		List<UUID> uuids = delay.get(fullCoords);
		if (uuids == null) {
			uuids = new ArrayList<UUID>();
		}
		uuids.add(uuid);
		mapManager.getDelayScripts().put(scriptType, createMap(fullCoords, uuids));
	}

	public void remove(ScriptType scriptType, String fullCoords, UUID uuid) {
		Map<String, List<UUID>> delay = mapManager.getDelayScripts().get(scriptType);
		if (delay == null) {
			return;
		}
		List<UUID> uuids = delay.get(fullCoords);
		if (uuids != null) {
			uuids.remove(uuid);
			mapManager.getDelayScripts().put(scriptType, createMap(fullCoords, uuids));
		}
	}

	public boolean contains(ScriptType scriptType, String fullCoords, UUID uuid) {
		Map<String, List<UUID>> delay = mapManager.getDelayScripts().get(scriptType);
		if (delay == null) {
			return false;
		}
		List<UUID> uuids = delay.get(fullCoords);
		return uuids != null && uuids.contains(uuid);
	}

	private Map<String, List<UUID>> createMap(String fullCoords, List<UUID> uuids) {
		Map<String, List<UUID>> value = new HashMap<String, List<UUID>>();
		value.put(fullCoords, uuids);
		return value;
	}
}