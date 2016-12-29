package com.github.yuttyann.scriptblockplus.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import com.github.yuttyann.scriptblockplus.file.Files;
import com.github.yuttyann.scriptblockplus.file.Yaml;
import com.github.yuttyann.scriptblockplus.util.Utils;

public class MapManager {

	private static HashMap<String, ArrayList<UUID>> delay;
	private static HashMap<String, ArrayList<UUID>> cooldown;
	private static HashMap<String, HashMap<UUID, long[]>> cooldownParams;
	private static ArrayList<String> interactCoords;
	private static ArrayList<String> walkCoords;

	public MapManager() {
		delay = new HashMap<String, ArrayList<UUID>>();
		cooldown = new HashMap<String, ArrayList<UUID>>();
		cooldownParams = new HashMap<String, HashMap<UUID, long[]>>();
		interactCoords = new ArrayList<String>();
		walkCoords = new ArrayList<String>();
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

	public static ArrayList<String> getWalkCoords() {
		return walkCoords;
	}

	public static void putAllScripts() {
		try {
			Yaml interact = Files.getInteract();
			StringBuilder builder = new StringBuilder();
			for (String world : Utils.getConfigSection(interact, "", false)) {
				for (String coords : Utils.getConfigSection(interact, world, false)) {
					interactCoords.add(builder.append(world).append(", ").append(coords).toString());
					builder.setLength(0);
				}
			}
			Yaml walk = Files.getWalk();
			for (String world : Utils.getConfigSection(walk, "", false)) {
				for (String coords : Utils.getConfigSection(walk, world, false)) {
					walkCoords.add(builder.append(world).append(", ").append(coords).toString());
					builder.setLength(0);
				}
			}
		} catch (Exception e) {
			interactCoords.clear();
			walkCoords.clear();
		}
	}
}
