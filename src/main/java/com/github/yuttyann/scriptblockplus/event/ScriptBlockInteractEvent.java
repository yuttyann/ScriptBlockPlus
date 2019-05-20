package com.github.yuttyann.scriptblockplus.event;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

public class ScriptBlockInteractEvent extends TriggerEvent {

	private boolean isLeftClick;

	public ScriptBlockInteractEvent(Player player, Block block, Action action) {
		super(player, block);
		this.isLeftClick = action == Action.LEFT_CLICK_BLOCK;
	}

	public boolean isLeftClick() {
		return isLeftClick;
	}

	public boolean isRightClick() {
		return !isLeftClick;
	}
}