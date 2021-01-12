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

import com.github.yuttyann.scriptblockplus.file.config.YamlConfig;
import com.github.yuttyann.scriptblockplus.utils.FileUtils;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

/**
 * ScriptBlockPlus Lang クラス
 * @author yuttyann44581
 */
public final class Lang {

    private static final String DEFAULT_LANGUAGE = "en";

    private final Plugin plugin;
    private final String language;
    private final String filePath;
    private final String directory;

    public Lang(@NotNull Plugin plugin, @NotNull String language, @NotNull String filePath, @NotNull String directory) {
        this.plugin = plugin;
        this.language = StringUtils.isEmpty(language) ? DEFAULT_LANGUAGE : language.toLowerCase(Locale.ROOT);
        this.filePath = filePath;
        this.directory = directory;
    }

    @NotNull
    public String getLanguage() {
        return language;
    }

    @NotNull
    public YamlConfig load() {
        return YamlConfig.load(plugin, getFile(), false).setInnerPath(getPath());
    }

    @NotNull
    public String getPath() {
        var path = directory + "/" + language + ".yml";
        var code = FileUtils.getResource(plugin, path) != null ? language : DEFAULT_LANGUAGE;
        var file = new File(plugin.getDataFolder(), StringUtils.replace(this.filePath, "{code}", code));
        return !file.exists() && !code.equals(language) ? directory + "/" + code + ".yml" : path;
    }

    @NotNull
    public File getFile() {
        var path = directory + "/" + language + ".yml";
        var code = FileUtils.getResource(plugin, path) != null ? language : DEFAULT_LANGUAGE;
        var file = new File(plugin.getDataFolder(), StringUtils.replace(this.filePath, "{code}", code));
        if (!file.exists()) {
            if (!code.equals(language)) {
                path = directory + "/" + code + ".yml";
            }
            try {
                FileUtils.copyFileFromPlugin(plugin, file, path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }
}