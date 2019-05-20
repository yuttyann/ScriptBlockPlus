package com.github.yuttyann.scriptblockplus.event;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class ScriptBlockBreakEvent extends TriggerEvent {

	public ScriptBlockBreakEvent(Player player, Block block) {
		super(player, block);
	}
}