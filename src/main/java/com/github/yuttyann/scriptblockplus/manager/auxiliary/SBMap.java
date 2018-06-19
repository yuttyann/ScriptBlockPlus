package com.github.yuttyann.scriptblockplus.manager.auxiliary;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.BiConsumer;

import com.github.yuttyann.scriptblockplus.enums.ScriptType;

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

	public T get(ScriptType scriptType, String fullCoords) {
		Map<String, T> map = get(scriptType);
		return map == null ? null : map.get(fullCoords);
	}

	public Map<String, T> put(ScriptType key, Map<String, T> value) {
		return sbMap.put(key, value);
	}

	public T put(ScriptType scriptType, String fullCoords, T value) {
		Map<String, T> map = get(scriptType);
		if (map == null) {
			map = new HashMap<>();
			sbMap.put(scriptType, map);
		}
		return map.put(fullCoords, value);
	}

	public Map<String, T> remove(ScriptType scriptType) {
		return sbMap.remove(scriptType);
	}

	public Map<String, T> remove(ScriptType scriptType, String fullCoords) {
		Map<String, T> map = get(scriptType);
		if (map != null) {
			map.remove(fullCoords);
			return sbMap.put(scriptType, map);
		}
		return null;
	}

	public boolean containsKey(ScriptType scriptType, String fullCoords) {
		Map<String, T> map = sbMap.get(scriptType);
		return map == null ? false : map.containsKey(fullCoords);
	}

	public Map<ScriptType, Map<String, T>> getMap() {
		return sbMap;
	}

	public void forEach(BiConsumer<Entry<ScriptType, String>, T> action) {
		SBEntry<ScriptType, String> sbEntry = new SBEntry<ScriptType, String>(null, null);
		for (Entry<ScriptType, Map<String, T>> entry1 : sbMap.entrySet()) {
			for (Entry<String, T> entry2 : entry1.getValue().entrySet()) {
				sbEntry.key = entry1.getKey();
				sbEntry.value = entry2.getKey();
				action.accept(sbEntry, entry2.getValue());
			}
		}
	}

	private class SBEntry<K, V> implements Entry<K, V> {

		private K key;
		private V value;

		private SBEntry(K key, V value) {
			this.key = key;
			this.value = value;
		}

		public K getKey() {
			return key;
		}

		public V getValue() {
			return value;
		}

		@Override
		public V setValue(V value) {
			throw new UnsupportedOperationException();
		}

		@Override
		public int hashCode() {
			return Objects.hashCode(key) ^ Objects.hashCode(value);
		}

		@Override
		public boolean equals(Object o) {
			if (o == this) {
				return true;
			}
			if (o instanceof Entry) {
				Entry<?, ?> entry = (Entry<?, ?>) o;
				return Objects.equals(key, entry.getKey()) && Objects.equals(value, entry.getValue());
			}
			return false;
		}
	}
}