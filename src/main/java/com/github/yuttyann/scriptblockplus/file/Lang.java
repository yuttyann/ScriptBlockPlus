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
 * 
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