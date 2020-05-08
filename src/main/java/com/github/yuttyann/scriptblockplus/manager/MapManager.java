package com.github.yuttyann.scriptblockplus.manager;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.file.Files;
import com.github.yuttyann.scriptblockplus.file.yaml.YamlConfig;
import com.github.yuttyann.scriptblockplus.manager.auxiliary.SBMap;
import com.github.yuttyann.scriptblockplus.script.ScriptType;
import com.github.yuttyann.scriptblockplus.script.option.time.TimerOption;
import com.github.yuttyann.scriptblockplus.utils.StreamUtils;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * ScriptBlockPlus MapManager クラス
 * @author yuttyann44581
 */
public final class MapManager {

	private final SBMap<Set<UUID>> delays;
	private final Map<ScriptType, Set<String>> scriptCoords;

	public MapManager() {
		this.delays = new SBMap<>();
		this.scriptCoords = new HashMap<>();
	}

	@NotNull
	public SBMap<Set<UUID>> getDelays() {
		return delays;
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
		Set<String> set = new HashSet<>(scriptCoords.size());
		scriptFile.getKeys().forEach(w -> scriptFile.getKeys(w).forEach(c -> set.add(w + ", " + c)));
		scriptCoords.put(scriptType, set);
	}

	public void putDelay(@NotNull UUID uuid, @NotNull ScriptType scriptType, @NotNull String fullCoords) {
		Set<UUID> set = delays.get(scriptType, fullCoords);
		if (set == null) {
			delays.put(scriptType, fullCoords, set = new HashSet<>());
		}
		set.add(uuid);
	}

	public void removeDelay(@NotNull UUID uuid, @NotNull ScriptType scriptType, @NotNull String fullCoords) {
		Set<UUID> set = delays.get(scriptType, fullCoords);
		if (set != null) {
			set.remove(uuid);
		}
	}

	public boolean containsDelay(@NotNull UUID uuid, @NotNull ScriptType scriptType, @NotNull String fullCoords) {
		Set<UUID> set = delays.get(scriptType, fullCoords);
		return set != null && set.contains(uuid);
	}

	public void addCoords(@NotNull Location location, @NotNull ScriptType scriptType) {
		String fullCoords = BlockCoords.getFullCoords(location);
		Set<String> set = scriptCoords.get(scriptType);
		if (set != null) {
			set.add(fullCoords);
		}
		TimerOption.removeAll(fullCoords, scriptType);
	}

	public void removeCoords(@NotNull Location location, @NotNull ScriptType scriptType) {
		String fullCoords = BlockCoords.getFullCoords(location);
		Set<String> set = scriptCoords.get(scriptType);
		if (set != null) {
			set.remove(fullCoords);
		}
		TimerOption.removeAll(fullCoords, scriptType);
	}

	public boolean containsCoords(@NotNull Location location, @NotNull ScriptType scriptType) {
		String fullCoords = BlockCoords.getFullCoords(location);
		Set<String> set = scriptCoords.get(scriptType);
		return set != null && set.contains(fullCoords);
	}
}