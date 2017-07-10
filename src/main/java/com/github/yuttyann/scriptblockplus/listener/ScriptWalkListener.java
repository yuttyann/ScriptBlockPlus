package com.github.yuttyann.scriptblockplus.listener;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.enums.Permission;
import com.github.yuttyann.scriptblockplus.enums.ScriptType;
import com.github.yuttyann.scriptblockplus.event.ScriptBlockWalkEvent;
import com.github.yuttyann.scriptblockplus.file.Lang;
import com.github.yuttyann.scriptblockplus.manager.ScriptManager;
import com.github.yuttyann.scriptblockplus.script.ScriptRead;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public class ScriptWalkListener extends ScriptManager implements Listener {

	public ScriptWalkListener(ScriptBlock plugin) {
		super(plugin, ScriptType.WALK);
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		UUID uuid = player.getUniqueId();
		BlockCoords blockCoords = new BlockCoords(player.getLocation().clone().subtract(0.0D, 1.0D, 0.0D));
		String fullCoords = blockCoords.getFullCoords();
		if (mapManager.getOldLocation().containsKey(uuid) && mapManager.getOldLocation().get(uuid).equals(fullCoords)) {
			return;
		}
		mapManager.getOldLocation().put(uuid, fullCoords);
		if (mapManager.containsLocation(blockCoords, scriptType)) {
			ScriptBlockWalkEvent walkEvent = new ScriptBlockWalkEvent(player, blockCoords.getBlock(), Utils.getItemInHand(player), blockCoords);
			Utils.callEvent(walkEvent);
			if (walkEvent.isCancelled()) {
				return;
			}
			if (!Permission.has(Permission.SCRIPTBLOCKPLUS_WALK_USE, player)) {
				Utils.sendPluginMessage(player, Lang.getNotPermissionMessage());
				return;
			}
			new ScriptRead(this, player, blockCoords).read(0);
		}
	}
}