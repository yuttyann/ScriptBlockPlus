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

	private static Map<UUID, String> oldLocation;
	private static Map<String, List<UUID>> delay;
	private static Map<String, List<UUID>> cooldown;
	private static Map<String, Map<UUID, long[]>> cooldownParams;
	private static Set<String> interactCoords;
	private static Set<String> breakCoords;
	private static Set<String> walkCoords;
	private static List<UUID> interactEvents;

	public MapManager() {
		oldLocation = new HashMap<UUID, String>();
		delay = new HashMap<String, List<UUID>>();
		cooldown = new HashMap<String, List<UUID>>();
		cooldownParams = new HashMap<String, Map<UUID, long[]>>();
		interactEvents = new ArrayList<UUID>();
		interactCoords = new HashSet<String>();
		breakCoords = new HashSet<String>();
		walkCoords = new HashSet<String>();
		setupScripts();
	}

	public static Map<UUID, String> getOldLocation() {
		return oldLocation;
	}

	public static Map<String, List<UUID>> getDelay() {
		return delay;
	}

	public static Map<String, List<UUID>> getCooldown() {
		return cooldown;
	}

	public static Map<String, Map<UUID, long[]>> getCooldownParams() {
		return cooldownParams;
	}

	public static Set<String> getInteractCoords() {
		return interactCoords;
	}

	public static Set<String> getBreakCoords() {
		return breakCoords;
	}

	public static Set<String> getWalkCoords() {
		return walkCoords;
	}

	public static void setupScripts() {
		try {
			reloadScripts(Files.getInteract(), ScriptType.INTERACT);
			reloadScripts(Files.getBreak(), ScriptType.BREAK);
			reloadScripts(Files.getWalk(), ScriptType.WALK);
		} catch (Exception e) {
			coordsAllClear();
		}
	}

	public static void reloadScripts(YamlConfig scriptFile, ScriptType scriptType) {
		switch (scriptType) {
		case INTERACT:
			interactCoords.clear();
			for (String world : scriptFile.getKeys(false)) {
				for (String coords : scriptFile.getConfigurationSection(world).getKeys(false)) {
					interactCoords.add(world + ", " + coords);
				}
			}
			break;
		case BREAK:
			breakCoords.clear();
			for (String world : scriptFile.getKeys(false)) {
				for (String coords : scriptFile.getConfigurationSection(world).getKeys(false)) {
					breakCoords.add(world + ", " + coords);
				}
			}
			break;
		case WALK:
			walkCoords.clear();
			for (String world : scriptFile.getKeys(false)) {
				for (String coords : scriptFile.getConfigurationSection(world).getKeys(false)) {
					walkCoords.add(world + ", " + coords);
				}
			}
			break;
		}
	}

	public static void addCoords(BlockLocation location, ScriptType scriptType) {
		String fullCoords = location.getFullCoords();
		switch (scriptType) {
		case INTERACT:
			if (!interactCoords.contains(fullCoords)) {
				interactCoords.add(fullCoords);
			}
			break;
		case BREAK:
			if (!breakCoords.contains(fullCoords)) {
				breakCoords.add(fullCoords);
			}
			break;
		case WALK:
			if (!walkCoords.contains(fullCoords)) {
				walkCoords.add(fullCoords);
			}
			break;
		}
	}

	public static void removeCoords(BlockLocation location, ScriptType scriptType) {
		String fullCoords = location.getFullCoords();
		switch (scriptType) {
		case INTERACT:
			if (interactCoords.contains(fullCoords)) {
				interactCoords.remove(fullCoords);
			}
			break;
		case BREAK:
			if (breakCoords.contains(fullCoords)) {
				breakCoords.remove(fullCoords);
			}
			break;
		case WALK:
			if (walkCoords.contains(fullCoords)) {
				walkCoords.remove(fullCoords);
			}
			break;
		}
	}

	public static boolean addEvents(UUID uuid) {
		if (!interactEvents.contains(uuid)) {
			return interactEvents.add(uuid);
		}
		return false;
	}

	public static boolean removeEvents(UUID uuid) {
		if (interactEvents.contains(uuid)) {
			return interactEvents.remove(uuid);
		}
		return false;
	}

	private static void coordsAllClear() {
		interactCoords.clear();
		breakCoords.clear();
		walkCoords.clear();
	}
}