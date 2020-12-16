package com.github.yuttyann.scriptblockplus.script.option.time;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.script.ScriptType;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * ScriptBlockPlus DelayMap クラス
 * @param <T> 値の型
 * @author yuttyann44581
 */
final class DelayMap<T> implements Serializable {

	private Map<ScriptType, Map<String, T>> sbMap;

	public DelayMap() {
		this.sbMap = new HashMap<>();
	}

	public void clear() {
		sbMap.clear();
	}

	public int size() {
		return sbMap.size();
	}

	public boolean isEmpty() {
		return sbMap.isEmpty();
	}

	@Nullable
	public T get(@NotNull Location location, @NotNull ScriptType scriptType) {
		return sbMap.computeIfAbsent(scriptType, k -> new HashMap<>()).get(BlockCoords.getFullCoords(location));
	}

	@Nullable
	public T put(@NotNull Location location, @NotNull ScriptType scriptType, T value) {
		Map<String, T> map = sbMap.get(scriptType);
		return map == null ? null : map.put(BlockCoords.getFullCoords(location), value);
	}

	@Nullable
	public Map<String, T> remove(@NotNull ScriptType scriptType) {
		return sbMap.remove(scriptType);
	}

	@Nullable
	public T remove(@NotNull Location location, @NotNull ScriptType scriptType) {
		return sbMap.computeIfAbsent(scriptType, k -> new HashMap<>()).remove(BlockCoords.getFullCoords(location));
	}

	public boolean has(@NotNull ScriptType scriptType) {
		return sbMap.get(scriptType) != null;
	}

	public boolean has(@NotNull Location location, @NotNull ScriptType scriptType) {
		return get(location, scriptType) != null;
	}

	public boolean containsKey(@NotNull ScriptType scriptType) {
		return sbMap.containsKey(scriptType);
	}

	public boolean containsKey(@NotNull ScriptType scriptType, @NotNull String fullCoords) {
		return sbMap.computeIfAbsent(scriptType, k -> new HashMap<>()).containsKey(fullCoords);
	}

	@NotNull
	public Map<ScriptType, Map<String, T>> getMap() {
		return sbMap;
	}
}