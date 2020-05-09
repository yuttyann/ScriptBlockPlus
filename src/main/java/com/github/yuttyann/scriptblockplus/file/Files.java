package com.github.yuttyann.scriptblockplus.file;

import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.file.config.ConfigKeys;
import com.github.yuttyann.scriptblockplus.file.config.SBConfig;
import com.github.yuttyann.scriptblockplus.file.yaml.UTF8Config;
import com.github.yuttyann.scriptblockplus.file.yaml.YamlConfig;
import com.github.yuttyann.scriptblockplus.script.ScriptType;
import com.github.yuttyann.scriptblockplus.utils.FileUtils;
import com.github.yuttyann.scriptblockplus.utils.StreamUtils;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.google.common.base.Charsets;
import org.bukkit.Bukkit;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * ScriptBlockPlus Files クラス
 * @author yuttyann44581
 */
public final class Files {

	private static final Map<String, YamlConfig> FILES = new HashMap<>();

	public static final String PATH_CONFIG = "config.yml";
	public static final String PATH_LANGS = "langs/{code}.yml";

	public static void reload() {
		ConfigKeys.clear();
		ConfigKeys.load(loadFile(PATH_CONFIG, true));
		ConfigKeys.load(loadLang());

		StreamUtils.forEach(ScriptType.values(), Files::loadScript);
		searchKeys();

		/* 今後の対策のための機能、必要になったら実装します。
		if (SBConfig.isSBPAPIVersion() && Utils.isCBXXXorLater("1.13")) {
			APIVersion apiVersion = new APIVersion(ScriptBlock.getInstance());
			apiVersion.update();
			Utils.sendMessage("[ScriptBlockPlus] API version " + apiVersion.get());
		}
		*/
	}

	public static void searchKeys() {
		YamlConfig config = getConfig();
		if (config.getFile().exists()) {
			sendNotKeyMessages(config, PATH_CONFIG);
		}
		YamlConfig lang = getLang();
		if (lang.getFile().exists()) {
			sendNotKeyMessages(lang, "lang/" + lang.getFileName());
		}
	}

	@NotNull
	public static Map<String, YamlConfig> getFiles() {
		return Collections.unmodifiableMap(FILES);
	}

	@NotNull
	public static YamlConfig getConfig() {
		return FILES.get(PATH_CONFIG);
	}

	@NotNull
	public static YamlConfig getLang() {
		return FILES.get(PATH_LANGS);
	}

	@NotNull
	public static YamlConfig getScriptFile(@NotNull ScriptType scriptType) {
		YamlConfig yaml = FILES.get(scriptType.type());
		if (yaml == null) {
			FILES.put(scriptType.type(), yaml = loadScript(scriptType));
		}
		return yaml;
	}

	@NotNull
	private static YamlConfig loadScript(@NotNull ScriptType scriptType) {
		YamlConfig yaml = loadFile("scripts/" + scriptType.type() + ".yml", false);
		return putFile(scriptType.type(), yaml);
	}

	@NotNull
	private static YamlConfig loadFile(@NotNull String filePath, boolean isCopyFile) {
		return putFile(filePath, YamlConfig.load(ScriptBlock.getInstance(), filePath, isCopyFile));
	}

	@NotNull
	private static YamlConfig loadLang() {
		String language = SBConfig.LANGUAGE.getValue();
		if (StringUtils.isEmpty(language) || "default".equalsIgnoreCase(language)) {
			language = Locale.getDefault().getLanguage();
		}
		Lang lang = new Lang(ScriptBlock.getInstance(), language);
		return putFile(Files.PATH_LANGS, lang.load(Files.PATH_LANGS, "lang"));
	}

	@NotNull
	private static YamlConfig putFile(@NotNull String name, @NotNull YamlConfig yaml) {
		FILES.put(name, yaml);
		return yaml;
	}

	private static void sendNotKeyMessages(@NotNull YamlConfig yaml, @NotNull String path) {
		String filePath = StringUtils.replace(yaml.getFolderPath(), File.separator, "/");
		InputStream is = FileUtils.getResource(ScriptBlock.getInstance(), path);
		if (is == null) {
			return;
		}
		YamlConfiguration config = UTF8Config.loadConfiguration(new InputStreamReader(is, Charsets.UTF_8));
		Set<String> keys = yaml.getKeys(true);
		for (String key : config.getKeys(true)) {
			if (!keys.contains(key)) {
				Object value = config.get(key) instanceof MemorySection ? "" : config.get(key);
				Bukkit.getConsoleSender().sendMessage("§c[" + filePath + "] Key not found: §r" + key + ": " + value);
			}
		}
	}
}