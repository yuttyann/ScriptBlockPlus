package com.github.yuttyann.scriptblockplus.metadata;

import org.bukkit.entity.Player;

import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.enums.ClickType;

public class ScriptText extends SimpleMetadata {

	public ScriptText(ScriptBlock plugin) {
		super(plugin);
	}

	public void set(Player player, ClickType clickType, String value) {
		set(player, clickType.toString(), value);
	}

	public void remove(Player player, ClickType clickType) {
		remove(player, clickType.toString());
	}

	public boolean has(Player player, ClickType clickType) {
		return has(player, clickType.toString());
	}

	@Override
	public void removeAll(Player player) {
		for (ClickType clickType : ClickType.values()) {
			if (has(player, clickType)) {
				remove(player, clickType);
			}
		}
	}

	@Override
	public boolean hasAll(Player player) {
		for (ClickType clickType : ClickType.values()) {
			if (has(player, clickType)) {
				return true;
			}
		}
		return false;
	}

	public String get(Player player, ClickType clickType) {
		return getString(player, clickType.toString());
	}
}