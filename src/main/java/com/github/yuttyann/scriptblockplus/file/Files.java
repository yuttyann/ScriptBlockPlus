package com.github.yuttyann.scriptblockplus.file;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.file.yaml.UTF8Config;
import com.github.yuttyann.scriptblockplus.file.yaml.YamlConfig;
import com.github.yuttyann.scriptblockplus.script.ScriptType;
import com.github.yuttyann.scriptblockplus.utils.FileUtils;
import com.github.yuttyann.scriptblockplus.utils.StreamUtils;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import com.google.common.base.Charsets;

public final class Files {

	private static final Map<String, YamlConfig> FILES = new HashMap<>();

	private static final String DEFAULT_LANGUAGE = Locale.getDefault().getLanguage();

	public static final String PATH_CONFIG = "config.yml";
	public static final String PATH_LANGS = "langs/{code}.yml";

	public static void reload() {
		loadFile(PATH_CONFIG, true);
		SBConfig.reloadConfig();

		loadLang(PATH_LANGS, "lang");
		SBConfig.reloadLang();

		StreamUtils.forEach(ScriptType.values(), s -> loadScript(s));
	}

	public static void searchKeys() {
		YamlConfig config = getConfig();
		if (config.getFile().exists()) {
			sendKeyMessages(config, PATH_CONFIG);
		}
		YamlConfig lang = getLang();
		if (lang.getFile().exists()) {
			sendKeyMessages(lang, "lang/" + lang.getFileName());
		}
	}

	public static Map<String, YamlConfig> getFiles() {
		return Collections.unmodifiableMap(FILES);
	}

	public static YamlConfig getConfig() {
		return FILES.get(PATH_CONFIG);
	}

	public static YamlConfig getLang() {
		return FILES.get(PATH_LANGS);
	}

	public static YamlConfig getScriptFile(ScriptType scriptType) {
		YamlConfig yaml = FILES.get(scriptType.getType());
		if (yaml == null) {
			yaml = loadScript(scriptType);
		}
		return yaml;
	}

	private static YamlConfig loadScript(ScriptType scriptType) {
		YamlConfig yaml = loadFile("scripts/" + scriptType.getType() + ".yml", false);
		return putFile(scriptType.getType(), yaml);
	}

	private static YamlConfig loadFile(String filePath, boolean isCopyFile) {
		return putFile(filePath, YamlConfig.load(ScriptBlock.getInstance(), filePath, isCopyFile));
	}

	private static YamlConfig loadLang(String filePath, String dirPath) {
		String language = SBConfig.getLanguage();
		if (StringUtils.isEmpty(language) || "default".equalsIgnoreCase(language)) {
			language = DEFAULT_LANGUAGE;
		}
		return putFile(filePath, new Lang(ScriptBlock.getInstance(), language).load(filePath, dirPath));
	}

	private static YamlConfig putFile(String name, YamlConfig yaml) {
		FILES.put(name, yaml);
		return yaml;
	}

	private static void sendKeyMessages(YamlConfig yaml, String path) {
		String filePath = StringUtils.replace(yaml.getFolderPath(), "\\", "/");
		InputStream is = FileUtils.getResource(ScriptBlock.getInstance(), path);
		Set<String> keys = yaml.getKeys(true);
		for (String key : UTF8Config.loadConfiguration(new InputStreamReader(is, Charsets.UTF_8)).getKeys(true)) {
			if (!keys.contains(key)) {
				Utils.sendMessage("§c[" + filePath + "] Key not found: §r" + key);
			}
		}
	}
}