package com.github.yuttyann.scriptblockplus.file;

import java.io.File;

import org.bukkit.plugin.Plugin;

import com.github.yuttyann.scriptblockplus.file.yaml.YamlConfig;
import com.github.yuttyann.scriptblockplus.utils.FileUtils;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;

public final class Lang {

	private final Plugin plugin;
	private final String language;

	public Lang(Plugin plugin, String language) {
		this.plugin = plugin;
		this.language = language.toLowerCase();
	}

	public String getLanguage() {
		return language;
	}

	public YamlConfig load(String filePath, String dirPath) {
		return YamlConfig.load(plugin, getFile(filePath, dirPath), false);
	}

	private File getFile(String filePath, String dirPath) {
		String path = dirPath + "/" + language + ".yml";
		String lang = isExists(path) ? language : "en";
		filePath = StringUtils.replace(filePath, "{code}", lang);
		File file = new File(plugin.getDataFolder(), filePath);
		if (!file.exists()) {
			if (!lang.equals(language)) {
				path = dirPath + "/" + lang + ".yml";
			}
			FileUtils.copyFileFromPlugin(plugin, file, path);
		}
		return file;
	}

	private boolean isExists(String filePath) {
		if (StringUtils.isEmpty(filePath)) {
			return false;
		}
		return FileUtils.getResource(plugin, filePath) != null;
	}
}