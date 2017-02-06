package com.github.yuttyann.scriptblockplus.option;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.scheduler.BukkitRunnable;

import com.github.yuttyann.scriptblockplus.ScriptBlock;
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

	public static long[] getParams(String fullCoords, UUID uuid) {
		Map<UUID, long[]> params = MapManager.getCooldownParams().get(fullCoords);
		if (params != null && params.containsKey(uuid)) {
			return params.get(uuid);
		}
		return null;
	}

	public static void put(String fullCoords, UUID uuid) {
		List<UUID> uuids = MapManager.getCooldown().get(fullCoords);
		List<UUID> uuids2 = uuids != null ? uuids : new ArrayList<UUID>();
		uuids2.add(uuid);
		MapManager.getCooldown().put(fullCoords, uuids2);
	}

	public static void putParams(String fullCoords, UUID uuid) {
		Map<UUID, long[]> params = MapManager.getCooldownParams().get(fullCoords);
		Map<UUID, long[]> params2 = params != null ? params : new HashMap<UUID, long[]>();
		params2.put(uuid, cooldownParams);
		MapManager.getCooldownParams().put(fullCoords, params2);
	}

	public static void remove(String fullCoords, UUID uuid) {
		List<UUID> uuids = MapManager.getCooldown().get(fullCoords);
		if (uuids != null) {
			uuids.remove(uuid);
			MapManager.getCooldown().put(fullCoords, uuids);
		}
	}

	public static void removeParams(String fullCoords, UUID uuid) {
		Map<UUID, long[]> params = MapManager.getCooldownParams().get(fullCoords);
		if (params != null && params.containsKey(uuid)) {
			params.remove(uuid);
			MapManager.getCooldownParams().put(fullCoords, params);
		}
	}

	public static boolean contains(String fullCoords, UUID uuid) {
		List<UUID> uuids = MapManager.getCooldown().get(fullCoords);
		return uuids != null && uuids.contains(uuid);
	}

	public static boolean containsParams(String fullCoords, UUID uuid) {
		Map<UUID, long[]> params = MapManager.getCooldownParams().get(fullCoords);
		return params != null && params.containsKey(uuid);
	}

	public void run(final UUID uuid, final String fullCoords) {
		put(fullCoords, uuid);
		new BukkitRunnable() {
			@Override
			public void run() {
				remove(fullCoords, uuid);
			}
		}.runTaskLater(ScriptBlock.instance, tick);
		new BukkitRunnable() {
			long second = getSecond();
			@Override
			public void run() {
				if (!contains(fullCoords, uuid)) {
					removeParams(fullCoords, uuid);
					cancel();
				}
				cooldownParams[0] = second / 3600;
				cooldownParams[1] = second % 3600 / 60;
				cooldownParams[2] = second % 3600 % 60;
				putParams(fullCoords, uuid);
				second--;
			}
		}.runTaskTimer(ScriptBlock.instance, 0, 20);
	}
}