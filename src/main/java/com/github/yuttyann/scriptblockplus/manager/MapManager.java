package com.github.yuttyann.scriptblockplus.manager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
	private SBMap<List<UUID>> delays;
	private Map<Integer, TimeData> cooldowns;
	private Map<ScriptType, Set<String>> scriptCoords;

	public MapManager(ScriptBlock plugin) {
		this.plugin = plugin;
		this.delays = new SBMap<>();
		this.cooldowns = new HashMap<>();
		this.scriptCoords = new HashMap<>();
	}

	public SBMap<List<UUID>> getDelays() {
		return delays;
	}

	public Map<Integer, TimeData> getCooldowns() {
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
				cooldowns.values().forEach(t -> set.add(t.serialize()));
			} catch (Exception e) {
				set.clear();
			} finally {
				if (set.size() > 0) {
					FileUtils.saveFile(cooldownFile, set);
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
				e.printStackTrace();
				cooldowns = new HashMap<>();
			} finally {
				cooldownFile.delete();
			}
		}
	}

	public void putDelay(ScriptType scriptType, String fullCoords, UUID uuid) {
		List<UUID> uuids = delays.get(scriptType, fullCoords);
		if (uuids == null) {
			uuids = new ArrayList<>();
			delays.put(scriptType, fullCoords, uuids);
		}
		uuids.add(uuid);
	}

	public void removeDelay(ScriptType scriptType, String fullCoords, UUID uuid) {
		List<UUID> uuids = delays.get(scriptType, fullCoords);
		if (uuids != null) {
			uuids.remove(uuid);
		}
	}

	public boolean containsDelay(ScriptType scriptType, String fullCoords, UUID uuid) {
		List<UUID> uuids = delays.get(scriptType, fullCoords);
		return uuids == null ?  false : uuids.contains(uuid);
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
		return set == null ? false : set.contains(fullCoords);
	}

	public void removeTimes(ScriptType scriptType, Location location) {
		removeTimes(scriptType, BlockCoords.getFullCoords(location));
	}

	public void removeTimes(ScriptType scriptType, String fullCoords) {
		delays.remove(scriptType, fullCoords);

		Set<Integer> set = new HashSet<>();
		for (Entry<Integer, TimeData> entry : cooldowns.entrySet()) {
			TimeData timeData = entry.getValue();
			if (timeData.getFullCoords().equals(fullCoords)
					&& timeData.getScriptType() == null ? true : timeData.getScriptType() == scriptType) {
				set.add(entry.getKey());
			}
		}
		set.forEach(cooldowns::remove);
	}
}