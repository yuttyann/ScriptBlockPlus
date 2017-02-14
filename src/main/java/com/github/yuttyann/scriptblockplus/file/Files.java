package com.github.yuttyann.scriptblockplus.file;

import org.bukkit.plugin.Plugin;

import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.enums.ScriptType;

public class Files {

	public static final String[] FILE_NAMES = {
		"config.yml", "messages.yml", "scripts/interact.yml", "scripts/break.yml", "scripts/walk.yml"
	};

	private static Files instance;
	private YamlConfig config;
	private YamlConfig messages;
	private YamlConfig interact;
	private YamlConfig break_;
	private YamlConfig walk;

	public Files() {
		Plugin plugin = ScriptBlock.instance;
		this.config = YamlConfig.load(plugin, FILE_NAMES[0]);
		this.messages = YamlConfig.load(plugin, FILE_NAMES[1]);
		this.interact = YamlConfig.load(plugin, FILE_NAMES[2], false);
		this.break_ = YamlConfig.load(plugin, FILE_NAMES[3], false);
		this.walk = YamlConfig.load(plugin, FILE_NAMES[4], false);
	}

	public static YamlConfig getConfig() {
		return instance.config;
	}

	public static YamlConfig getMessages() {
		return instance.messages;
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
		}
		return null;
	}

	public static void reload() {
		instance = new Files();
		Messages.reload();
	}
}