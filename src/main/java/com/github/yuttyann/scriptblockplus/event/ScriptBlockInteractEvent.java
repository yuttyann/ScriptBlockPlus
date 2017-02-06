package com.github.yuttyann.scriptblockplus.event;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.inventory.ItemStack;

import com.github.yuttyann.scriptblockplus.BlockLocation;
import com.github.yuttyann.scriptblockplus.file.Files;
import com.github.yuttyann.scriptblockplus.file.YamlConfig;

public class ScriptBlockInteractEvent extends ScriptBlockEvent implements Cancellable {

	private Player player;
	private Block block;
	private ItemStack item;
	private YamlConfig config;
	private Location location;
	private boolean cancelled;

	public ScriptBlockInteractEvent(Player player, Block block, ItemStack item, BlockLocation location) {
		this.player = player;
		this.block = block;
		this.item = item;
		this.config = Files.getConfig();
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