package com.github.yuttyann.scriptblockplus.file.config;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class ConfigData {

    private final String key;
    private final Object value;

    public ConfigData(@NotNull String key, @Nullable Object value) {
        this.key = key;
        this.value = value;
    }

    @NotNull
    public String getKey() {
        return key;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public <T> T getValue(@Nullable T def) {
        Object obj = value;
        return obj == null ? def : (T) obj;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof String) {
            return Objects.equals(key, o);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, value);
    }
}