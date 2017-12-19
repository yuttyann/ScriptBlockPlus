package com.github.yuttyann.scriptblockplus.enums;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public enum ClickType {
	CREATE,
	ADD,
	REMOVE,
	VIEW;

	private static final List<String> TYPES;

	static {
		List<String> list = new ArrayList<String>();
		for (ScriptType scriptType : ScriptType.values()) {
			for (ClickType clickType : ClickType.values()) {
				list.add(clickType.createKey(scriptType));
			}
		}
		TYPES = Collections.unmodifiableList(list);
	}

	public String createKey(ScriptType scriptType) {
		return scriptType.name() + "_" + name();
	}

	public static List<String> types() {
		return TYPES;
	}
}