package com.github.yuttyann.scriptblockplus.script;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.Validate;
import org.bukkit.permissions.Permissible;

import com.github.yuttyann.scriptblockplus.enums.Permission;
import com.github.yuttyann.scriptblockplus.utils.StreamUtils;

public final class ScriptType implements Comparable<ScriptType>, Serializable {

	private static final Map<String, ScriptType> TYPES = new LinkedHashMap<>();

	public static final class SBPermission {

		private static final String PREFIX = "scriptblockplus.";

		public static boolean has(Permissible permissible, ScriptType scriptType, boolean isCMDorUse) {
			return Permission.has(permissible, scriptType == null ? null : getNode(scriptType, isCMDorUse));
		}

		public static String[] getNodes(boolean isCMDorUse) {
			return StreamUtils.toArray(TYPES.values(), t -> getNode(t, isCMDorUse), new String[TYPES.size()]);
		}

		public static String getNode(ScriptType scriptType, boolean isCMDorUse) {
			return isCMDorUse ? PREFIX + "command." + scriptType.type : PREFIX + scriptType.type + ".use";
		}
	}

	// Default Types
	public static final ScriptType INTERACT = new ScriptType("interact");
	public static final ScriptType BREAK = new ScriptType("break");
	public static final ScriptType WALK = new ScriptType("walk");

	private final String type;
	private final String name;
	private final int ordinal;

	public ScriptType(String type) {
		Validate.notNull(type, "Type cannot be null");
		this.type = type.toLowerCase();
		this.name = type.toUpperCase();

		ScriptType scriptType = TYPES.get(name);
		this.ordinal = scriptType == null ? size() : scriptType.ordinal;
		if (scriptType == null) {
			TYPES.put(name, this);
		}
	}

	public String getType() {
		return type;
	}

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
		hash *= 31 + ordinal;
		hash *= 31 + type.hashCode();
		hash *= 31 + name.hashCode();
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
	public int compareTo(ScriptType another) {
		return Integer.compare(ordinal, another.ordinal);
	}

	public static int size() {
		return TYPES.size();
	}

	public static String[] types() {
		return StreamUtils.toArray(TYPES.values(), t -> t.type, new String[TYPES.size()]);
	}

	public static String[] names() {
		return StreamUtils.toArray(TYPES.values(), t -> t.name, new String[TYPES.size()]);
	}

	public static ScriptType[] values() {
		return TYPES.values().toArray(new ScriptType[TYPES.size()]);
	}

	public static ScriptType valueOf(int ordinal) {
		if (ordinal < 0) {
			throw new IllegalArgumentException("Ordinal cannot be null");
		}
		for (ScriptType scriptType : TYPES.values()) {
			if (scriptType.ordinal == ordinal) {
				return scriptType;
			}
		}
		throw new NullPointerException(ordinal + " does not exist");
	}

	public static ScriptType valueOf(String name) {
		Validate.notNull(name, "Name cannot be null");
		ScriptType scriptType = TYPES.get(name.toUpperCase());
		if (scriptType == null) {
			throw new NullPointerException(name + " does not exist");
		}
		return scriptType;
	}
}