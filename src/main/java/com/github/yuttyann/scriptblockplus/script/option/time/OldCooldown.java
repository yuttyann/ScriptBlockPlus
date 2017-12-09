package com.github.yuttyann.scriptblockplus.script.option.time;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.github.yuttyann.scriptblockplus.file.SBConfig;
import com.github.yuttyann.scriptblockplus.manager.MapManager;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public class OldCooldown extends BaseOption {

	private Task task;

	public OldCooldown() {
		super("oldcooldown", "@oldcooldown:");
	}

	private class Task extends BukkitRunnable {

		private int second = -1;
		private String fullCoords;
		private MapManager mapManager;

		private Task(OldCooldown cooldown, int second) {
			this.second = second + 1;
			if (cooldown != null) {
				this.fullCoords = cooldown.getFullCoords();
			}
		}

		public void runTaskTimer(Plugin plugin, MapManager mapManager) {
			this.mapManager = mapManager;
			mapManager.getOldCooldownMap().put(fullCoords, OldCooldown.this);
			runTaskTimer(plugin, 0, 20L);
		}

		@Override
		public void run() {
			if (--second <= 0) {
				mapManager.getOldCooldownMap().put(fullCoords, OldCooldown.this);
				cancel();
			}
		}
	}

	@Override
	protected boolean isValid() throws Exception {
		int original = getSecond();
		if (original > 0) {
			short hour = (short) (original / 3600);
			byte minute = (byte) (original % 3600 / 60);
			byte second = (byte) (original % 3600 % 60);
			Utils.sendMessage(getPlayer(), SBConfig.getActiveCooldownMessage(hour, minute, second));
			return false;
		}
		task = new Task(this, Integer.parseInt(getOptionValue()));
		task.runTaskTimer(getPlugin(), getMapManager());
		return true;
	}

	private int getSecond() {
		OldCooldown cooldownMap = getMapManager().getOldCooldownMap().get(getFullCoords());
		return cooldownMap == null ? -1 : cooldownMap.task.second;
	}

	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("second", task.second);
		map.put("fullcoords", task.fullCoords);
		return map;
	}

	public void deserialize(Plugin plugin, MapManager mapManager, Map<String, Object> map) {
		task = new Task(null, (int) map.get("second"));
		task.fullCoords = (String) map.get("fullcoords");
		task.runTaskTimer(plugin, mapManager);
	}
}