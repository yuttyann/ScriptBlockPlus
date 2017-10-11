package com.github.yuttyann.scriptblockplus.event;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public abstract class ScriptBlockEvent extends PlayerEvent implements Cancellable {

	private static final HandlerList handlers = new HandlerList();

	protected Block block;
	protected Location location;

	public ScriptBlockEvent(Player player) {
		super(player);
	}

	public ScriptBlockEvent(Player player, Block block) {
		super(player);
		this.block = block;
		this.location = block.getLocation();
	}

	public ScriptBlockEvent(Player player, Block block, Location location) {
		super(player);
		this.block = block;
		this.location = location;
	}

	public final Block getBlock() {
		return block;
	}

	public final Location getLocation() {
		return location;
	}

	@Override
	public abstract boolean isCancelled();

	@Override
	public abstract void setCancelled(boolean cancel);

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}