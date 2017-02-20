package com.github.yuttyann.scriptblockplus.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.github.yuttyann.scriptblockplus.BlockLocation;
import com.github.yuttyann.scriptblockplus.enums.ScriptType;
import com.github.yuttyann.scriptblockplus.file.Files;
import com.github.yuttyann.scriptblockplus.file.YamlConfig;

public class MapManager {

	private List<UUID> interactEvents;
	private Set<String> interactLocation;
	private Set<String> breakLocation;
	private Set<String> walkLocation;
	private Map<UUID, String> oldLocation;
	private Map<String, List<UUID>> delay;
	private Map<String, Map<UUID, int[]>> cooldown;

	public MapManager() {
		this.interactEvents = new ArrayList<UUID>();
		this.interactLocation = new HashSet<String>();
		this.breakLocation = new HashSet<String>();
		this.walkLocation = new HashSet<String>();
		this.oldLocation = new HashMap<UUID, String>();
		this.delay = new HashMap<String, List<UUID>>();
		this.cooldown = new HashMap<String, Map<UUID, int[]>>();
		reloadAllScripts();
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

	public Map<String, List<UUID>> getDelay() {
		return delay;
	}

	public Map<String, Map<UUID, int[]>> getCooldown() {
		return cooldown;
	}

	public void reloadAllScripts() {
		try {
			delay.clear();
			cooldown.clear();
			reloadScripts(Files.getInteract(), ScriptType.INTERACT);
			reloadScripts(Files.getBreak(), ScriptType.BREAK);
			reloadScripts(Files.getWalk(), ScriptType.WALK);
		} catch (Exception e) {
			locationAllClear();
		}
	}

	public void reloadScripts(YamlConfig scriptFile, ScriptType scriptType) {
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

	public void addLocation(BlockLocation location, ScriptType scriptType) {
		String fullCoords = location.getFullCoords();
		removeTimes(fullCoords);
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
		removeTimes(fullCoords);
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

	public void removeTimes(String fullCoords) {
		if (delay.containsKey(fullCoords)) {
			delay.remove(fullCoords);
		}
		if (cooldown.containsKey(fullCoords)) {
			cooldown.remove(fullCoords);
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

	private void locationAllClear() {
		interactLocation.clear();
		breakLocation.clear();
		walkLocation.clear();
	}
}