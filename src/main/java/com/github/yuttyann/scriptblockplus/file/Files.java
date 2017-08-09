package com.github.yuttyann.scriptblockplus.file;

import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.enums.ScriptType;

public class Files {

	public static final String[] FILE_PATHS = {
		"config.yml", "lang.yml", "scripts/interact.yml", "scripts/break.yml", "scripts/walk.yml", "scripts/cooldown.dat",
		"plugins/ScriptBlock/BlocksData/interact_Scripts.yml", "plugins/ScriptBlock/BlocksData/walk_Scripts.yml",
	};

	private static Files instance;
	private YamlConfig config;
	private YamlConfig lang;
	private YamlConfig interact;
	private YamlConfig break_;
	private YamlConfig walk;

	public Files() {
		ScriptBlock plugin = ScriptBlock.getInstance();
		this.config = YamlConfig.load(plugin, FILE_PATHS[0]);
		this.lang = YamlConfig.load(plugin, FILE_PATHS[1]);
		this.interact = YamlConfig.load(plugin, FILE_PATHS[2], false);
		this.break_ = YamlConfig.load(plugin, FILE_PATHS[3], false);
		this.walk = YamlConfig.load(plugin, FILE_PATHS[4], false);
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
			return instance.interact;
		case BREAK:
			return instance.break_;
		case WALK:
			return instance.walk;
		default:
			return null;
		}
	}

	public static void reload() {
		instance = new Files();
		Lang.reload();
	}
}