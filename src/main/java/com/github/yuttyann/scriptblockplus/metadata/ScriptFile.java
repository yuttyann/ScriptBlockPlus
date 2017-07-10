package com.github.yuttyann.scriptblockplus.metadata;

import org.bukkit.entity.Player;

import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.enums.ScriptType;
import com.github.yuttyann.scriptblockplus.script.ScriptEdit;

public class ScriptFile extends SimpleMetadata {

	public ScriptFile(ScriptBlock plugin) {
		super(plugin);
	}

	public void set(Player player, ScriptType scriptType, ScriptEdit value) {
		set(player, getType(scriptType), value);
	}

	public void remove(Player player, ScriptType scriptType) {
		remove(player, getType(scriptType));
	}

	public boolean has(Player player, ScriptType scriptType) {
		return has(player, getType(scriptType));
	}

	@Override
	public void removeAll(Player player) {
		for (ScriptType scriptType : ScriptType.values()) {
			if (has(player, scriptType)) {
				remove(player, scriptType);
			}
		}
	}

	@Override
	public boolean hasAll(Player player) {
		for (ScriptType scriptType : ScriptType.values()) {
			if (has(player, scriptType)) {
				return true;
			}
		}
		return false;
	}

	public ScriptEdit get(Player player) {
		for (ScriptType scriptType : ScriptType.values()) {
			Object value = get(player, getType(scriptType));
			if (value != null) {
				return (ScriptEdit) value;
			}
		}
		return null;
	}

	private String getType(ScriptType scriptType) {
		return "SCRIPTBLOCKPLUS_" + scriptType.toString().toUpperCase();
	}
}