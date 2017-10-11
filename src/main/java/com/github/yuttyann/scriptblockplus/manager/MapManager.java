package com.github.yuttyann.scriptblockplus.manager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Location;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.enums.ScriptType;
import com.github.yuttyann.scriptblockplus.file.Files;
import com.github.yuttyann.scriptblockplus.file.YamlConfig;
import com.github.yuttyann.scriptblockplus.script.option.time.Cooldown;
import com.github.yuttyann.scriptblockplus.utils.FileUtils;
import com.github.yuttyann.scriptblockplus.utils.StreamUtils;

public class MapManager {

	private ScriptBlock plugin;
	private Map<ScriptType, Set<String>> scriptLocation;
	private Map<ScriptType, Map<String, List<UUID>>> delayScripts;
	private Map<ScriptType, Map<String, Map<UUID, Cooldown>>> cooldownScripts;

	public MapManager(ScriptBlock plugin) {
		this.plugin = plugin;
		this.scriptLocation = new HashMap<ScriptType, Set<String>>();
		this.delayScripts = new HashMap<ScriptType, Map<String, List<UUID>>>();
		this.cooldownScripts = new HashMap<ScriptType, Map<String, Map<UUID, Cooldown>>>();
	}

	public Map<ScriptType, Set<String>> getScriptLocation() {
		return scriptLocation;
	}

	public Map<ScriptType, Map<String, List<UUID>>> getDelayScripts() {
		return delayScripts;
	}

	public Map<ScriptType, Map<String, Map<UUID, Cooldown>>> getCooldownScripts() {
		return cooldownScripts;
	}

	public void loadAllScripts() {
		try {
			StreamUtils.forEach(ScriptType.values(), s -> loadScripts(Files.getScriptFile(s), s));
		} catch (Exception e) {
			scriptLocation.clear();
		}
	}

	public void loadScripts(YamlConfig scriptFile, ScriptType scriptType) {
		Set<String> set = new HashSet<String>();
		scriptFile.getKeys().forEach(w -> scriptFile.getKeys(w).forEach(c -> set.add(w + ", " + c)));
		scriptLocation.put(scriptType, set);
	}

	public void saveCooldown() {
		if (cooldownScripts.size() > 0) {
			File cooldownFile = new File(plugin.getDataFolder(), "scripts/cooldown.dat");
			try {
				FileUtils.saveFile(cooldownFile, cooldownScripts);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void loadCooldown() {
		File cooldownFile = new File(plugin.getDataFolder(), "scripts/cooldown.dat");
		if (cooldownFile.exists()) {
			try {
				cooldownScripts = FileUtils.loadFile(cooldownFile);
				StreamUtils.mapForEach(ScriptType.values(),
					s -> cooldownScripts.get(s).values(),
						cm -> cm.forEach(m -> m.values().forEach(c -> c.start(0L, 20L))));
			} catch (Exception e) {
				cooldownScripts = new HashMap<ScriptType, Map<String, Map<UUID, Cooldown>>>();
			} finally {
				cooldownFile.delete();
			}
		}
	}

	public void putCooldown(ScriptType scriptType, String fullCoords, UUID uuid, Cooldown cooldown) {
		Map<String, Map<UUID, Cooldown>> cooldownMap = cooldownScripts.get(scriptType);
		if (cooldownMap == null) {
			cooldownMap = new HashMap<String, Map<UUID, Cooldown>>();
		}
		Map<UUID, Cooldown> cooldowns = cooldownMap.get(fullCoords);
		if (cooldowns == null) {
			cooldowns = new HashMap<UUID, Cooldown>();
		}
		cooldowns.put(uuid, cooldown);
		cooldownMap.put(fullCoords, cooldowns);
		cooldownScripts.put(scriptType, cooldownMap);
	}

	public void removeCooldown(ScriptType scriptType, String fullCoords, UUID uuid) {
		Map<String, Map<UUID, Cooldown>> cooldownMap = cooldownScripts.get(scriptType);
		Map<UUID, Cooldown> cooldowns = cooldownMap == null ? null : cooldownMap.get(fullCoords);
		if (cooldowns != null && cooldowns.containsKey(uuid)) {
			cooldowns.remove(uuid);
			cooldownMap.put(fullCoords, cooldowns);
			cooldownScripts.put(scriptType, cooldownMap);
		}
	}

	public void putDelay(ScriptType scriptType, String fullCoords, UUID uuid) {
		Map<String, List<UUID>> delayMap = delayScripts.get(scriptType);
		if (delayMap == null) {
			delayMap = new HashMap<String, List<UUID>>();
		}
		List<UUID> uuids = delayMap.get(fullCoords);
		if (uuids == null) {
			uuids = new ArrayList<UUID>();
		}
		uuids.add(uuid);
		delayScripts.put(scriptType, createDelayMap(fullCoords, uuids));
	}

	public void removeDelay(ScriptType scriptType, String fullCoords, UUID uuid) {
		Map<String, List<UUID>> delayMap = delayScripts.get(scriptType);
		List<UUID> uuids = delayMap == null ? null : delayMap.get(fullCoords);
		if (uuids != null) {
			uuids.remove(uuid);
			delayScripts.put(scriptType, createDelayMap(fullCoords, uuids));
		}
	}

	public boolean containsDelay(ScriptType scriptType, String fullCoords, UUID uuid) {
		Map<String, List<UUID>> delayMap = delayScripts.get(scriptType);
		List<UUID> uuids = delayMap == null ? null : delayMap.get(fullCoords);
		return uuids != null && uuids.contains(uuid);
	}

	public void addLocation(ScriptType scriptType, Location location) {
		String fullCoords = BlockCoords.getFullCoords(location);
		Set<String> locationSet = scriptLocation.get(scriptType);
		if (locationSet != null && !locationSet.contains(fullCoords)) {
			locationSet.add(fullCoords);
			scriptLocation.put(scriptType, locationSet);
		}
		removeTimes(scriptType, location);
	}

	public void removeLocation(ScriptType scriptType, Location location) {
		String fullCoords = BlockCoords.getFullCoords(location);
		Set<String> locationSet = scriptLocation.get(scriptType);
		if (locationSet != null && locationSet.contains(fullCoords)) {
			locationSet.remove(fullCoords);
			scriptLocation.put(scriptType, locationSet);
		}
		removeTimes(scriptType, location);
	}

	public boolean containsLocation(ScriptType scriptType, Location location) {
		Set<String> locationSet = scriptLocation.get(scriptType);
		return locationSet != null && locationSet.contains(BlockCoords.getFullCoords(location));
	}

	public void removeTimes(ScriptType scriptType, Location location) {
		String fullCoords = BlockCoords.getFullCoords(location);
		Map<String, List<UUID>> delayMap = delayScripts.get(scriptType);
		if (delayMap != null && delayMap.containsKey(fullCoords)) {
			delayMap.remove(fullCoords);
			delayScripts.put(scriptType, delayMap);
		}
		Map<String, Map<UUID, Cooldown>> cooldownMap = cooldownScripts.get(scriptType);
		if (cooldownMap != null && cooldownMap.containsKey(fullCoords)) {
			cooldownMap.remove(fullCoords);
			cooldownScripts.put(scriptType, cooldownMap);
		}
	}

	private Map<String, List<UUID>> createDelayMap(String fullCoords, List<UUID> uuids) {
		Map<String, List<UUID>> map = new HashMap<String, List<UUID>>();
		map.put(fullCoords, uuids);
		return map;
	}
}