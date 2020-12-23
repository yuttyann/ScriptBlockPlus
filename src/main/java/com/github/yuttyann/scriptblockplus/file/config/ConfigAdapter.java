package com.github.yuttyann.scriptblockplus.file.config;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * ScriptBlockPlus ConfigAdapter クラス
 * @param <T> 値の型
 * @author yuttyann44581
 */
public class ConfigAdapter<T> {

    private final Map<String, Object> map;

    public ConfigAdapter(@NotNull Map<String, Object> map) {
        this.map = map;
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public T get(@NotNull String key) {
        return (T) map.get(key);
    }

    @NotNull
    public T get(@NotNull String key, @NotNull T def) {
        T value = get(key);
        return value == null ? def : value;
    }
}
