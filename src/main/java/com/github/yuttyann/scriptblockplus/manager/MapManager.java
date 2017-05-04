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

import com.github.yuttyann.scriptblockplus.BlockLocation;
import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.enums.ScriptType;
import com.github.yuttyann.scriptblockplus.file.Files;
import com.github.yuttyann.scriptblockplus.file.YamlConfig;
import com.github.yuttyann.scriptblockplus.option.Cooldown;
import com.github.yuttyann.scriptblockplus.utils.FileUtils;

public class MapManager {

	private ScriptBlock plugin;
	private List<UUID> interactEvents;
	private Set<String> interactLocation;
	private Set<String> breakLocation;
	private Set<String> walkLocation;
	private Map<UUID, String> oldLocation;
	private Map<ScriptType, Map<String, List<UUID>>> delayScripts;
	private Map<ScriptType, Map<String, Map<UUID, int[]>>> cooldownScripts;

	public MapManager(ScriptBlock plugin) {
		this.plugin = plugin;
		this.interactEvents = new ArrayList<UUID>();
		this.interactLocation = new HashSet<String>();
		this.breakLocation = new HashSet<String>();
		this.walkLocation = new HashSet<String>();
		this.oldLocation = new HashMap<UUID, String>();
		this.delayScripts =  new HashMap<ScriptType, Map<String, List<UUID>>>();
		this.cooldownScripts = new HashMap<ScriptType, Map<String, Map<UUID, int[]>>>();
	}

	public Set<String> getInteractLocation() {
		return interactLocation;
	}

	public Set<String> getBreakLocation() {
		return breakLocation;
	}

	public Set<String> getWalkLocation() {
		return walkLocation;
	}

	public Map<UUID, String> getOldLocation() {
		return oldLocation;
	}

	public Map<ScriptType, Map<String, List<UUID>>> getDelayScripts() {
		return delayScripts;
	}

	public Map<ScriptType, Map<String, Map<UUID, int[]>>> getCooldownScripts() {
		return cooldownScripts;
	}

	public void loadAllScripts() {
		try {
			loadScripts(Files.getInteract(), ScriptType.INTERACT);
			loadScripts(Files.getBreak(), ScriptType.BREAK);
			loadScripts(Files.getWalk(), ScriptType.WALK);
		} catch (Exception e) {
			interactLocation.clear();
			breakLocation.clear();
			walkLocation.clear();
		}
	}

	public void loadScripts(YamlConfig scriptFile, ScriptType scriptType) {
		switch (scriptType) {
		case INTERACT:
			interactLocation.clear();
			for (String world : scriptFile.getKeys(false)) {
				for (String coords : scriptFile.getKeys(world, false)) {
					interactLocation.add(world + ", " + coords);
				}
			}
			break;
		case BREAK:
			breakLocation.clear();
			for (String world : scriptFile.getKeys(false)) {
				for (String coords : scriptFile.getKeys(world, false)) {
					breakLocation.add(world + ", " + coords);
				}
			}
			break;
		case WALK:
			walkLocation.clear();
			for (String world : scriptFile.getKeys(false)) {
				for (String coords : scriptFile.getKeys(world, false)) {
					walkLocation.add(world + ", " + coords);
				}
			}
			break;
		}
	}

	public void saveCooldown() {
		if (cooldownScripts.size() > 0) {
			File cooldownFile = new File(plugin.getDataFolder(), Files.FILE_PATHS[7]);
			try {
				FileUtils.saveFile(cooldownFile, cooldownScripts);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void loadCooldown() {
		File cooldownFile = new File(plugin.getDataFolder(), Files.FILE_PATHS[7]);
		if (cooldownFile.exists()) {
			Object cooldownData = null;
			try {
				cooldownData = FileUtils.loadFile(cooldownFile);
				cooldownFile.delete();
			} catch (Exception e) {
				e.printStackTrace();
			}
			cooldownScripts = (Map<ScriptType, Map<String, Map<UUID, int[]>>>) cooldownData;
			for (ScriptType scriptType : cooldownScripts.keySet()) {
				Map<String, Map<UUID, int[]>> cooldownMap = cooldownScripts.get(scriptType);
				for (Entry<String, Map<UUID, int[]>> entry : cooldownMap.entrySet()) {
					String fullCoords = entry.getKey();
					for (Entry<UUID, int[]> entry2 : cooldownMap.get(fullCoords).entrySet()) {
						int[] params = entry2.getValue();
						Integer result = (params[0] * 3600) + (params[1] * 60) + params[2];
						new Cooldown(plugin, result.toString()).run(scriptType, entry2.getKey(), fullCoords);
					}
				}
			}
		}
	}

	public void addLocation(BlockLocation location, ScriptType scriptType) {
		String fullCoords = location.getFullCoords();
		removeTimes(location, scriptType);
		switch (scriptType) {
		case INTERACT:
			if (!interactLocation.contains(fullCoords)) {
				interactLocation.add(fullCoords);
			}
			break;
		case BREAK:
			if (!breakLocation.contains(fullCoords)) {
				breakLocation.add(fullCoords);
			}
			break;
		case WALK:
			if (!walkLocation.contains(fullCoords)) {
				walkLocation.add(fullCoords);
			}
			break;
		}
	}

	public void removeLocation(BlockLocation location, ScriptType scriptType) {
		String fullCoords = location.getFullCoords();
		removeTimes(location, scriptType);
		switch (scriptType) {
		case INTERACT:
			if (interactLocation.contains(fullCoords)) {
				interactLocation.remove(fullCoords);
			}
			break;
		case BREAK:
			if (breakLocation.contains(fullCoords)) {
				breakLocation.remove(fullCoords);
			}
			break;
		case WALK:
			if (walkLocation.contains(fullCoords)) {
				walkLocation.remove(fullCoords);
			}
			break;
		}
	}

	public void removeTimes(BlockLocation location, ScriptType scriptType) {
		String fullCoords = location.getFullCoords();
		Map<String, List<UUID>> delayFullCoords = delayScripts.get(scriptType);
		Map<String, Map<UUID, int[]>> cooldownFullCoords = cooldownScripts.get(scriptType);
		if (delayFullCoords != null && delayFullCoords.containsKey(fullCoords)) {
			delayFullCoords.remove(fullCoords);
			delayScripts.put(scriptType, delayFullCoords);
		}
		if (cooldownFullCoords != null && cooldownFullCoords.containsKey(fullCoords)) {
			cooldownFullCoords.remove(fullCoords);
			cooldownScripts.put(scriptType, cooldownFullCoords);
		}
	}

	public boolean addEvents(UUID uuid) {
		if (!interactEvents.contains(uuid)) {
			return interactEvents.add(uuid);
		}
		return false;
	}

	public boolean removeEvents(UUID uuid) {
		if (interactEvents.contains(uuid)) {
			return interactEvents.remove(uuid);
		}
		return false;
	}
}