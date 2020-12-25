package com.github.yuttyann.scriptblockplus.script;

import com.github.yuttyann.scriptblockplus.player.ObjectMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * ScriptBlockPlus ScriptMap クラス
 * @author yuttyann44581
 */
public abstract class ScriptMap implements ObjectMap {

    private static final Map<UUID, Map<String, Object>> TEMP_MAP = new HashMap<>();

    protected final UUID ramdomId = UUID.randomUUID();

    @Override
    public void put(@NotNull String key, @Nullable Object value) {
        TEMP_MAP.computeIfAbsent(ramdomId, k -> new HashMap<>()).put(key, value);
    }

    @Override
    @Nullable
    @SuppressWarnings("unchecked")
    public <T> T get(@NotNull String key) {
        Map<String, Object> map = TEMP_MAP.get(ramdomId);
        return map == null ? null : (T) map.get(key);
    }

    @Override
    public void remove(@NotNull String key) {
        TEMP_MAP.get(ramdomId).remove(key);
    }

    @Override
    public void clear() {
        TEMP_MAP.remove(ramdomId);
    }
}