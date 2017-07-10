package com.github.yuttyann.scriptblockplus.enums;

public enum ScriptType {
	INTERACT(0, "interact"),
	BREAK(1, "break"),
	WALK(2, "walk");

	private int id;
	private String type;

	private ScriptType(int id, String type) {
		this.id = id;
		this.type = type;
	}

	public int getId() {
		return id;
	}

	@Override
	public String toString() {
		return type;
	}

	public static ScriptType getType(String name) {
		for (ScriptType scriptType : values()) {
			if (name.equals(scriptType.toString())) {
				return scriptType;
			}
		}
		return null;
	}

	public static int length() {
		return values().length;
	}
}