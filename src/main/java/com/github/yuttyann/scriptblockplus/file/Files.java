package com.github.yuttyann.scriptblockplus.file;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.enums.ScriptType;

public final class Files {

	private static final Map<String, YamlConfig> files = new HashMap<String, YamlConfig>();

	private Files() {
		throw new AssertionError();
	}

	public static void reload() {
		files.put("config", loadFile("config.yml", true));
		SBConfig.reloadConfig();
		files.put("lang", loadLang("lang_{lang}.yml", "lang"));
		SBConfig.reloadLang();
		files.put("interact", loadFile("scripts/interact.yml", false));
		files.put("break", loadFile("scripts/break.yml", false));
		files.put("walk", loadFile("scripts/walk.yml", false));
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

	public static YamlConfig getInteract() {
		return files.get("interact");
	}

	public static YamlConfig getBreak() {
		return files.get("break");
	}

	public static YamlConfig getWalk() {
		return files.get("walk");
	}

	public static YamlConfig getScriptFile(ScriptType scriptType) {
		switch (scriptType) {
		case INTERACT:
			return getInteract();
		case BREAK:
			return getBreak();
		case WALK:
			return getWalk();
		default:
			return null;
		}
	}

	private static YamlConfig loadFile(String filePath, boolean isCopyFile) {
		return YamlConfig.load(ScriptBlock.getInstance(), filePath, isCopyFile);
	}

	private static YamlConfig loadLang(String filePath, String dirPath) {
		String language = SBConfig.getLanguage();
		if ("default".equalsIgnoreCase(language)) {
			language = System.getProperty("user.language");
		}
		return new Lang(ScriptBlock.getInstance(), language).load(filePath, dirPath);
	}
}