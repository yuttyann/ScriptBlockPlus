package com.github.yuttyann.scriptblockplus.event;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class ScriptBlockWalkEvent extends TriggerEvent {

	public ScriptBlockWalkEvent(Player player, Block block) {
		super(player, block);
	}
}