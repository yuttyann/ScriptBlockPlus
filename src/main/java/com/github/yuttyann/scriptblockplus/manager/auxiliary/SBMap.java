package com.github.yuttyann.scriptblockplus.manager.auxiliary;

import com.github.yuttyann.scriptblockplus.script.ScriptType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public final class SBMap<T> implements Serializable {

	private Map<ScriptType, Map<String, T>> sbMap;

	public SBMap() {
		this(0);
	}

	public SBMap(int initialCapacity) {
		this.sbMap = initialCapacity > 0 ? new HashMap<>(initialCapacity) : new HashMap<>();
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
	public T get(@NotNull ScriptType scriptType, @NotNull String fullCoords) {
		return sbMap.computeIfAbsent(scriptType, k -> new HashMap<>()).get(fullCoords);
	}

	@Nullable
	public T put(@NotNull ScriptType scriptType, @NotNull String fullCoords, T value) {
		Map<String, T> map = sbMap.get(scriptType);
		return map == null ? null : map.put(fullCoords, value);
	}

	@Nullable
	public Map<String, T> remove(@NotNull ScriptType scriptType) {
		return sbMap.remove(scriptType);
	}

	@Nullable
	public T remove(@NotNull ScriptType scriptType, @NotNull String fullCoords) {
		return sbMap.computeIfAbsent(scriptType, k -> new HashMap<>()).remove(fullCoords);
	}

	public boolean has(@NotNull ScriptType scriptType) {
		return sbMap.get(scriptType) != null;
	}

	public boolean has(@NotNull ScriptType scriptType, @NotNull String fullCoords) {
		return get(scriptType, fullCoords) != null;
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