package com.github.yuttyann.scriptblockplus.file;

import com.github.yuttyann.scriptblockplus.file.yaml.YamlConfig;
import com.github.yuttyann.scriptblockplus.utils.FileUtils;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * ScriptBlockPlus Lang クラス
 * @author yuttyann44581
 */
public class Lang {

	private static final String DEFAULT_LANGUAGE = "en";

	private final Plugin plugin;
	private final String language;

	public Lang(@NotNull Plugin plugin, @NotNull String language) {
		this.plugin = plugin;
		this.language = StringUtils.isEmpty(language) ? DEFAULT_LANGUAGE : language.toLowerCase();
	}

	@NotNull
	public final String getLanguage() {
		return language;
	}

	@NotNull
	public final YamlConfig load(@NotNull String filePath, @NotNull String dirPath) {
		return YamlConfig.load(plugin, getFile(filePath, dirPath), false);
	}

	@NotNull
	private File getFile(@NotNull String filePath, @NotNull String dirPath) {
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

	private boolean isExists(@NotNull String filePath) {
		if (StringUtils.isNotEmpty(filePath)) {
			return FileUtils.getResource(plugin, filePath) != null;
		}
		return false;
	}
}