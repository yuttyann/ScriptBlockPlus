package com.github.yuttyann.scriptblockplus.event;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.github.yuttyann.scriptblockplus.file.Files;
import com.github.yuttyann.scriptblockplus.util.BlockLocation;

public class ScriptBlockInteractEvent extends ScriptBlockEvent implements Cancellable {

	private PlayerInteractEvent event;
	private Player player;
	private Block block;
	private ItemStack item;
	private Location location;
	private boolean cancelled;

	public ScriptBlockInteractEvent(PlayerInteractEvent event, Player player,
			Block block, ItemStack item, BlockLocation blockLocation) {
		this.event = event;
		this.player = player;
		this.block = block;
		this.item = item;
		this.location = blockLocation.toLocation();
	}

	public PlayerInteractEvent getPlayerInteractEvent() {
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

	public boolean hasItem() {
		return item != null;
	}

	public boolean isLeftClick() {
		return Files.getConfig().getBoolean("LeftClick");
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public void setLeftClick(boolean value) {
		Files.getConfig().set("LeftClick", value);
		Files.getConfig().save();
	}

	public void setCancelled(boolean cancel) {
		this.cancelled = cancel;
	}
}
