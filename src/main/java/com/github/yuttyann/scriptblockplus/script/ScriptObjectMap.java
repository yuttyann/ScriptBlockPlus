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
	public void setData(String key, Object value) {
		if (!OBJECT_MAP.containsKey(scriptId)) {
			OBJECT_MAP.put(scriptId, new HashMap<>());
		}
		OBJECT_MAP.get(scriptId).put(key, value);
	}

	@Override
	public byte getByte(String key) {
		return getData(key, Byte.class);
	}

	@Override
	public short getShort(String key) {
		return getData(key, Short.class);
	}

	@Override
	public int getInt(String key) {
		return getData(key, Integer.class);
	}

	@Override
	public long getLong(String key) {
		return getData(key, Byte.class);
	}

	@Override
	public char getChar(String key) {
		return getData(key, Character.class);
	}

	@Override
	public float getFloat(String key) {
		return getData(key, Float.class);
	}

	@Override
	public double getDouble(String key) {
		return getData(key, Double.class);
	}

	@Override
	public boolean getBoolean(String key) {
		return getData(key, Boolean.class);
	}

	@Override
	public String getString(String key) {
		return getData(key, String.class);
	}

	@Override
	public <T> T getData(String key) {
		return getData(key, null);
	}

	@Override
	public <T> T getData(String key, Class<T> classOfT) {
		Map<String, Object> map = OBJECT_MAP.get(scriptId);
		return map == null ? null : classOfT == null ? (T) map.get(key) : classOfT.cast(map.get(key));
	}

	@Override
	public <T> T removeData(String key) {
		return removeData(key, null);
	}

	@Override
	public <T> T removeData(String key, Class<T> classOfT) {
		Map<String, Object> map = OBJECT_MAP.get(scriptId);
		return map == null ? null : classOfT == null ? (T) map.remove(key) : classOfT.cast(map.remove(key));
	}

	@Override
	public boolean hasData(String key) {
		return getData(key) != null;
	}

	@Override
	public void clearData() {
		OBJECT_MAP.remove(scriptId);
	}
}