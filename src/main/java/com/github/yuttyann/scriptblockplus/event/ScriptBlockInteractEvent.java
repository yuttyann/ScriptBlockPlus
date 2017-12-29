package com.github.yuttyann.scriptblockplus.event;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

import com.github.yuttyann.scriptblockplus.utils.Utils;

public class ScriptBlockInteractEvent extends ScriptBlockEvent {

	private ItemStack mainHand;
	private ItemStack offHand;
	private boolean isLeftClick;
	private boolean cancelled;

	public ScriptBlockInteractEvent(Player player, Block block) {
		this(player, block, Action.RIGHT_CLICK_BLOCK);
	}

	public ScriptBlockInteractEvent(Player player, Block block, Action action) {
		super(player, block);
		this.mainHand = Utils.getItemInMainHand(player);
		this.offHand = Utils.getItemInOffHand(player);
		this.isLeftClick = action.name().startsWith("LEFT_CLICK_");
	}

	public ItemStack getItemInMainHand() {
		return mainHand;
	}

	public ItemStack getItemInOffHand() {
		return offHand;
	}

	public ItemStack getItem(boolean isMainHand) {
		if (!isMainHand && !Utils.isCB19orLater()) {
			isMainHand = true;
		}
		return isMainHand ? mainHand : offHand;
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