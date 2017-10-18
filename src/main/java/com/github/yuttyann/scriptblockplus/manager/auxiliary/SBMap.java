package com.github.yuttyann.scriptblockplus.manager.auxiliary;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;

import com.github.yuttyann.scriptblockplus.enums.ScriptType;

public final class SBMap<T> implements Serializable {

	public class Storage<K, V> {

		private K key;
		private V value;

		public Storage(K key, V value) {
			this.key = key;
			this.value = value;
		}

		public K getKey() {
			return key;
		}

		public V getValue() {
			return value;
		}
	}

	private Map<ScriptType, Map<String, T>> storageMap;

	public SBMap() {
		this.storageMap = new HashMap<ScriptType, Map<String, T>>();
	}

	public int size() {
		return storageMap.size();
	}

	public boolean isEmpty() {
		return storageMap.isEmpty();
	}

	public void clear() {
		storageMap.clear();
	}

	public Map<String, T> get(ScriptType key) {
		return storageMap.get(key);
	}

	public T get(ScriptType scriptType, String fullCoords) {
		Map<String, T> map = get(scriptType);
		return map == null ? null : map.get(fullCoords);
	}

	public Map<String, T> put(ScriptType key, Map<String, T> value) {
		return storageMap.put(key, value);
	}

	public Map<String, T> put(ScriptType scriptType, String fullCoords, T value) {
		Map<String, T> map = get(scriptType);
		if (map == null) {
			map = new HashMap<String, T>();
		}
		map.put(fullCoords, value);
		return storageMap.put(scriptType, map);
	}

	public Map<String, T> remove(ScriptType scriptType) {
		return storageMap.remove(scriptType);
	}

	public Map<String, T> remove(ScriptType scriptType, String fullCoords) {
		Map<String, T> map = get(scriptType);
		if (map != null && map.containsKey(fullCoords)) {
			map.remove(fullCoords);
			return storageMap.put(scriptType, map);
		}
		return null;
	}

	public boolean containsKey(ScriptType scriptType, String fullCoords) {
		Map<String, T> map = storageMap.get(scriptType);
		return map == null ? false : map.containsKey(fullCoords);
	}

	public void forEach(BiConsumer<Storage<ScriptType, String>, T> action) {
		Storage<ScriptType, String> storage = new Storage<ScriptType, String>(null, null);
		for (Entry<ScriptType, Map<String, T>> entry1 : storageMap.entrySet()) {
			for (Entry<String, T> entry2 : entry1.getValue().entrySet()) {
				storage.key = entry1.getKey();
				storage.value = entry2.getKey();
				action.accept(storage, entry2.getValue());
			}
		}
	}

	public Map<ScriptType, Map<String, T>> getMap() {
		return storageMap;
	}
}