package com.github.yuttyann.scriptblockplus.file;

import com.github.yuttyann.scriptblockplus.manager.OptionManager.ScriptType;

public class Files {

	private static Files instance;
	private Yaml config;
	private Yaml messages;
	private Yaml interact;
	private Yaml walk;

	public Files() {
		config = new Yaml("config");
		messages = new Yaml("messages");
		interact = new Yaml("scripts/interact", false);
		walk = new Yaml("scripts/walk", false);
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

	public static Yaml getScripts(ScriptType scriptType) {
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
