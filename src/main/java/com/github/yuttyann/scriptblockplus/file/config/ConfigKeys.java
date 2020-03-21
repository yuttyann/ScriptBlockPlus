package com.github.yuttyann.scriptblockplus.file.config;

import com.github.yuttyann.scriptblockplus.file.yaml.YamlConfig;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;

public class ConfigKeys {

    private static final Map<String, Object> NODE = new HashMap<>();

    public static void clear() {
        NODE.clear();
    }

    public static void load(@NotNull YamlConfig yaml) {
        yaml.getKeys(true).forEach(s -> NODE.put(s, yaml.get(s)));
    }

    @NotNull
    public static ConfigKey<String> stringKey(@NotNull String key, @NotNull String def) {
        return new ElementKey<>(new ConfigAdapter<>(NODE), key, def);
    }

    @NotNull
    public static ConfigKey<List<String>> stringListKey(@NotNull String key, @NotNull List<String> def) {
        return new ElementKey<>(new ConfigAdapter<>(NODE), key, def);
    }

    @NotNull
    public static ConfigKey<Boolean> booleanKey(@NotNull String key, boolean def) {
        return new ElementKey<>(new ConfigAdapter<>(NODE), key, def);
    }

    @NotNull
    public static <T> CustomKey<T> customKey(@NotNull Function<ConfigAdapter<T>, T> function) {
        return new CustomKey<>(new ConfigAdapter<>(NODE), function);
    }

    @NotNull
    public static ReplaceKey replaceKey(@NotNull ConfigKey<String> configKey, @NotNull String... replaceKeys) {
        return new ReplaceKey(configKey, replaceKeys);
    }

    @NotNull
    public static ReplaceKey replaceKey(@NotNull ConfigKey<String> configKey, @NotNull Function<ReplaceKey, String> function) {
        return new FunctionReplaceKey(configKey, function);
    }

    private static class ElementKey<T> implements ConfigKey<T> {

        private final String key;
        private final ConfigAdapter<T> adapter;

        private T def;

        private ElementKey(@NotNull ConfigAdapter<T> adapter, @NotNull String key, @NotNull T def) {
            this.adapter = adapter;
            this.key = key;
            this.def = def;
        }

        @Override
        @NotNull
        public T get() {
            return adapter.get(key, def);
        }

        @Override
        public String toString() {
            return get().toString();
        }
    }

    private static class CustomKey<T> implements ConfigKey<T> {

        private final ConfigAdapter<T> adapter;
        private final Function<ConfigAdapter<T>, T> function;

        private CustomKey(@NotNull ConfigAdapter<T> adapter, @NotNull Function<ConfigAdapter<T>, T> function) {
            this.adapter = adapter;
            this.function = function;
        }

        @Override
        @NotNull
        public T get() {
            return function.apply(adapter);
        }

        @NotNull
        public T get(@NotNull String key, @NotNull T def) {
            return adapter.get(key, def);
        }

        @Override
        public String toString() {
            return get().toString();
        }
    }

    private static class FunctionReplaceKey extends ReplaceKey {

        private final Function<ReplaceKey, String> function;

        private FunctionReplaceKey(@NotNull ConfigKey<String> configKey, @NotNull Function<ReplaceKey, String> function) {
            super(configKey);
            this.function = function;
        }

        @Override
        @NotNull
        public ReplaceKey replace(@NotNull Object... replaces) {
            FunctionReplaceKey replaceKey = new FunctionReplaceKey(getConfigKey(), function);
            replaceKey.args = replaces;
            replaceKey.result = replaceKey.function.apply(replaceKey);
            return replaceKey;
        }
    }
}