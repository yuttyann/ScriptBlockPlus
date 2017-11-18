package com.github.yuttyann.scriptblockplus.file;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.enums.ScriptType;
import com.github.yuttyann.scriptblockplus.file.yaml.YamlConfig;
import com.github.yuttyann.scriptblockplus.utils.ProfileFetcher;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;

public final class Files {

	private static final Map<String, YamlConfig> files = new HashMap<String, YamlConfig>();

	private static String defaultLanguage;

	public static void reload() {
		files.put("config", loadFile("config.yml", true));
		SBConfig.reloadConfig();

		files.put("lang", loadLang("lang_{code}.yml", "lang"));
		SBConfig.reloadLang();

		for (ScriptType scriptType : ScriptType.values()) {
			String type = scriptType.getType();
			files.put(type, loadFile("scripts/" + type + ".yml", false));
		}
	}

	public static Map<String, YamlConfig> getFiles() {
		return Collections.unmodifiableMap(files);
	}

	public static YamlConfig getConfig() {
		return files.get("config");
	}

	public static YamlConfig getLang() {
		return files.get("lang");
	}

	public static YamlConfig getScriptFile(ScriptType scriptType) {
		return scriptType == null ? null : files.get(scriptType.getType());
	}

	private static YamlConfig loadFile(String filePath, boolean isCopyFile) {
		return YamlConfig.load(ScriptBlock.getInstance(), filePath, isCopyFile);
	}

	private static YamlConfig loadLang(String filePath, String dirPath) {
		String language = SBConfig.getLanguage();
		if ("default".equalsIgnoreCase(language)) {
			if (defaultLanguage == null) {
				defaultLanguage = getDefaultLanguage();
			}
			language = defaultLanguage;
		}
		return new Lang(ScriptBlock.getInstance(), language).load(filePath, dirPath);
	}

	private static String getDefaultLanguage() {
		try {
			String url = "http://api.yuttyann44581.net/json-language/";
			String code =  ProfileFetcher.getJsonObject(url).get("code").getAsString();
			return StringUtils.split(code, "-")[0];
		} catch (Exception e) {
			return Locale.getDefault().getLanguage();
		}
	}
}