package com.github.yuttyann.scriptblockplus.file;

import com.github.yuttyann.scriptblockplus.file.config.YamlConfig;
import com.github.yuttyann.scriptblockplus.utils.FileUtils;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;

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
		this.language = StringUtils.isEmpty(language) ? DEFAULT_LANGUAGE : language.toLowerCase();
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
		String path = directory + "/" + language + ".yml";
		String code = isExists(path) ? language : DEFAULT_LANGUAGE;
		String filePath = StringUtils.replace(this.filePath, "{code}", code);
		File file = new File(plugin.getDataFolder(), filePath);
		return !file.exists() && !code.equals(language) ? directory + "/" + code + ".yml" : path;
	}

	@NotNull
	public File getFile() {
		String path = directory + "/" + language + ".yml";
		String code = isExists(path) ? language : DEFAULT_LANGUAGE;
		String filePath = StringUtils.replace(this.filePath, "{code}", code);
		File file = new File(plugin.getDataFolder(), filePath);
		if (!file.exists()) {
			if (!code.equals(language)) {
				path = directory + "/" + code + ".yml";
			}
			FileUtils.copyFileFromPlugin(plugin, file, path);
		}
		return file;
	}

	private boolean isExists(@NotNull String filePath) {
		return StringUtils.isNotEmpty(filePath) && FileUtils.getResource(plugin, filePath) != null;
	}
}