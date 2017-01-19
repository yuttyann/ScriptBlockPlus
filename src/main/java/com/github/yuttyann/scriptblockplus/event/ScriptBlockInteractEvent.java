package com.github.yuttyann.scriptblockplus.event;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.github.yuttyann.scriptblockplus.file.Files;
import com.github.yuttyann.scriptblockplus.file.Yaml;
import com.github.yuttyann.scriptblockplus.utils.BlockLocation;

public class ScriptBlockInteractEvent extends ScriptBlockEvent implements Cancellable {

	private PlayerInteractEvent event;
	private Player player;
	private Block block;
	private ItemStack item;
	private Yaml config;
	private Location location;
	private boolean cancelled;

	public ScriptBlockInteractEvent(PlayerInteractEvent event, Player player,
			Block block, ItemStack item, BlockLocation blockLocation) {
		this.event = event;
		this.player = player;
		this.block = block;
		this.item = item;
		this.config = Files.getConfig();
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
		return config.getBoolean("LeftClick");
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public void setLeftClick(boolean value) {
		config.set("LeftClick", value);
		config.save();
	}

	public void setCancelled(boolean cancel) {
		this.cancelled = cancel;
	}
}