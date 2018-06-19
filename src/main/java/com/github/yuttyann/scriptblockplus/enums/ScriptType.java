package com.github.yuttyann.scriptblockplus.enums;

import java.util.ArrayList;
import java.util.List;

import com.github.yuttyann.scriptblockplus.utils.StreamUtils;

public enum ScriptType {
	INTERACT("interact"),
	BREAK("break"),
	WALK("walk");

	private static final List<String> TYPES;

	static {
		TYPES = new ArrayList<>();
		StreamUtils.forEach(values(), s -> TYPES.add(s.type));
	}

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

	public static String[] types() {
		return TYPES.toArray(new String[TYPES.size()]);
	}
}