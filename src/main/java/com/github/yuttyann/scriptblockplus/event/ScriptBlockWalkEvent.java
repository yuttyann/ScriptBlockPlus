package com.github.yuttyann.scriptblockplus.event;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * ScriptBlockPlus ScriptBlockWalkEvent イベントクラス
 * @author yuttyann44581
 */
public class ScriptBlockWalkEvent extends TriggerEvent {

	public ScriptBlockWalkEvent(@NotNull final Player player, @NotNull final Block block) {
		super(player, block);
	}
}