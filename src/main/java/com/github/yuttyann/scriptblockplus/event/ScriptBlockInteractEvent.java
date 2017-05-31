package com.github.yuttyann.scriptblockplus.event;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.inventory.ItemStack;

import com.github.yuttyann.scriptblockplus.file.Files;
import com.github.yuttyann.scriptblockplus.file.YamlConfig;

public class ScriptBlockInteractEvent extends ScriptBlockEvent implements Cancellable {

	private Player player;
	private Block block;
	private ItemStack item;
	private Location location;
	private boolean cancelled;

	public ScriptBlockInteractEvent(Player player, Block block, ItemStack item, Location location) {
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

	public boolean isBlockInHand() {
		if (!hasItem()) {
			return false;
		}
		return item.getType().isBlock();
	}

	public boolean getLeftClick() {
		return Files.getConfig().getBoolean("LeftClick");
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public void setLeftClick(boolean value) {
		YamlConfig config = Files.getConfig();
		config.set("LeftClick", value);
		config.save();
	}

	public void setCancelled(boolean cancel) {
		this.cancelled = cancel;
	}
}