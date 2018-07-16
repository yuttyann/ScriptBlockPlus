package com.github.yuttyann.scriptblockplus.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.event.ScriptBlockWalkEvent;
import com.github.yuttyann.scriptblockplus.file.SBConfig;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.script.ScriptRead;
import com.github.yuttyann.scriptblockplus.script.ScriptType;
import com.github.yuttyann.scriptblockplus.script.ScriptType.SPermission;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public class ScriptWalkListener extends IAssist {

	public ScriptWalkListener(ScriptBlock plugin) {
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
		if (mapManager.containsCoords(scriptType, blockCoords)) {
			ScriptBlockWalkEvent walkEvent = new ScriptBlockWalkEvent(player, blockCoords.getBlock());
			Bukkit.getPluginManager().callEvent(walkEvent);
			if (walkEvent.isCancelled()) {
				return;
			}
			if (!SPermission.has(sbPlayer, ScriptType.WALK, false)) {
				Utils.sendMessage(sbPlayer, SBConfig.getNotPermissionMessage());
				return;
			}
			new ScriptRead(this, player, blockCoords).read(0);
		}
	}
}