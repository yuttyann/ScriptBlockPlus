/**
 * ScriptBlockPlus - Allow you to add script to any blocks.
 * Copyright (C) 2021 yuttyann44581
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <https://www.gnu.org/licenses/>.
 */
package com.github.yuttyann.scriptblockplus.file.config;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * ScriptBlockPlus ConfigKeys クラス
 * @author yuttyann44581
 */
public final class ConfigKeys {

    private static final ConfigAdapter ADAPTER = new ConfigAdapter(new HashMap<>());

    public static void clear() {
        ADAPTER.clear();
    }

    public static void load(@NotNull YamlConfig yaml) {
        ADAPTER.load(yaml);
    }

    @NotNull
    public static ConfigKey<Integer> integerKey(@NotNull String key, int def) {
        return elementKey(key, def);
    }

    @NotNull
    public static ConfigKey<Boolean> booleanKey(@NotNull String key, boolean def) {
        return elementKey(key, def);
    }

    @NotNull
    public static ConfigKey<String> stringKey(@NotNull String key, @NotNull String def) {
        return elementKey(key, def);
    }

    @NotNull
    public static ConfigKey<List<String>> stringListKey(@NotNull String key, @NotNull List<String> def) {
        return elementKey(key, def);
    }

    @NotNull
    public static <T> ConfigKey<T> elementKey(@NotNull String key, @NotNull T def) {
        return new ElementKey<>(ADAPTER, key, def);
    }

    @NotNull
    public static <T> ConfigKey<T> customKey(@NotNull Function<ConfigAdapter, T> function) {
        return new CustomKey<>(ADAPTER, function);
    }

    @NotNull
    public static ReplaceKey replaceKey(@NotNull String key, @NotNull String def, @NotNull String... replaceKeys) {
        return new ReplaceKey(stringKey(key, def), replaceKeys);
    }

    @NotNull
    public static ReplaceKey replaceKey(@NotNull String key, @NotNull String def, @NotNull Function<ReplaceKey, String> function) {
        return new FunctionalReplaceKey(stringKey(key, def), function);
    }

    private static class ElementKey<T> implements ConfigKey<T> {

        private final String key;
        private final T def;

        private ElementKey(@NotNull ConfigAdapter adapter, @NotNull String key, @NotNull T def) {
            this.key = key;
            this.def = def;
        }

        @Override
        @NotNull
        public Optional<T> get() {
            return Optional.of(ADAPTER.get(key, def));
        }

        @Override
        public String toString() {
            return getValue().toString();
        }
    }

    private static class CustomKey<T> implements ConfigKey<T> {

        private final Function<ConfigAdapter, T> function;

        private CustomKey(@NotNull ConfigAdapter adapter, @NotNull Function<ConfigAdapter, T> function) {
            this.function = function;
        }

        @Override
        @NotNull
        public Optional<T> get() {
            return Optional.ofNullable(function.apply(ADAPTER));
        }

        @Override
        public String toString() {
            return String.valueOf(get().orElse(null));
        }
    }

    private static class FunctionalReplaceKey extends ReplaceKey {

        private final Function<ReplaceKey, String> function;

        private FunctionalReplaceKey(@NotNull ConfigKey<String> configKey, @NotNull Function<ReplaceKey, String> function) {
            super(configKey);
            this.function = function;
        }

        @Override
        @NotNull
        public ReplaceKey replace(@NotNull Object... replaces) {
            var replaceKey = new FunctionalReplaceKey(getConfigKey(), function);
            replaceKey.args = replaces;
            replaceKey.result = replaceKey.function.apply(replaceKey);
            return replaceKey;
        }
    }
}