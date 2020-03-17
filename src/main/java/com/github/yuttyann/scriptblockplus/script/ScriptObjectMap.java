package com.github.yuttyann.scriptblockplus.script;

import com.github.yuttyann.scriptblockplus.listener.ScriptListener;
import com.github.yuttyann.scriptblockplus.player.ObjectMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class ScriptObjectMap extends ScriptListener implements ObjectMap {

	private static final Map<UUID, Map<String, Object>> OBJECT_MAP = new HashMap<>();

	private final UUID ramdomId = UUID.randomUUID();

	protected ScriptObjectMap(@NotNull ScriptListener listener) {
		super(listener);
	}

	@Override
	public void put(@NotNull String key, @Nullable Object value) {
		Map<String, Object> map = OBJECT_MAP.get(ramdomId);
		if (map == null) {
			OBJECT_MAP.put(ramdomId, map = new HashMap<>());
		}
		map.put(key, value);
	}

	@Nullable
	@Override
	public <T> T get(@NotNull String key) {
		Map<String, Object> map = OBJECT_MAP.get(ramdomId);
		return map == null ? null : (T) map.get(key);
	}

	@Nullable
	@Override
	public <T> T remove(@NotNull String key) {
		Map<String, Object> map = OBJECT_MAP.get(ramdomId);
		return map == null ? null : (T) map.remove(key);
	}

	@Override
	public boolean containsKey(@NotNull String key) {
		Map<String, Object> map = OBJECT_MAP.get(ramdomId);
		return map != null && map.containsKey(key);
	}

	@Override
	public boolean containsValue(@NotNull Object value) {
		Map<String, Object> map = OBJECT_MAP.get(ramdomId);
		return map != null && map.containsValue(value);
	}

	@Override
	public void clear() {
		OBJECT_MAP.remove(ramdomId);
	}
}