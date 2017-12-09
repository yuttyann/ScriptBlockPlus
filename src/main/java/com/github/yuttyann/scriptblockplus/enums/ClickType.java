package com.github.yuttyann.scriptblockplus.enums;

import java.util.LinkedList;
import java.util.List;

public enum ClickType {
	CREATE,
	ADD,
	REMOVE,
	VIEW;

	private static final String[] TYPES;

	static {
		List<String> list = new LinkedList<String>();
		for (ScriptType scriptType : ScriptType.values()) {
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