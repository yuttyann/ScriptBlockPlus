package com.github.yuttyann.scriptblockplus.option;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.scheduler.BukkitRunnable;

import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.manager.MapManager;

public class Cooldown {

	private int second;
	private ScriptBlock plugin;
	private MapManager mapManager;

	public Cooldown(ScriptBlock plugin, String second) {
		this.plugin = plugin;
		this.second = Integer.parseInt(second);
		this.mapManager = plugin.getMapManager();
	}

	public int getSecond() {
		return second;
	}

	public int[] get(String fullCoords, UUID uuid) {
		Map<UUID, int[]> params = mapManager.getCooldown().get(fullCoords);
		if (params != null && params.containsKey(uuid)) {
			return params.get(uuid);
		}
		return new int[]{0, 0, 0};
	}

	public Map<UUID, int[]> put(String fullCoords, UUID uuid, int[] params) {
		Map<UUID, int[]> temp = mapManager.getCooldown().get(fullCoords);
		Map<UUID, int[]> params2 = temp != null ? temp : new HashMap<UUID, int[]>();
		params2.put(uuid, params);
		return mapManager.getCooldown().put(fullCoords, params2);
	}

	public void remove(String fullCoords, UUID uuid) {
		Map<UUID, int[]> params = mapManager.getCooldown().get(fullCoords);
		if (params != null && params.containsKey(uuid)) {
			params.remove(uuid);
			mapManager.getCooldown().put(fullCoords, params);
		}
	}

	public boolean contains(String fullCoords, UUID uuid) {
		Map<UUID, int[]> params = mapManager.getCooldown().get(fullCoords);
		return params != null && params.containsKey(uuid);
	}

	public void run(final UUID uuid, final String fullCoords) {
		put(fullCoords, uuid, calcParams(get(fullCoords, uuid), second));
		new BukkitRunnable() {
			int second = getSecond();
			int[] params = new int[3];
			@Override
			public void run() {
				if (second == 0) {
					remove(fullCoords, uuid);
				}
				if (!contains(fullCoords, uuid)) {
					cancel();
				} else {
					put(fullCoords, uuid, calcParams(params, second > 0 ? second : (second = getSecond())));
					second--;
				}
			}
		}.runTaskTimer(plugin, 0, 20);
	}

	private int[] calcParams(int[] params, int second) {
		params[0] = second / 3600;
		params[1] = second % 3600 / 60;
		params[2] = second % 3600 % 60;
		return params;
	}
}