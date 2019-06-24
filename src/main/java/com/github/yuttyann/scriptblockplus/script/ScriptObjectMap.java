package com.github.yuttyann.scriptblockplus.script;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.github.yuttyann.scriptblockplus.listener.IAssist;
import com.github.yuttyann.scriptblockplus.player.ObjectMap;

abstract class ScriptObjectMap extends IAssist implements ObjectMap {

	private static final Map<UUID, Map<String, Object>> OBJECT_MAP = new HashMap<>();

	private final UUID ramdomId = UUID.randomUUID();

	ScriptObjectMap(IAssist iAssist) {
		super(iAssist);
	}

	@Override
	public void put(String key, Object value) {
		Map<String, Object> map = OBJECT_MAP.get(ramdomId);
		if (map == null) {
			OBJECT_MAP.put(ramdomId, map = new HashMap<>());
		}
		map.put(key, value);
	}

	@Override
	public <T> T get(String key) {
		Map<String, Object> map = OBJECT_MAP.get(ramdomId);
		return map == null ? null : (T) map.get(key);
	}

	@Override
	public <T> T remove(String key) {
		Map<String, Object> map = OBJECT_MAP.get(ramdomId);
		return map == null ? null : (T) map.remove(key);
	}

	@Override
	public boolean containsKey(String key) {
		Map<String, Object> map = OBJECT_MAP.get(ramdomId);
		return map != null && map.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		Map<String, Object> map = OBJECT_MAP.get(ramdomId);
		return map != null && map.containsValue(value);
	}

	@Override
	public void clear() {
		OBJECT_MAP.remove(ramdomId);
	}
}