package com.github.yuttyann.scriptblockplus.manager;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
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
import com.github.yuttyann.scriptblockplus.enums.ScriptType;
import com.github.yuttyann.scriptblockplus.file.Files;
import com.github.yuttyann.scriptblockplus.file.YamlConfig;
import com.github.yuttyann.scriptblockplus.script.option.Option;
import com.github.yuttyann.scriptblockplus.utils.FileUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public class MapManager {

	private ScriptBlock plugin;
	private List<Option> options;
	private Map<UUID, Double> moneyCosts;
	private Map<UUID, String> oldLocation;
	private Map<ScriptType, Set<String>> scriptLocation;
	private Map<ScriptType, Map<String, List<UUID>>> delayScripts;
	private Map<ScriptType, Map<String, Map<UUID, int[]>>> cooldownScripts;

	public MapManager(ScriptBlock plugin) {
		this.plugin = plugin;
		this.options = new ArrayList<Option>();
		this.moneyCosts = new HashMap<UUID, Double>();
		this.oldLocation = new HashMap<UUID, String>();
		this.scriptLocation = new HashMap<ScriptType, Set<String>>();
		this.delayScripts =  new HashMap<ScriptType, Map<String, List<UUID>>>();
		this.cooldownScripts = new HashMap<ScriptType, Map<String, Map<UUID, int[]>>>();
	}

	public List<Option> getOptions() {
		return options;
	}

	public Map<UUID, Double> getMoneyCosts() {
		return moneyCosts;
	}

	public Map<UUID, String> getOldLocation() {
		return oldLocation;
	}

	public Map<ScriptType, Set<String>> getScriptLocation() {
		return scriptLocation;
	}

	public Map<ScriptType, Map<String, List<UUID>>> getDelayScripts() {
		return delayScripts;
	}

	public Map<ScriptType, Map<String, Map<UUID, int[]>>> getCooldownScripts() {
		return cooldownScripts;
	}

	public void loadAllScripts() {
		try {
			for (ScriptType scriptType : ScriptType.values()) {
				loadScripts(Files.getScriptFile(scriptType), scriptType);
			}
		} catch (Exception e) {
			scriptLocation.clear();
		}
	}

	public void loadScripts(YamlConfig scriptFile, ScriptType scriptType) {
		Set<String> locationSet = new HashSet<String>();
		for (String world : scriptFile.getKeys(false)) {
			for (String coords : scriptFile.getKeys(world, false)) {
				locationSet.add(world + ", " + coords);
			}
		}
		scriptLocation.put(scriptType, locationSet);
	}

	public void saveCooldown() {
		if (cooldownScripts.size() > 0) {
			File cooldownFile = new File(plugin.getDataFolder(), Files.FILE_PATHS[5]);
			try {
				Map<String, Map<UUID, int[]>> cooldownMap;
				for (ScriptType scriptType : cooldownScripts.keySet()) {
					cooldownMap = cooldownScripts.get(scriptType);
					for (String fullCoords : cooldownMap.keySet()) {
						for (Entry<UUID, int[]> entry : cooldownMap.get(fullCoords).entrySet()) {
							int[] params = entry.getValue();
							params[3] = Utils.getTime(Calendar.SECOND);
							putCooldown(entry.getKey(), fullCoords, scriptType, params);
						}
					}
				}
				FileUtils.saveFile(cooldownFile, cooldownScripts);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void loadCooldown() {
		File cooldownFile = new File(plugin.getDataFolder(), Files.FILE_PATHS[5]);
		if (cooldownFile.exists()) {
			Object cooldownData = null;
			try {
				cooldownData = FileUtils.loadFile(cooldownFile);
				cooldownFile.delete();
			} catch (Exception e) {
				e.printStackTrace();
			}
			cooldownScripts = (Map<ScriptType, Map<String, Map<UUID, int[]>>>) cooldownData;
			Map<String, Map<UUID, int[]>> cooldownMap;
			for (ScriptType scriptType : cooldownScripts.keySet()) {
				cooldownMap = cooldownScripts.get(scriptType);
				for (String fullCoords : cooldownMap.keySet()) {
					for (Entry<UUID, int[]> entry : cooldownMap.get(fullCoords).entrySet()) {
						int[] params = entry.getValue();
						if (params[3] > 0) {
							params[2] += Utils.getTime(Calendar.SECOND) - params[3];
							params[3] = 0;
							putCooldown(entry.getKey(), fullCoords, scriptType, params);
						}
					}
				}
			}
		}
	}

	public void putCooldown(UUID uuid, String fullCoords, ScriptType scriptType, int[] params) {
		Map<String, Map<UUID, int[]>> cooldownMap = cooldownScripts.get(scriptType);
		if (cooldownMap == null) {
			cooldownMap = new HashMap<String, Map<UUID,int[]>>();
		}
		Map<UUID, int[]> paramMap = cooldownMap.get(fullCoords);
		if (paramMap == null) {
			paramMap = new HashMap<UUID, int[]>();
		}
		paramMap.put(uuid, params);
		cooldownMap.put(fullCoords, paramMap);
		cooldownScripts.put(scriptType, cooldownMap);
	}

	public void removeCooldown(UUID uuid, String fullCoords, ScriptType scriptType) {
		Map<String, Map<UUID, int[]>> cooldownMap = cooldownScripts.get(scriptType);
		Map<UUID, int[]> params = cooldownMap != null ? cooldownMap.get(fullCoords) : null;
		if (params != null && params.containsKey(uuid)) {
			params.remove(uuid);
			cooldownMap.put(fullCoords, params);
			cooldownScripts.put(scriptType, cooldownMap);
		}
	}

	public void putDelay(UUID uuid, String fullCoords, ScriptType scriptType) {
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

	public void removeDelay(UUID uuid, String fullCoords, ScriptType scriptType) {
		Map<String, List<UUID>> delayMap = delayScripts.get(scriptType);
		List<UUID> uuids = delayMap != null ? delayMap.get(fullCoords) : null;
		if (uuids != null) {
			uuids.remove(uuid);
			delayScripts.put(scriptType, createDelayMap(fullCoords, uuids));
		}
	}

	public boolean containsDelay(UUID uuid, String fullCoords, ScriptType scriptType) {
		Map<String, List<UUID>> delayMap = delayScripts.get(scriptType);
		List<UUID> uuids = delayMap != null ? delayMap.get(fullCoords) : null;
		return uuids != null && uuids.contains(uuid);
	}

	public void addLocation(Location location, ScriptType scriptType) {
		String fullCoords = BlockCoords.getFullCoords(location);
		Set<String> locationSet = scriptLocation.get(scriptType);
		if (locationSet != null && !locationSet.contains(fullCoords)) {
			locationSet.add(fullCoords);
			scriptLocation.put(scriptType, locationSet);
		}
		removeTimes(location, scriptType);
	}

	public void removeLocation(Location location, ScriptType scriptType) {
		String fullCoords = BlockCoords.getFullCoords(location);
		Set<String> locationSet = scriptLocation.get(scriptType);
		if (locationSet != null && locationSet.contains(fullCoords)) {
			locationSet.remove(fullCoords);
			scriptLocation.put(scriptType, locationSet);
		}
		removeTimes(location, scriptType);
	}

	public boolean containsLocation(BlockCoords blockCoords, ScriptType scriptType) {
		Set<String> locationSet = scriptLocation.get(scriptType);
		return locationSet != null && locationSet.contains(blockCoords.getFullCoords());
	}

	public void removeTimes(Location location, ScriptType scriptType) {
		String fullCoords = BlockCoords.getFullCoords(location);
		Map<String, List<UUID>> delayFullCoords = delayScripts.get(scriptType);
		if (delayFullCoords != null && delayFullCoords.containsKey(fullCoords)) {
			delayFullCoords.remove(fullCoords);
			delayScripts.put(scriptType, delayFullCoords);
		}
		Map<String, Map<UUID, int[]>> cooldownFullCoords = cooldownScripts.get(scriptType);
		if (cooldownFullCoords != null && cooldownFullCoords.containsKey(fullCoords)) {
			cooldownFullCoords.remove(fullCoords);
			cooldownScripts.put(scriptType, cooldownFullCoords);
		}
	}

	private Map<String, List<UUID>> createDelayMap(String fullCoords, List<UUID> uuids) {
		Map<String, List<UUID>> value = new HashMap<String, List<UUID>>();
		value.put(fullCoords, uuids);
		return value;
	}
}