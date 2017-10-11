package com.github.yuttyann.scriptblockplus.file;

import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.enums.ScriptType;

public class Files extends Lang {

	private static Files instance;

	private YamlConfig config;
	private YamlConfig lang;
	private YamlConfig interact;
	private YamlConfig break_;
	private YamlConfig walk;

	private Files() {
		super(ScriptBlock.getInstance());
		this.config = load("config_{lang}.yml", "config");
		this.lang = load("lang_{lang}.yml", "lang");
		this.interact = load("scripts/interact.yml", false);
		this.break_ = load("scripts/break.yml", false);
		this.walk = load("scripts/walk.yml", false);
	}

	public static void reload() {
		instance = new Files();
		SBConfig.reload();
	}

	public static YamlConfig getConfig() {
		return instance.config;
	}

	public static YamlConfig getLang() {
		return instance.lang;
	}

	public static YamlConfig getInteract() {
		return instance.interact;
	}

	public static YamlConfig getBreak() {
		return instance.break_;
	}

	public static YamlConfig getWalk() {
		return instance.walk;
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

	private final YamlConfig load(String filePath, boolean isCopyFile) {
		return YamlConfig.load(plugin , filePath, isCopyFile);
	}
}