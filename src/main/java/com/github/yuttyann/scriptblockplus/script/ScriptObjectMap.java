package com.github.yuttyann.scriptblockplus.script;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.github.yuttyann.scriptblockplus.listener.IAssist;
import com.github.yuttyann.scriptblockplus.player.ObjectMap;

abstract class ScriptObjectMap extends IAssist implements ObjectMap {

	private static final Map<UUID, Map<String, Object>> OBJECT_MAP = new HashMap<>();

	private final UUID scriptId;

	ScriptObjectMap(IAssist iAssist) {
		super(iAssist);
		this.scriptId = UUID.randomUUID();
	}

	@Override
	public void put(String key, Object value) {
		Map<String, Object> map = OBJECT_MAP.get(scriptId);
		if (map == null) {
			OBJECT_MAP.put(scriptId, map = new HashMap<>());
		}
		map.put(key, value);
	}

	@Override
	public byte getByte(String key) {
		return get(key, Byte.class);
	}

	@Override
	public short getShort(String key) {
		return get(key, Short.class);
	}

	@Override
	public int getInt(String key) {
		return get(key, Integer.class);
	}

	@Override
	public long getLong(String key) {
		return get(key, Byte.class);
	}

	@Override
	public char getChar(String key) {
		return get(key, Character.class);
	}

	@Override
	public float getFloat(String key) {
		return get(key, Float.class);
	}

	@Override
	public double getDouble(String key) {
		return get(key, Double.class);
	}

	@Override
	public boolean getBoolean(String key) {
		return get(key, Boolean.class);
	}

	@Override
	public String getString(String key) {
		return get(key, String.class);
	}

	@Override
	public <T> T get(String key) {
		Map<String, Object> map = OBJECT_MAP.get(scriptId);
		return map == null ? null : (T) map.get(key);
	}

	@Override
	public <T> T get(String key, Class<T> classOfT) {
		return classOfT == null ? get(key) : classOfT.cast(get(key));
	}

	@Override
	public <T> T remove(String key) {
		Map<String, Object> map = OBJECT_MAP.get(scriptId);
		return map == null ? null : (T) map.remove(key);
	}

	@Override
	public <T> T remove(String key, Class<T> classOfT) {
		return classOfT == null ? remove(key) : classOfT.cast(remove(key));
	}

	@Override
	public boolean has(String key) {
		return get(key) != null;
	}

	@Override
	public boolean containsKey(String key) {
		Map<String, Object> map = OBJECT_MAP.get(scriptId);
		return map != null && map.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		Map<String, Object> map = OBJECT_MAP.get(scriptId);
		return map != null && map.containsValue(value);
	}

	@Override
	public void clear() {
		OBJECT_MAP.remove(scriptId);
	}
}