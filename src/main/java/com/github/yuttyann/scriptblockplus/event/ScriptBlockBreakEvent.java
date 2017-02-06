package com.github.yuttyann.scriptblockplus.event;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.inventory.ItemStack;

import com.github.yuttyann.scriptblockplus.BlockLocation;

public class ScriptBlockBreakEvent extends ScriptBlockEvent implements Cancellable {

	private Player player;
	private Block block;
	private ItemStack item;
	private Location location;
	private boolean cancelled;

	public ScriptBlockBreakEvent(Player player, Block block, ItemStack item, BlockLocation location) {
		this.player = player;
		this.block = block;
		this.item = item;
		this.location = location;
	}

	public Player getPlayer() {
		return player;
	}

	public Block getBlock() {
		return block;
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

	public boolean hasItem() {
		return item != null;
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public void setCancelled(boolean cancel) {
		this.cancelled = cancel;
	}
}