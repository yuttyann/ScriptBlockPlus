package com.github.yuttyann.scriptblockplus.enums;

import java.util.ArrayList;
import java.util.List;

public enum ClickType {
	CREATE,
	ADD,
	REMOVE,
	VIEW;

	private static final String[] TYPES;

	static {
		ScriptType[] values = ScriptType.values();
		List<String> list = new ArrayList<String>(values().length * values.length);
		for (ScriptType scriptType : values) {
			for (ClickType clickType : values()) {
				list.add(clickType.createKey(scriptType));
			}
		}
		TYPES = list.toArray(new String[list.size()]);
	}

	public String createKey(ScriptType scriptType) {
		return scriptType.name() + "_" + name();
	}

	public static String[] types() {
		return TYPES;
	}
}