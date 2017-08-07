package com.github.yuttyann.scriptblockplus.metadata;

import org.bukkit.entity.Player;

import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.enums.ClickType;

public class ClickAction extends SimpleMetadata {

	public ClickAction(ScriptBlock plugin) {
		super(plugin);
	}

	public void set(Player player, String key, boolean value) {
		super.set(player, key, value);
	}

	@Override
	public void removeAll(Player player) {
		for (String clickData : ClickType.types()) {
			if (has(player, clickData)) {
				remove(player, clickData);
			}
		}
	}

	@Override
	public boolean hasAll(Player player) {
		for (String clickData : ClickType.types()) {
			if (has(player, clickData)) {
				return true;
			}
		}
		return false;
	}

	public boolean get(Player player, String clickData) {
		return getBoolean(player, clickData);
	}
}