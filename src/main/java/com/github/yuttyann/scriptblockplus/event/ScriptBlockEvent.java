package com.github.yuttyann.scriptblockplus.event;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public abstract class ScriptBlockEvent extends PlayerEvent {

	private static final HandlerList handlers = new HandlerList();

	protected Block block;

	public ScriptBlockEvent(Player player) {
		super(player);
	}

	public ScriptBlockEvent(Player player, Block block) {
		super(player);
		this.block = block;
	}

	public final Block getBlock() {
		return block;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}