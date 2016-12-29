package com.github.yuttyann.scriptblockplus.option;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.scheduler.BukkitRunnable;

import com.github.yuttyann.scriptblockplus.Main;
import com.github.yuttyann.scriptblockplus.manager.MapManager;

public class Cooldown {

	private long tick;
	private static long[] cooldownParams;

	public Cooldown(long tick) {
		this.tick = tick;
		cooldownParams = new long[3];
	}

	public Cooldown(String tick) {
		this.tick = Long.parseLong(tick);
		cooldownParams = new long[3];
	}

	public long getTick() {
		return tick;
	}

	public long getSecond() {
		return tick / 20;
	}

	public static long[] getParams(String fullcoords, UUID uuid) {
		HashMap<UUID, long[]> params = MapManager.getCooldownParams().get(fullcoords);
		if (params != null && params.containsKey(uuid)) {
			return params.get(uuid);
		}
		return null;
	}

	public static void put(String fullcoords, UUID uuid) {
		ArrayList<UUID> uuids = MapManager.getCooldown().get(fullcoords);
		ArrayList<UUID> uuids2 = uuids != null ? uuids : new ArrayList<UUID>();
		uuids2.add(uuid);
		MapManager.getCooldown().put(fullcoords, uuids2);
	}

	public static void putParams(String fullcoords, UUID uuid) {
		HashMap<UUID, long[]> params = MapManager.getCooldownParams().get(fullcoords);
		HashMap<UUID, long[]> params2 = params != null ? params : new HashMap<UUID, long[]>();
		params2.put(uuid, cooldownParams);
		MapManager.getCooldownParams().put(fullcoords, params2);
	}

	public static void remove(String fullcoords, UUID uuid) {
		ArrayList<UUID> uuids = MapManager.getCooldown().get(fullcoords);
		if (uuids != null) {
			uuids.remove(uuid);
			MapManager.getCooldown().put(fullcoords, uuids);
		}
	}

	public static void removeParams(String fullcoords, UUID uuid) {
		HashMap<UUID, long[]> params = MapManager.getCooldownParams().get(fullcoords);
		if (params != null && params.containsKey(uuid)) {
			params.remove(uuid);
			MapManager.getCooldownParams().put(fullcoords, params);
		}
	}

	public static boolean contains(String fullcoords, UUID uuid) {
		ArrayList<UUID> uuids = MapManager.getCooldown().get(fullcoords);
		return uuids != null && uuids.contains(uuid);
	}

	public static boolean containsParams(String fullcoords, UUID uuid) {
		HashMap<UUID, long[]> params = MapManager.getCooldownParams().get(fullcoords);
		return params != null && params.containsKey(uuid);
	}

	public void run(final UUID uuid, final String fullcoords) {
		put(fullcoords, uuid);
		new BukkitRunnable() {
			@Override
			public void run() {
				remove(fullcoords, uuid);
			}
		}.runTaskLater(Main.instance, tick);
		new BukkitRunnable() {
			long second = getSecond();
			@Override
			public void run() {
				if (!contains(fullcoords, uuid)) {
					removeParams(fullcoords, uuid);
					cancel();
				}
				cooldownParams[0] = second / 3600;
				cooldownParams[1] = second % 3600 / 60;
				cooldownParams[2] = second % 3600 % 60;
				putParams(fullcoords, uuid);
				second--;
			}
		}.runTaskTimer(Main.instance, 0, 20);
	}
}
