package com.github.yuttyann.scriptblockplus.listener;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.event.ScriptBlockWalkEvent;
import com.github.yuttyann.scriptblockplus.file.SBConfig;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.script.ScriptRead;
import com.github.yuttyann.scriptblockplus.script.ScriptType;
import com.github.yuttyann.scriptblockplus.script.ScriptType.SBPermission;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.NotNull;

public class ScriptWalkListener extends ScriptListener {

	public ScriptWalkListener(@NotNull ScriptBlock plugin) {
		super(plugin, ScriptType.WALK);
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerMoveEvent(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		SBPlayer sbPlayer = SBPlayer.fromPlayer(player);
		BlockCoords blockCoords = new BlockCoords(sbPlayer.getLocation()).subtract(0.0D, 1.0D, 0.0D);
		if (blockCoords.getFullCoords().equals(sbPlayer.getOldFullCoords())) {
			return;
		}
		sbPlayer.setOldFullCoords(blockCoords.getFullCoords());
		if (mapManager.containsCoords(blockCoords, scriptType)) {
			ScriptBlockWalkEvent walkEvent = new ScriptBlockWalkEvent(player, blockCoords.getBlock());
			Bukkit.getPluginManager().callEvent(walkEvent);
			if (walkEvent.isCancelled()) {
				return;
			}
			if (!SBPermission.has(sbPlayer, ScriptType.WALK, false)) {
				SBConfig.NOT_PERMISSION.send(player);
				return;
			}
			new ScriptRead(player, blockCoords, this).read(0);
		}
	}
}