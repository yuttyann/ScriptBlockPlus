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
import com.github.yuttyann.scriptblockplus.file.config.YamlConfig;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * ScriptBlockPlus SBFiles クラス
 * @author yuttyann44581
 */
public final class SBFiles {

    private static final Map<String, YamlConfig> FILES = new HashMap<>();

    public static final String PATH_CONFIG = "config_{code}.yml";
    public static final String PATH_MESSAGE = "message_{code}.yml";

    public static void reload() {
        // ScriptBlockのインスタンス
        var plugin = ScriptBlock.getInstance();

        // ファイルの内容を読み込む
        ConfigKeys.clear();
        ConfigKeys.load(loadLang(plugin, PATH_CONFIG, "config"));
        ConfigKeys.load(loadLang(plugin, PATH_MESSAGE, "message"));

        // リロードを行ったことを知らせるイベントを呼ぶ
        Bukkit.getPluginManager().callEvent(new FileReloadEvent());
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
    public static YamlConfig loadLang(@NotNull Plugin plugin, @NotNull String filePath, @NotNull String directoryPath) {
        return putFile(plugin, filePath, new Lang(plugin, Locale.getDefault().getLanguage(), filePath, directoryPath).load());
    }

    @NotNull
    private static YamlConfig putFile(@NotNull Plugin plugin, @NotNull String name, @NotNull YamlConfig yaml) {
        FILES.put(plugin.getName() + "_" + name, yaml);
        return yaml;
    }
}