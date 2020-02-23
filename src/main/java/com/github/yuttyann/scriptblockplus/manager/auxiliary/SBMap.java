package com.github.yuttyann.scriptblockplus.manager.auxiliary;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.github.yuttyann.scriptblockplus.script.ScriptType;

public final class SBMap<T> implements Serializable {

	private Map<ScriptType, Map<String, T>> sbMap;

	public SBMap() {
		this(0);
	}

	public SBMap(int initialCapacity) {
		this.sbMap = initialCapacity > 0 ? new HashMap<>(initialCapacity) : new HashMap<>();
	}

	public int size() {
		return sbMap.size();
	}

	public boolean isEmpty() {
		return sbMap.isEmpty();
	}

	public void clear() {
		sbMap.clear();
	}

	@Nullable
	public Map<String, T> get(@NotNull ScriptType key) {
		return sbMap.get(key);
	}

	@Nullable
	public T get(@NotNull String fullCoords, @NotNull ScriptType scriptType) {
		Map<String, T> map = get(scriptType);
		return map == null ? null : map.get(fullCoords);
	}

	@Nullable
	public Map<String, T> put(@NotNull ScriptType key, @Nullable Map<String, T> value) {
		return sbMap.put(key, value);
	}

	@Nullable
	public T put(@NotNull String fullCoords, @NotNull ScriptType scriptType, T value) {
		Map<String, T> map = get(scriptType);
		if (map == null) {
			sbMap.put(scriptType, map = new HashMap<>());
		}
		return map.put(fullCoords, value);
	}

	@Nullable
	public Map<String, T> remove(@NotNull ScriptType scriptType) {
		return sbMap.remove(scriptType);
	}

	@Nullable
	public Map<String, T> remove(@NotNull String fullCoords, @NotNull ScriptType scriptType) {
		Map<String, T> map = get(scriptType);
		if (map != null) {
			map.remove(fullCoords);
			return sbMap.put(scriptType, map);
		}
		return null;
	}

	public boolean containsKey(@NotNull ScriptType scriptType) {
		return sbMap.containsKey(scriptType);
	}

	public boolean containsKey(@NotNull String fullCoords, @NotNull ScriptType scriptType) {
		Map<String, T> map = sbMap.get(scriptType);
		return map == null ? false : map.containsKey(fullCoords);
	}

	@NotNull
	public Map<ScriptType, Map<String, T>> getMap() {
		return sbMap;
	}
}