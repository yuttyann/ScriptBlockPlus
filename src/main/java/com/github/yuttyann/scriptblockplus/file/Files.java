package com.github.yuttyann.scriptblockplus.file;

import com.github.yuttyann.scriptblockplus.enums.ScriptType;

public class Files {

	public static final String[] FILE_NAMES = {
		"config", "messages", "scripts/interact", "scripts/walk"
	};

	private static Files instance;
	private Yaml config;
	private Yaml messages;
	private Yaml interact;
	private Yaml walk;

	public Files() {
		this.config = new Yaml(FILE_NAMES[0]);
		this.messages = new Yaml(FILE_NAMES[1]);
		this.interact = new Yaml(FILE_NAMES[2], false);
		this.walk = new Yaml(FILE_NAMES[3], false);
	}

	public static Yaml getConfig() {
		return instance.config;
	}

	public static Yaml getMessages() {
		return instance.messages;
	}

	public static Yaml getInteract() {
		return instance.interact;
	}

	public static Yaml getWalk() {
		return instance.walk;
	}

	public static Yaml getScriptFile(ScriptType scriptType) {
		switch (scriptType) {
		case INTERACT:
			return instance.interact;
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