package com.github.yuttyann.scriptblockplus.enums;

import com.github.yuttyann.scriptblockplus.utils.StreamUtils;



public enum ScriptType {
	INTERACT("interact", 0),
	BREAK("break", 1),
	WALK("walk", 2);

	private String type;
	private int id;

	private ScriptType(String type, int id) {
		this.type = type;
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public int getId() {
		return id;
	}

	public static ScriptType getById(int id) {
		return StreamUtils.filterOrElse(values(), s -> s.id == id, null);
	}
}