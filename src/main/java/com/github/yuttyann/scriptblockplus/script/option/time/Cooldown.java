package com.github.yuttyann.scriptblockplus.script.option.time;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.github.yuttyann.scriptblockplus.enums.ScriptType;
import com.github.yuttyann.scriptblockplus.file.SBConfig;
import com.github.yuttyann.scriptblockplus.manager.MapManager;
import com.github.yuttyann.scriptblockplus.manager.auxiliary.SBMap;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public class Cooldown extends BaseOption {

	private Task task;

	public Cooldown() {
		super("cooldown", "@cooldown:");
	}

	private class Task extends BukkitRunnable {

		private int second = -1;
		private UUID uuid;
		private String fullCoords;
		private MapManager mapManager;
		private ScriptType scriptType;

		private Task(Cooldown cooldown, int second) {
			this.second = second + 1;
			if (cooldown != null) {
				this.uuid = cooldown.getUniqueId();
				this.fullCoords = cooldown.getFullCoords();
				this.scriptType = cooldown.getScriptType();
			}
		}

		public void runTaskTimer(Plugin plugin, MapManager mapManager) {
			this.mapManager = mapManager;
			mapManager.putCooldown(scriptType, fullCoords, uuid, Cooldown.this);
			runTaskTimer(plugin, 0, 20L);
		}

		@Override
		public void run() {
			if (--second <= 0) {
				mapManager.putCooldown(scriptType, fullCoords, uuid, Cooldown.this);
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
		SBMap<Map<UUID, Cooldown>> cooldownMap = getMapManager().getCooldownMap();
		Map<UUID, Cooldown> cooldowns = cooldownMap.get(getScriptType(), getFullCoords());
		Cooldown cooldown = cooldowns == null ? null : cooldowns.get(getUniqueId());
		return cooldown == null ? -1 : cooldown.task.second;
	}

	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("second", task.second);
		map.put("uuid", task.uuid);
		map.put("fullcoords", task.fullCoords);
		map.put("scripttype", task.scriptType);
		return map;
	}

	public void deserialize(Plugin plugin, MapManager mapManager, Map<String, Object> map) {
		task = new Task(null, (int) map.get("second"));
		task.uuid = (UUID) map.get("uuid");
		task.fullCoords = (String) map.get("fullcoords");
		task.scriptType = (ScriptType) map.get("scripttype");
		task.runTaskTimer(plugin, mapManager);
	}
}