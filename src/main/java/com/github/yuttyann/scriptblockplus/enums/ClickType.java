package com.github.yuttyann.scriptblockplus.enums;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.github.yuttyann.scriptblockplus.utils.StreamUtils;

public enum ClickType {
	CREATE,
	ADD,
	REMOVE,
	VIEW;

	private static final List<String> TYPES;

	static {
		TYPES = new ArrayList<>();
		forE(ScriptType.values(), s -> forE(values(), c -> TYPES.add(c.createKey(s))));
	}

	public String createKey(ScriptType scriptType) {
		return scriptType.name() + "_" + name();
	}

	public static String[] types() {
		return TYPES.toArray(new String[TYPES.size()]);
	}

	private static <T> void forE(T[] array, Consumer<T> action) {
		StreamUtils.forEach(array, action);
	}
}