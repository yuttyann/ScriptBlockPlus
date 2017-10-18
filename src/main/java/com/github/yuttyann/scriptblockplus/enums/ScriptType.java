package com.github.yuttyann.scriptblockplus.enums;

public enum ScriptType {
	INTERACT("interact"),
	BREAK("break"),
	WALK("walk");

	private String type;

	private ScriptType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}
}