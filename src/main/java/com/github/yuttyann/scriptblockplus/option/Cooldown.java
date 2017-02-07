package com.github.yuttyann.scriptblockplus.option;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.scheduler.BukkitRunnable;

import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.manager.MapManager;

public class Cooldown {

	private int second;

	public Cooldown(int second) {
		this.second = second;
	}

	public Cooldown(String second) {
		this.second = Integer.parseInt(second);
	}

	public int getSecond() {
		return second;
	}

	public long getTick() {
		return second * 20;
	}

	public int[] get(String fullCoords, UUID uuid) {
		Map<UUID, int[]> params = MapManager.getCooldownParams().get(fullCoords);
		if (params != null && params.containsKey(uuid)) {
			return params.get(uuid);
		}
		return new int[]{0, 0, 0};
	}

	public Map<UUID, int[]> put(String fullCoords, UUID uuid, int[] params) {
		Map<UUID, int[]> temp = MapManager.getCooldownParams().get(fullCoords);
		Map<UUID, int[]> params2 = temp != null ? temp : new HashMap<UUID, int[]>();
		params2.put(uuid, params);
		return MapManager.getCooldownParams().put(fullCoords, params2);
	}

	public void remove(String fullCoords, UUID uuid) {
		Map<UUID, int[]> params = MapManager.getCooldownParams().get(fullCoords);
		if (params != null && params.containsKey(uuid)) {
			params.remove(uuid);
			MapManager.getCooldownParams().put(fullCoords, params);
		}
	}

	public boolean contains(String fullCoords, UUID uuid) {
		Map<UUID, int[]> params = MapManager.getCooldownParams().get(fullCoords);
		return params != null && params.containsKey(uuid);
	}

	public void run(final UUID uuid, final String fullCoords) {
		put(fullCoords, uuid, calcParams(get(fullCoords, uuid), second));
		new BukkitRunnable() {
			@Override
			public void run() {
				remove(fullCoords, uuid);
			}
		}.runTaskLater(ScriptBlock.instance, getTick());
		new BukkitRunnable() {
			int second = getSecond();
			int[] params = new int[3];
			@Override
			public void run() {
				if (!contains(fullCoords, uuid)) {
					cancel();
				} else {
					put(fullCoords, uuid, calcParams(params, second > 0 ? second : (second = getSecond())));
					second--;
				}
			}
		}.runTaskTimer(ScriptBlock.instance, 0, 20);
	}

	private int[] calcParams(int[] params, int second) {
		params[0] = second / 3600;
		params[1] = second % 3600 / 60;
		params[2] = second % 3600 % 60;
		return params;
	}
}