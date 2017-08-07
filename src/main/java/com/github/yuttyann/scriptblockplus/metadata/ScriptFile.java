package com.github.yuttyann.scriptblockplus.metadata;

import org.bukkit.entity.Player;

import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.enums.ScriptType;
import com.github.yuttyann.scriptblockplus.script.ScriptEdit;

public class ScriptFile extends SimpleMetadata {

	public ScriptFile(ScriptBlock plugin) {
		super(plugin);
	}

	public void set(Player player, ScriptType key, ScriptEdit value) {
		set(player, key.create(), value);
	}

	public void remove(Player player, ScriptType key) {
		remove(player, key.create());
	}

	public boolean has(Player player, ScriptType key) {
		return has(player, key.create());
	}

	@Override
	public void removeAll(Player player) {
		for (ScriptType scriptType : ScriptType.values()) {
			String key = scriptType.create();
			if (has(player, key)) {
				remove(player, key);
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

	public ScriptEdit getEdit(Player player) {
		for (ScriptType scriptType : ScriptType.values()) {
			Object value = get(player, scriptType.create());
			if (value != null) {
				return (ScriptEdit) value;
			}
		}
		return null;
	}
}