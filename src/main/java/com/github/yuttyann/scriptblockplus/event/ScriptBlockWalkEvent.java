package com.github.yuttyann.scriptblockplus.event;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

import com.github.yuttyann.scriptblockplus.utils.BlockLocation;

public class ScriptBlockWalkEvent extends ScriptBlockEvent implements Cancellable {

	private PlayerMoveEvent event;
	private Player player;
	private Block block;
	private ItemStack item;
	private Location location;
	private boolean cancelled;

	public ScriptBlockWalkEvent(PlayerMoveEvent event, Player player,
			Block block, ItemStack item, BlockLocation blockLocation) {
		this.event = event;
		this.player = player;
		this.block = block;
		this.item = item;
		this.location = blockLocation.toLocation();
	}

	public PlayerMoveEvent getPlayerMoveEvent() {
		return event;
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

	public Location getLocation() {
		return location;
	}

	public boolean hasBlock() {
		return block != null;
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