package com.github.yuttyann.scriptblockplus.manager.auxiliary;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

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

	public Map<String, T> get(ScriptType key) {
		return sbMap.get(key);
	}

	public T get(String fullCoords, ScriptType scriptType) {
		Map<String, T> map = get(scriptType);
		return map == null ? null : map.get(fullCoords);
	}

	public Map<String, T> put(ScriptType key, Map<String, T> value) {
		return sbMap.put(key, value);
	}

	public T put(String fullCoords, ScriptType scriptType, T value) {
		Map<String, T> map = get(scriptType);
		if (map == null) {
			sbMap.put(scriptType, map = new HashMap<>());
		}
		return map.put(fullCoords, value);
	}

	public Map<String, T> remove(ScriptType scriptType) {
		return sbMap.remove(scriptType);
	}

	public Map<String, T> remove(String fullCoords, ScriptType scriptType) {
		Map<String, T> map = get(scriptType);
		if (map != null) {
			map.remove(fullCoords);
			return sbMap.put(scriptType, map);
		}
		return null;
	}

	public boolean containsKey(ScriptType scriptType) {
		return sbMap.containsKey(scriptType);
	}

	public boolean containsKey(String fullCoords, ScriptType scriptType) {
		Map<String, T> map = sbMap.get(scriptType);
		return map == null ? false : map.containsKey(fullCoords);
	}

	public Map<ScriptType, Map<String, T>> getMap() {
		return sbMap;
	}
}