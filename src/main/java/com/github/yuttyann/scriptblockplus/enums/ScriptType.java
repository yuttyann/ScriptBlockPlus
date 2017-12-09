package com.github.yuttyann.scriptblockplus.enums;

public enum ScriptType {
	INTERACT("interact"),
	BREAK("break"),
	WALK("walk");

	private final String type;

	private ScriptType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	@Override
	public String toString() {
		return type;
	}
}