package com.github.yuttyann.scriptblockplus.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import com.github.yuttyann.scriptblockplus.enums.ScriptType;
import com.github.yuttyann.scriptblockplus.file.Files;
import com.github.yuttyann.scriptblockplus.file.Yaml;
import com.github.yuttyann.scriptblockplus.utils.BlockLocation;

public class MapManager {

	private static HashMap<UUID, String> oldLocation;
	private static HashMap<String, ArrayList<UUID>> delay;
	private static HashMap<String, ArrayList<UUID>> cooldown;
	private static HashMap<String, HashMap<UUID, long[]>> cooldownParams;
	private static ArrayList<UUID> interactEvents;
	private static ArrayList<String> interactCoords;
	private static ArrayList<String> breakCoords;
	private static ArrayList<String> walkCoords;

	public MapManager() {
		oldLocation = new HashMap<UUID, String>();
		delay = new HashMap<String, ArrayList<UUID>>();
		cooldown = new HashMap<String, ArrayList<UUID>>();
		cooldownParams = new HashMap<String, HashMap<UUID, long[]>>();
		interactEvents = new ArrayList<UUID>();
		interactCoords = new ArrayList<String>();
		breakCoords = new ArrayList<String>();
		walkCoords = new ArrayList<String>();
		MapManager.putAllScripts();
	}

	public static HashMap<UUID, String> getOldLocation() {
		return oldLocation;
	}

	public static HashMap<String, ArrayList<UUID>> getDelay() {
		return delay;
	}

	public static HashMap<String, ArrayList<UUID>> getCooldown() {
		return cooldown;
	}

	public static HashMap<String, HashMap<UUID, long[]>> getCooldownParams() {
		return cooldownParams;
	}

	public static ArrayList<String> getInteractCoords() {
		return interactCoords;
	}

	public static ArrayList<String> getBreakCoords() {
		return breakCoords;
	}

	public static ArrayList<String> getWalkCoords() {
		return walkCoords;
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

	public static void putAllScripts() {
		try {
			coordsAllClear();
			StringBuilder builder = new StringBuilder();
			Yaml interact = Files.getInteract();
			for (String world : interact.getConfigurationSection("").getKeys(false)) {
				for (String coords : interact.getConfigurationSection(world).getKeys(false)) {
					interactCoords.add(builder.append(world).append(", ").append(coords).toString());
					builder.setLength(0);
				}
			}
			Yaml break_ = Files.getWalk();
			for (String world : break_.getConfigurationSection("").getKeys(false)) {
				for (String coords : break_.getConfigurationSection(world).getKeys(false)) {
					breakCoords.add(builder.append(world).append(", ").append(coords).toString());
					builder.setLength(0);
				}
			}
			Yaml walk = Files.getWalk();
			for (String world : walk.getConfigurationSection("").getKeys(false)) {
				for (String coords : walk.getConfigurationSection(world).getKeys(false)) {
					walkCoords.add(builder.append(world).append(", ").append(coords).toString());
					builder.setLength(0);
				}
			}
		} catch (Exception e) {
			coordsAllClear();
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
		walkCoords.clear();
	}
}