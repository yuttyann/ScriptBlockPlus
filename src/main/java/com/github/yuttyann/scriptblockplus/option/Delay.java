package com.github.yuttyann.scriptblockplus.option;

import java.util.ArrayList;
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

	public static void put(String fullCoords, UUID uuid) {
		ArrayList<UUID> uuids = MapManager.getDelay().get(fullCoords);
		ArrayList<UUID> uuids2 = uuids != null ? uuids : new ArrayList<UUID>();
		uuids2.add(uuid);
		MapManager.getDelay().put(fullCoords, uuids2);
	}

	public static void remove(String fullCoords, UUID uuid) {
		ArrayList<UUID> uuids = MapManager.getDelay().get(fullCoords);
		if (uuids != null) {
			uuids.remove(uuid);
			MapManager.getDelay().put(fullCoords, uuids);
		}
	}

	public static boolean contains(String fullCoords, UUID uuid) {
		ArrayList<UUID> uuids = MapManager.getDelay().get(fullCoords);
		return uuids != null && uuids.contains(uuid);
	}
}