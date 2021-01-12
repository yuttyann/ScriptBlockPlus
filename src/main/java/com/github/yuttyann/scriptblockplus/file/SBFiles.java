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
package com.github.yuttyann.scriptblockplus.file;

import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.event.FileReloadEvent;
import com.github.yuttyann.scriptblockplus.file.config.ConfigKeys;
import com.github.yuttyann.scriptblockplus.file.config.SBConfig;
import com.github.yuttyann.scriptblockplus.file.config.YamlConfig;
import com.github.yuttyann.scriptblockplus.utils.FileUtils;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.google.common.base.Charsets;
import org.bukkit.Bukkit;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.InputStreamReader;
import java.util.*;

/**
 * ScriptBlockPlus SBFiles クラス
 * @author yuttyann44581
 */
public final class SBFiles {

    private static final Map<String, YamlConfig> FILES = new HashMap<>();

    public static final String S = File.separator;
    public static final String PATH_CONFIG = "config.yml";
    public static final String PATH_LANGS = "langs" + S + "{code}.yml";

    public static void reload() {
        // ScriptBlockのインスタンス
        var plugin = ScriptBlock.getInstance();

        // ファイルの内容を読み込む
        ConfigKeys.clear();
        ConfigKeys.load(loadFile(plugin, PATH_CONFIG, true));
        ConfigKeys.load(loadLang(plugin, PATH_LANGS));

        // ファイルの内容が欠けていないか確認
        searchKeys(plugin, PATH_CONFIG, PATH_LANGS);

        // リロードを行ったことを知らせるイベントを呼ぶ
        Bukkit.getPluginManager().callEvent(new FileReloadEvent());
    }

    public static void searchKeys(@NotNull Plugin plugin, @NotNull String... paths) {
        for (var path : paths) {
            var yaml = getFile(plugin, path);
            if (yaml.isPresent() && yaml.get().exists()) {
                var filePath = Optional.ofNullable(yaml.get().getInnerPath());
                sendNotKeyMessages(plugin, yaml.get(), filePath.orElse(yaml.get().getFileName()));
            }
        }
    }

    public static void sendNotKeyMessages(@NotNull Plugin plugin, @NotNull YamlConfig yaml, @NotNull String path) {
        var resource = FileUtils.getResource(plugin, path);
        if (resource == null) {
            return;
        }
        var keys = yaml.getKeys(true);
        var config = YamlConfiguration.loadConfiguration(new InputStreamReader(resource, Charsets.UTF_8));
        var filePath = plugin.getName() + "/" + StringUtils.replace(yaml.getFolderPath(), S, "/");
        for (String key : config.getKeys(true)) {
            if (!keys.contains(key)) {
                var value = config.get(key) instanceof MemorySection ? "" : config.get(key);
                Bukkit.getConsoleSender().sendMessage("§c[" + filePath + "] Key not found: §r" + key + ": " + value);
            }
        }
    }

    @NotNull
    public static Map<String, YamlConfig> getFiles() {
        return Collections.unmodifiableMap(FILES);
    }

    public static Optional<YamlConfig> getFile(@NotNull Plugin plugin, @NotNull String filePath) {
        return Optional.ofNullable(FILES.get(plugin.getName() + "_" + filePath));
    }

    @NotNull
    public static YamlConfig loadFile(@NotNull Plugin plugin, @NotNull String filePath, boolean isCopyFile) {
        return putFile(plugin, filePath, YamlConfig.load(plugin, filePath, isCopyFile));
    }

    @NotNull
    public static YamlConfig loadLang(@NotNull Plugin plugin, @NotNull String filePath) {
        var language = SBConfig.LANGUAGE.getValue();
        if (StringUtils.isEmpty(language) || "default".equalsIgnoreCase(language)) {
            language = Locale.getDefault().getLanguage();
        }
        return putFile(plugin, filePath, new Lang(plugin, language, filePath, "lang").load());
    }

    @NotNull
    private static YamlConfig putFile(@NotNull Plugin plugin, @NotNull String name, @NotNull YamlConfig yaml) {
        FILES.put(plugin.getName() + "_" + name, yaml);
        return yaml;
    }
}