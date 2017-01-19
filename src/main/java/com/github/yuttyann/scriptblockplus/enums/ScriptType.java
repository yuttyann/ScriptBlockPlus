package com.github.yuttyann.scriptblockplus.enums;

public enum ScriptType {
	INTERACT("interact"),
	WALK("walk");

	private String type;

	private ScriptType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return type;
	}
}