package com.github.yuttyann.scriptblockplus.type;

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
