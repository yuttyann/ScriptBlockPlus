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
import com.github.yuttyann.scriptblockplus.file.yaml.YamlConfig;
import com.github.yuttyann.scriptblockplus.manager.auxiliary.SBMap;
import com.github.yuttyann.scriptblockplus.script.option.time.Cooldown;
import com.github.yuttyann.scriptblockplus.script.option.time.OldCooldown;
import com.github.yuttyann.scriptblockplus.utils.FileUtils;
import com.github.yuttyann.scriptblockplus.utils.StreamUtils;

public final class MapManager {

	private ScriptBlock plugin;
	private SBMap<List<UUID>> delayMap;
	private SBMap<Map<UUID, Cooldown>> cooldownMap;
	private Map<String, OldCooldown> oldCooldownMap;
	private Map<ScriptType, Set<String>> scriptCoords;

	public MapManager(ScriptBlock plugin) {
		this.plugin = plugin;
		this.delayMap = new SBMap<List<UUID>>();
		this.cooldownMap = new SBMap<Map<UUID, Cooldown>>();
		this.oldCooldownMap = new HashMap<String, OldCooldown>();
		this.scriptCoords = new HashMap<ScriptType, Set<String>>();
	}

	public SBMap<List<UUID>> getDelayMap() {
		return delayMap;
	}

	public SBMap<Map<UUID, Cooldown>> getCooldownMap() {
		return cooldownMap;
	}

	public Map<String, OldCooldown> getOldCooldownMap() {
		return oldCooldownMap;
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
		if (cooldownMap.size() > 0) {
			File cooldownFile = new File(plugin.getDataFolder(), "scripts/cooldown.dat");
			SBMap<Map<UUID, Map<String, Object>>> map = new SBMap<Map<UUID, Map<String, Object>>>();
			try {
				cooldownMap.forEach((s, m) -> {
					Map<UUID, Map<String, Object>> value = new HashMap<UUID, Map<String, Object>>();
					m.forEach((u, c) -> {
						value.put(u, c.serialize());
					});
					if (value.size() > 0) {
						map.put(s.getKey(), s.getValue(), value);
					}
				});
			} catch (Exception e) {
				map.clear();
			} finally {
				if (map.size() > 0) {
					FileUtils.saveFile(cooldownFile, map);
				}
			}
		}
	}

	public void saveOldCooldown() {
		if (oldCooldownMap.size() > 0) {
			File oldCooldownFile = new File(plugin.getDataFolder(), "scripts/oldcooldown.dat");
			Map<String, Map<String, Object>> map = new HashMap<String, Map<String, Object>>();
			try {
				oldCooldownMap.forEach((s, m) -> map.put(s, m.serialize()));
			} catch (Exception e) {
				map.clear();
			} finally {
				if (map.size() > 0) {
					FileUtils.saveFile(oldCooldownFile, map);
				}
			}
		}
	}

	public void loadCooldown() {
		File cooldownFile = new File(plugin.getDataFolder(), "scripts/cooldown.dat");
		if (cooldownFile.exists()) {
			try {
				SBMap<Map<UUID, Map<String, Object>>> map = FileUtils.loadFile(cooldownFile);
				map.forEach((s, v) -> v.forEach((u, m) -> new Cooldown().deserialize(plugin, this, m)));
			} catch (Exception e) {
				cooldownMap = new SBMap<Map<UUID, Cooldown>>();
			} finally {
				cooldownFile.delete();
			}
		}
	}

	public void loadOldCooldown() {
		File oldCooldownFile = new File(plugin.getDataFolder(), "scripts/oldcooldown.dat");
		if (oldCooldownFile.exists()) {
			try {
				Map<String, Map<String, Object>> map = FileUtils.loadFile(oldCooldownFile);
				map.forEach((s, m) -> new OldCooldown().deserialize(plugin, this, m));
			} catch (Exception e) {
				oldCooldownMap = new HashMap<String, OldCooldown>();
			} finally {
				oldCooldownFile.delete();
			}
		}
	}

	public void putDelay(ScriptType scriptType, String fullCoords, UUID uuid) {
		List<UUID> uuids = delayMap.get(scriptType, fullCoords);
		if (uuids == null) {
			uuids = new ArrayList<UUID>();
			delayMap.put(scriptType, fullCoords, uuids);
		}
		uuids.add(uuid);
	}

	public void removeDelay(ScriptType scriptType, String fullCoords, UUID uuid) {
		List<UUID> uuids = delayMap.get(scriptType, fullCoords);
		if (uuids != null) {
			uuids.remove(uuid);
		}
	}

	public boolean containsDelay(ScriptType scriptType, String fullCoords, UUID uuid) {
		List<UUID> uuids = delayMap.get(scriptType, fullCoords);
		return uuids != null && uuids.contains(uuid);
	}

	public void putCooldown(ScriptType scriptType, String fullCoords, UUID uuid, Cooldown cooldown) {
		Map<UUID, Cooldown> cooldowns = cooldownMap.get(scriptType, fullCoords);
		if (cooldowns == null) {
			cooldowns = new HashMap<UUID, Cooldown>();
			cooldownMap.put(scriptType, fullCoords, cooldowns);
		}
		cooldowns.put(uuid, cooldown);
	}

	public void removeCooldown(ScriptType scriptType, String fullCoords, UUID uuid) {
		Map<UUID, Cooldown> cooldowns = cooldownMap.get(scriptType, fullCoords);
		if (cooldowns != null && cooldowns.containsKey(uuid)) {
			cooldowns.remove(uuid);
		}
	}

	public void addCoords(ScriptType scriptType, Location location) {
		String fullCoords = BlockCoords.getFullCoords(location);
		Set<String> set = scriptCoords.get(scriptType);
		if (set != null && !set.contains(fullCoords)) {
			set.add(fullCoords);
		}
		removeTimes(scriptType, fullCoords);
	}

	public void removeCoords(ScriptType scriptType, Location location) {
		String fullCoords = BlockCoords.getFullCoords(location);
		Set<String> set = scriptCoords.get(scriptType);
		if (set != null && set.contains(fullCoords)) {
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
		if (delayMap != null && delayMap.containsKey(scriptType, fullCoords)) {
			delayMap.remove(scriptType, fullCoords);
		}
		if (cooldownMap != null && cooldownMap.containsKey(scriptType, fullCoords)) {
			cooldownMap.remove(scriptType, fullCoords);
		}
		if (oldCooldownMap != null && oldCooldownMap.containsKey(fullCoords)) {
			oldCooldownMap.remove(fullCoords);
		}
	}
}