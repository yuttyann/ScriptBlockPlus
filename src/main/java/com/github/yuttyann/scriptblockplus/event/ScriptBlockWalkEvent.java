package com.github.yuttyann.scriptblockplus.event;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.inventory.ItemStack;

public class ScriptBlockWalkEvent extends ScriptBlockEvent implements Cancellable {

	private ItemStack item;
	private Location location;
	private boolean cancelled;

	public ScriptBlockWalkEvent(Player player, Block block, ItemStack item, Location location) {
		super(player, block);
		this.item = item;
		this.location = location;
	}

	public ItemStack getItem() {
		return item;
	}

	public Material getMaterial() {
		if (!hasItem()) {
			return Material.AIR;
		}
		return item.getType();
	}

	public Location getLocation() {
		return location;
	}

	public boolean hasBlock() {
		return block != null;
	}

	public boolean isBlockInHand() {
		if (!hasItem()) {
			return false;
		}
		return item.getType().isBlock();
	}

	public boolean hasItem() {
		return item != null;
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public void setCancelled(boolean cancel) {
		cancelled = cancel;
	}
}