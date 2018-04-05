package com.github.yuttyann.scriptblockplus.file;

import java.io.File;

import org.bukkit.plugin.Plugin;

import com.github.yuttyann.scriptblockplus.file.yaml.YamlConfig;
import com.github.yuttyann.scriptblockplus.utils.FileUtils;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;

public final class Lang {

	private static final String DEFAULT_LANGUAGE = "en";

	private final Plugin plugin;
	private final String language;

	public Lang(Plugin plugin, String language) {
		this.plugin = plugin;
		this.language = StringUtils.isEmpty(language) ? DEFAULT_LANGUAGE : language.toLowerCase();
	}

	public String getLanguage() {
		return language;
	}

	public YamlConfig load(String filePath, String dirPath) {
		return YamlConfig.load(plugin, getFile(filePath, dirPath), false);
	}

	private File getFile(String filePath, String dirPath) {
		String path = dirPath + "/" + language + ".yml";
		String code = isExists(path) ? language : DEFAULT_LANGUAGE;
		filePath = StringUtils.replace(filePath, "{code}", code);
		File file = new File(plugin.getDataFolder(), filePath);
		if (!file.exists()) {
			if (!code.equals(language)) {
				path = dirPath + "/" + code + ".yml";
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