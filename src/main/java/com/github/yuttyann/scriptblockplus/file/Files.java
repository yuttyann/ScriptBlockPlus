package com.github.yuttyann.scriptblockplus.file;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.file.yaml.YamlConfig;
import com.github.yuttyann.scriptblockplus.script.ScriptType;
import com.github.yuttyann.scriptblockplus.utils.StreamUtils;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;

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
}