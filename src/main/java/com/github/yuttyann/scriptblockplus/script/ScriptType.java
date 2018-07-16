package com.github.yuttyann.scriptblockplus.script;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang.Validate;
import org.bukkit.permissions.Permissible;

import com.github.yuttyann.scriptblockplus.utils.StreamUtils;

public final class ScriptType {

	public static final class SPermission {

		private static final String PREFIX = "scriptblockplus.";

		public static boolean has(Permissible permissible, ScriptType scriptType, boolean isCMDorUse) {
			return scriptType == null ? false : permissible.hasPermission(getNode(scriptType, isCMDorUse));
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

	private static final Map<String, ScriptType> TYPES = new LinkedHashMap<>();

	private final String type;
	private final String name;

	public ScriptType(String type) {
    	Validate.notNull(type, "Type cannot be null");
		this.type = type;
		this.name = type.toUpperCase();
		TYPES.put(name, this);
	}

	public String getType() {
		return type;
	}

	public String name() {
		return name;
	}

	@Override
	public int hashCode() {
		return Objects.hash(type.hashCode(), name.hashCode());
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ScriptType) {
			ScriptType scriptType = (ScriptType) obj;
			return type.equals(scriptType.type) && name.equals(scriptType.name);
		}
		return false;
	}

	@Override
	public String toString() {
		return type;
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

    public static ScriptType valueOf(String name) {
    	Validate.notNull(name, "Name cannot be null");
    	ScriptType scriptType = TYPES.get(name);
    	if (scriptType == null) {
    		throw new NullPointerException(name + " does not exist");
    	}
    	return scriptType;
    }
}