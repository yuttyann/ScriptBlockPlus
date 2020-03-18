package com.github.yuttyann.scriptblockplus.file.config;

import com.github.yuttyann.scriptblockplus.file.Files;
import com.github.yuttyann.scriptblockplus.file.yaml.YamlConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

public class ConfigKeys {

    private static final Set<ConfigData> DATAS = new HashSet<>();

    public static void reload() {
        if (DATAS.size() > 0) {
            DATAS.clear();
        }
        for (YamlConfig value : Files.getFiles().values()) {
            for (String key : value.getKeys(true)) {
                DATAS.add(new ConfigData(key, value.get(key)));
            }
        }
    }

    @NotNull
    public static ConfigKey<String> stringKey(@NotNull String key, @Nullable String def) {
        return new ConfigKey<>(key, def);
    }

    @NotNull
    public static ConfigKey<List<String>> stringListKey(@NotNull String key, @Nullable List<String> def) {
        return new ConfigKey<>(key, def);
    }

    @NotNull
    public static CustomText customStringKey(@NotNull String key, @Nullable String def, @NotNull Consumer<CustomText> action) {
        CustomText text = new CustomText(key, def);
        action.accept(text);
        return text;
    }

    @NotNull
    public static ConfigKey<Integer> integerKey(@NotNull String key, int def) {
        return new ConfigKey<>(key, def);
    }

    @NotNull
    public static ConfigKey<Boolean> booleanKey(@NotNull String key, boolean def) {
        return new ConfigKey<>(key, def);
    }

    @NotNull
    static ConfigData getConfigData(@NotNull String key) {
       return DATAS.stream().filter(d -> key.equals(d.getKey())).findFirst().orElse(new EmptyConfigData());
    }

    private static class EmptyConfigData extends ConfigData {

        EmptyConfigData() {
            super("null", Files.getConfig());
        }

        @Override
        @Nullable
        public <R> R getValue(@Nullable R def) {
            return def;
        }
    }
}