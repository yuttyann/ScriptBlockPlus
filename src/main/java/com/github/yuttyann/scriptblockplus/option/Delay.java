package com.github.yuttyann.scriptblockplus.option;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.github.yuttyann.scriptblockplus.ScriptBlock;
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

	public void put(String fullCoords, UUID uuid) {
		List<UUID> temp = mapManager.getDelay().get(fullCoords);
		List<UUID> uuids = temp != null ? temp : new ArrayList<UUID>();
		uuids.add(uuid);
		mapManager.getDelay().put(fullCoords, uuids);
	}

	public void remove(String fullCoords, UUID uuid) {
		List<UUID> uuids = mapManager.getDelay().get(fullCoords);
		if (uuids != null) {
			uuids.remove(uuid);
			mapManager.getDelay().put(fullCoords, uuids);
		}
	}

	public boolean contains(String fullCoords, UUID uuid) {
		List<UUID> uuids = mapManager.getDelay().get(fullCoords);
		return uuids != null && uuids.contains(uuid);
	}
}