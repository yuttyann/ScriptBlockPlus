package com.github.yuttyann.scriptblockplus.listener;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.enums.Permission;
import com.github.yuttyann.scriptblockplus.enums.ScriptType;
import com.github.yuttyann.scriptblockplus.event.ScriptBlockWalkEvent;
import com.github.yuttyann.scriptblockplus.file.SBConfig;
import com.github.yuttyann.scriptblockplus.manager.ScriptManager;
import com.github.yuttyann.scriptblockplus.script.SBPlayer;
import com.github.yuttyann.scriptblockplus.script.ScriptRead;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public class ScriptWalkListener extends ScriptManager implements Listener {

	public ScriptWalkListener(ScriptBlock plugin) {
		super(plugin, ScriptType.WALK);
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerMove(PlayerMoveEvent event) {
		SBPlayer sbPlayer = SBPlayer.get(event.getPlayer());
		BlockCoords blockCoords = new BlockCoords(sbPlayer.getLocation()).subtract(0.0D, 1.0D, 0.0D);
		if (blockCoords.getFullCoords().equals(sbPlayer.getOldFullCoords())) {
			return;
		}
		sbPlayer.setOldFullCoords(blockCoords.getFullCoords());
		if (mapManager.containsCoords(scriptType, blockCoords)) {
			ScriptBlockWalkEvent walkEvent = new ScriptBlockWalkEvent(sbPlayer.getPlayer(), blockCoords.getBlock());
			Bukkit.getPluginManager().callEvent(walkEvent);
			if (walkEvent.isCancelled()) {
				return;
			}
			if (!Permission.WALK_USE.has(sbPlayer)) {
				Utils.sendMessage(sbPlayer, SBConfig.getNotPermissionMessage());
				return;
			}
			new ScriptRead(this, sbPlayer.getUniqueId(), blockCoords).read(0);
		}
	}
}