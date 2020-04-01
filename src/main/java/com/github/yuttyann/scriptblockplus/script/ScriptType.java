package com.github.yuttyann.scriptblockplus.script;

import com.github.yuttyann.scriptblockplus.utils.StreamUtils;
import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.*;
import java.util.function.Function;

public final class ScriptType implements Comparable<ScriptType>, Serializable {

	private static final Map<String, ScriptType> TYPES = new LinkedHashMap<>();

	// Default Types
	public static final ScriptType INTERACT = new ScriptType("interact");
	public static final ScriptType BREAK = new ScriptType("break");
	public static final ScriptType WALK = new ScriptType("walk");

	private final String type;
	private final String name;
	private final int ordinal;

	public ScriptType(@NotNull String type) {
		Validate.notNull(type, "Type cannot be null");
		this.type = type.toLowerCase();
		this.name = type.toUpperCase();

		ScriptType scriptType = TYPES.get(name);
		this.ordinal = scriptType == null ? TYPES.size() : scriptType.ordinal;
		if (scriptType == null) {
			TYPES.put(name, this);
		}
	}

	@NotNull
	public String getType() {
		return type;
	}

	@NotNull
	public String name() {
		return name;
	}

	public int ordinal() {
		return ordinal;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public int hashCode() {
		int hash = 1;
		int prime = 31;
		hash = prime * hash + ordinal;
		hash = prime * hash + type.hashCode();
		hash = prime * hash + name.hashCode();
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ScriptType) {
			ScriptType scriptType = ((ScriptType) obj);
			return type.equals(scriptType.type) && name.equals(scriptType.name);
		}
		return false;
	}

	@Override
	public int compareTo(@NotNull ScriptType another) {
		return Integer.compare(ordinal, another.ordinal);
	}

	public static int size() {
		return TYPES.size();
	}

	@NotNull
	public static String[] types() {
		return toArray(t -> t.type, new String[TYPES.size()]);
	}

	@NotNull
	public static String[] names() {
		return toArray(t -> t.name, new String[TYPES.size()]);
	}

	@NotNull
	public static ScriptType[] values() {
		return TYPES.values().toArray(new ScriptType[size()]);
	}

	@NotNull
	public static <T> T[] toArray(@NotNull Function<ScriptType, T> mapper, @NotNull T[] array) {
		return StreamUtils.toArray(TYPES.values(), mapper, array);
	}

	@NotNull
	public static ScriptType valueOf(int ordinal) {
		ScriptType scriptType = StreamUtils.fOrElse(TYPES.values(), s -> s.ordinal == ordinal, null);
		if (scriptType == null) {
			throw new NullPointerException(ordinal + " does not exist");
		}
		return scriptType;
	}

	@NotNull
	public static ScriptType valueOf(String name) {
		Validate.notNull(name, "Name cannot be null");
		ScriptType scriptType = TYPES.get(name.toUpperCase());
		if (scriptType == null) {
			throw new NullPointerException(name + " does not exist");
		}
		return scriptType;
	}
}