package com.github.yuttyann.scriptblockplus.file.config;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class ConfigAdapter<T> {

    private final Map<String, Object> map;

    public ConfigAdapter(@NotNull Map<String, Object> map) {
        this.map = map;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public T get(@NotNull String key) {
        return (T) map.get(key);
    }

    @NotNull
    public T get(@NotNull String key, @NotNull T def) {
        T value = get(key);
        return value == null ? def : value;
    }
}
