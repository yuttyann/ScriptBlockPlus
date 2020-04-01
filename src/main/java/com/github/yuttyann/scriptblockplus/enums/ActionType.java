package com.github.yuttyann.scriptblockplus.enums;

import com.github.yuttyann.scriptblockplus.script.ScriptType;
import com.github.yuttyann.scriptblockplus.utils.StreamUtils;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ScriptBlockPlus ActionType 列挙型
 * @author yuttyann44581
 */
public enum ActionType {
	CREATE, ADD, REMOVE, VIEW;

	private static final Set<String> TYPES = new HashSet<>();

	private static AtomicInteger I = new AtomicInteger(ScriptType.size());

	static {
		reload();
	}

	@NotNull
	public String getKey(@NotNull ScriptType scriptType) {
		return scriptType.name() + "_" + name();
	}

	@NotNull
	public static String[] types() {
		if (I.get() != ScriptType.size()) {
			reload();
		}
		return TYPES.toArray(new String[TYPES.size()]);
	}

	private static void reload() {
		try {
			TYPES.clear();
			StreamUtils.forEach(ScriptType.values(), s -> StreamUtils.forEach(values(), c -> TYPES.add(c.getKey(s))));
		} finally {
			I.set(ScriptType.size());
		}
	}
}