package com.github.yuttyann.scriptblockplus.manager;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.file.Files;
import com.github.yuttyann.scriptblockplus.file.yaml.YamlConfig;
import com.github.yuttyann.scriptblockplus.manager.auxiliary.SBMap;
import com.github.yuttyann.scriptblockplus.script.ScriptType;
import com.github.yuttyann.scriptblockplus.script.option.time.TimeData;
import com.github.yuttyann.scriptblockplus.utils.FileUtils;
import com.github.yuttyann.scriptblockplus.utils.StreamUtils;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.*;

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

	@NotNull
	public SBMap<Set<UUID>> getDelays() {
		return delays;
	}

	@NotNull
	public Set<TimeData> getCooldowns() {
		return cooldowns;
	}

	@NotNull
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

	public void loadScripts(@NotNull YamlConfig scriptFile, @NotNull ScriptType scriptType) {
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

	public void putDelay(@NotNull UUID uuid, @NotNull String fullCoords, @NotNull ScriptType scriptType) {
		Set<UUID> set = delays.get(fullCoords, scriptType);
		if (set == null) {
			delays.put(fullCoords, scriptType, set = new HashSet<>());
		}
		set.add(uuid);
	}

	public void removeDelay(@NotNull UUID uuid, @NotNull String fullCoords, @NotNull ScriptType scriptType) {
		Set<UUID> set = delays.get(fullCoords, scriptType);
		if (set != null) {
			set.remove(uuid);
		}
	}

	public boolean containsDelay(@NotNull UUID uuid, @NotNull String fullCoords, @NotNull ScriptType scriptType) {
		Set<UUID> set = delays.get(fullCoords, scriptType);
		return set != null && set.contains(uuid);
	}

	public void addCoords(@NotNull Location location, @NotNull ScriptType scriptType) {
		String fullCoords = BlockCoords.getFullCoords(location);
		Set<String> set = scriptCoords.get(scriptType);
		if (set != null) {
			set.add(fullCoords);
		}
		removeTimes(fullCoords, scriptType);
	}

	public void removeCoords(@NotNull Location location, @NotNull ScriptType scriptType) {
		String fullCoords = BlockCoords.getFullCoords(location);
		Set<String> set = scriptCoords.get(scriptType);
		if (set != null) {
			set.remove(fullCoords);
		}
		removeTimes(fullCoords, scriptType);
	}

	public boolean containsCoords(@NotNull Location location, @NotNull ScriptType scriptType) {
		String fullCoords = BlockCoords.getFullCoords(location);
		Set<String> set = scriptCoords.get(scriptType);
		return set != null && set.contains(fullCoords);
	}

	public void removeTimes(@NotNull Location location, @NotNull ScriptType scriptType) {
		removeTimes(BlockCoords.getFullCoords(location), scriptType);
	}

	public void removeTimes(@NotNull String fullCoords, @NotNull ScriptType scriptType) {
		delays.remove(fullCoords, scriptType);
		Set<TimeData> set = new HashSet<>();
		for (TimeData timeData : cooldowns) {
			ScriptType tScriptType = timeData.getScriptType();
			if ((tScriptType == null || tScriptType == scriptType) && timeData.getFullCoords().equals(fullCoords)) {
				set.add(timeData);
			}
		}
		set.forEach(cooldowns::remove);
	}
}