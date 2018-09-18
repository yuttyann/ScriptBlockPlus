package com.github.yuttyann.scriptblockplus.event;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

public class ScriptBlockInteractEvent extends ScriptBlockEvent {

	private boolean isLeftClick;
	private boolean cancelled;

	public ScriptBlockInteractEvent(Player player, Block block, Action action) {
		super(player, block);
		this.isLeftClick = action == Action.LEFT_CLICK_BLOCK;
	}

	public Material getMaterial(boolean isMainHand) {
		if (!hasItem(isMainHand)) {
			return Material.AIR;
		}
		return getItem(isMainHand).getType();
	}

	public boolean hasItem(boolean isMainHand) {
		return getItem(isMainHand) != null;
	}

	public boolean isBlockInHand(boolean isMainHand) {
		if (!hasItem(isMainHand)) {
			return false;
		}
		return getItem(isMainHand).getType().isBlock();
	}

	public boolean isLeftClick() {
		return isLeftClick;
	}

	public boolean isRightClick() {
		return !isLeftClick;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancel) {
		cancelled = cancel;
	}
}