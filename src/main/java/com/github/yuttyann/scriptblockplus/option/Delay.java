package com.github.yuttyann.scriptblockplus.option;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.github.yuttyann.scriptblockplus.manager.MapManager;

public class Delay {

	private long tick;

	public Delay(long tick) {
		this.tick = tick;
	}

	public Delay(String tick) {
		this.tick = Long.parseLong(tick);
	}

	public long getTick() {
		return tick;
	}

	public void put(String fullCoords, UUID uuid) {
		List<UUID> temp = MapManager.getDelay().get(fullCoords);
		List<UUID> uuids = temp != null ? temp : new ArrayList<UUID>();
		uuids.add(uuid);
		MapManager.getDelay().put(fullCoords, uuids);
	}

	public void remove(String fullCoords, UUID uuid) {
		List<UUID> uuids = MapManager.getDelay().get(fullCoords);
		if (uuids != null) {
			uuids.remove(uuid);
			MapManager.getDelay().put(fullCoords, uuids);
		}
	}

	public boolean contains(String fullCoords, UUID uuid) {
		List<UUID> uuids = MapManager.getDelay().get(fullCoords);
		return uuids != null && uuids.contains(uuid);
	}
}