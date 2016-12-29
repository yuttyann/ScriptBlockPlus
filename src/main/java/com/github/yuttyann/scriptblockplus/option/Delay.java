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

	public static void put(String fullcoords, UUID uuid) {
		ArrayList<UUID> uuids = MapManager.getDelay().get(fullcoords);
		ArrayList<UUID> uuids2 = uuids != null ? uuids : new ArrayList<UUID>();
		uuids2.add(uuid);
		MapManager.getDelay().put(fullcoords, uuids2);
	}

	public static void remove(String fullcoords, UUID uuid) {
		ArrayList<UUID> uuids = MapManager.getDelay().get(fullcoords);
		if (uuids != null) {
			uuids.remove(uuid);
			MapManager.getDelay().put(fullcoords, uuids);
		}
	}

	public static boolean contains(String fullcoords, UUID uuid) {
		ArrayList<UUID> uuids = MapManager.getDelay().get(fullcoords);
		return uuids != null && uuids.contains(uuid);
	}
}
