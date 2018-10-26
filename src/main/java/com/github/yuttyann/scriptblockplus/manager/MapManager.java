package com.github.yuttyann.scriptblockplus.manager;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Location;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.file.Files;
import com.github.yuttyann.scriptblockplus.file.yaml.YamlConfig;
import com.github.yuttyann.scriptblockplus.manager.auxiliary.SBMap;
import com.github.yuttyann.scriptblockplus.script.ScriptType;
import com.github.yuttyann.scriptblockplus.script.option.time.TimeData;
import com.github.yuttyann.scriptblockplus.utils.FileUtils;
import com.github.yuttyann.scriptblockplus.utils.StreamUtils;

public final class MapManager {

	private ScriptBlock plugin;
	private SBMap<Set<UUID>> delays;
	private Set<TimeData> cooldowns;
	private Map<ScriptType, Set<String>> scriptCoords;

	public MapManager(ScriptBlock plugin) {
		this.plugin = plugin;
		this.delays = new SBMap<>();
		this.cooldowns = new HashSet<>();
		this.scriptCoords = new HashMap<>();
	}

	public SBMap<Set<UUID>> getDelays() {
		return delays;
	}

	public Set<TimeData> getCooldowns() {
		return cooldowns;
	}

	public Map<ScriptType, Set<String>> getScriptCoords() {
		return scriptCoords;
	}

	public void loadAllScripts() {
		try {
			StreamUtils.forEach(ScriptType.values(), s -> loadScripts(Files.getScriptFile(s), s));
		} catch (Exception e) {
			scriptCoords.clear();
		}
	}

	public void loadScripts(YamlConfig scriptFile, ScriptType scriptType) {
		Set<String> set = new HashSet<String>(scriptCoords.size());
		scriptFile.getKeys().forEach(w -> scriptFile.getKeys(w).forEach(c -> set.add(w + ", " + c)));
		scriptCoords.put(scriptType, set);
	}

	public void saveCooldown() {
		if (cooldowns.size() > 0) {
			File cooldownFile = new File(plugin.getDataFolder(), "scripts/cooldown.dat");
			Set<Map<String, Object>> set = new HashSet<>();
			try {
				cooldowns.forEach(t -> set.add(t.serialize()));
			} catch (Exception e) {
				set.clear();
			} finally {
				if (set.size() > 0) {
					try {
						FileUtils.saveFile(cooldownFile, set);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	public void loadCooldown() {
		File cooldownFile = new File(plugin.getDataFolder(), "scripts/cooldown.dat");
		if (cooldownFile.exists()) {
			try {
				Set<Map<String, Object>> set = FileUtils.loadFile(cooldownFile);
				set.forEach(TimeData::deserialize);
			} catch (Exception e) {
				cooldowns = new HashSet<>();
			} finally {
				cooldownFile.delete();
			}
		}
	}

	public void putDelay(ScriptType scriptType, String fullCoords, UUID uuid) {
		Set<UUID> set = delays.get(scriptType, fullCoords);
		if (set == null) {
			delays.put(scriptType, fullCoords, set = new HashSet<>());
		}
		set.add(uuid);
	}

	public void removeDelay(ScriptType scriptType, String fullCoords, UUID uuid) {
		Set<UUID> set = delays.get(scriptType, fullCoords);
		if (set != null) {
			set.remove(uuid);
		}
	}

	public boolean containsDelay(ScriptType scriptType, String fullCoords, UUID uuid) {
		Set<UUID> set = delays.get(scriptType, fullCoords);
		return set != null && set.contains(uuid);
	}

	public void addCoords(ScriptType scriptType, Location location) {
		String fullCoords = BlockCoords.getFullCoords(location);
		Set<String> set = scriptCoords.get(scriptType);
		if (set != null) {
			set.add(fullCoords);
		}
		removeTimes(scriptType, fullCoords);
	}

	public void removeCoords(ScriptType scriptType, Location location) {
		String fullCoords = BlockCoords.getFullCoords(location);
		Set<String> set = scriptCoords.get(scriptType);
		if (set != null) {
			set.remove(fullCoords);
		}
		removeTimes(scriptType, fullCoords);
	}

	public boolean containsCoords(ScriptType scriptType, Location location) {
		String fullCoords = BlockCoords.getFullCoords(location);
		Set<String> set = scriptCoords.get(scriptType);
		return set != null && set.contains(fullCoords);
	}

	public void removeTimes(ScriptType scriptType, Location location) {
		removeTimes(scriptType, BlockCoords.getFullCoords(location));
	}

	public void removeTimes(ScriptType scriptType, String fullCoords) {
		delays.remove(scriptType, fullCoords);
		Set<TimeData> set = new HashSet<>();
		for (TimeData timeData : cooldowns) {
			ScriptType tScriptType = timeData.getScriptType();
			if (timeData.getFullCoords().equals(fullCoords) && (tScriptType == null || tScriptType == scriptType)) {
				set.add(timeData);
			}
		}
		set.forEach(cooldowns::remove);
	}
}