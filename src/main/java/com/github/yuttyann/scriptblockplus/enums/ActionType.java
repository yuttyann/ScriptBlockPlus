package com.github.yuttyann.scriptblockplus.enums;

import java.util.HashSet;
import java.util.Set;

import com.github.yuttyann.scriptblockplus.script.ScriptType;
import com.github.yuttyann.scriptblockplus.utils.StreamUtils;

public enum ActionType {
	CREATE,
	ADD,
	REMOVE,
	VIEW;

	private static final Set<String> TYPES = new HashSet<>();

	private static int i;

	static {
		reload();
	}

	public String createKey(ScriptType scriptType) {
		return scriptType.name() + "_" + name();
	}

	public static String[] types() {
		if (i != ScriptType.size()) {
			reload();
		}
		return TYPES.toArray(new String[TYPES.size()]);
	}

	private static void reload() {
		TYPES.clear();
		try {
			StreamUtils.forEach(ScriptType.values(), s -> StreamUtils.forEach(values(), c -> TYPES.add(c.createKey(s))));
		} finally {
			i = ScriptType.size();
		}
	}
}