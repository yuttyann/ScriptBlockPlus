package com.github.yuttyann.scriptblockplus.file.config;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class ConfigAdapter<T> {

    private final Map<String, Object> map;

    public ConfigAdapter(@NotNull Map<String, Object> map) {
        this.map = map;
    }

    @SuppressWarnings("unchecked")
    @NotNull
    public T get(@NotNull String key, @NotNull T def) {
        Object value = map.get(key);
        return value == null ? def : (T) value;
    }
}
