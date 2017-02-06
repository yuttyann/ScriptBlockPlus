package com.github.yuttyann.scriptblockplus.listener;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import com.github.yuttyann.scriptblockplus.BlockLocation;
import com.github.yuttyann.scriptblockplus.enums.Permission;
import com.github.yuttyann.scriptblockplus.enums.ScriptType;
import com.github.yuttyann.scriptblockplus.event.ScriptBlockWalkEvent;
import com.github.yuttyann.scriptblockplus.file.Messages;
import com.github.yuttyann.scriptblockplus.manager.MapManager;
import com.github.yuttyann.scriptblockplus.manager.OptionManager;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public class PlayerMoveListener implements Listener {

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerMoveEvent(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		UUID uuid = player.getUniqueId();
		BlockLocation location = BlockLocation.fromLocation(player.getLocation()).subtract(0, 0.5, 0);
		String fullCoords = location.getFullCoords();
		if (MapManager.getOldLocation().containsKey(uuid) && MapManager.getOldLocation().get(uuid).equals(fullCoords)) {
			return;
		}
		MapManager.getOldLocation().put(uuid, fullCoords);
		if (MapManager.getWalkCoords().contains(fullCoords)) {
			ScriptBlockWalkEvent scEvent = new ScriptBlockWalkEvent(player, location.getBlock(), Utils.getItemInHand(player), location);
			Utils.callEvent(scEvent);
			if (scEvent.isCancelled()) {
				return;
			}
			if (!Permission.has(Permission.SCRIPTBLOCKPLUS_WALK_USE, player)) {
				Utils.sendPluginMessage(player, Messages.notPermissionMessage);
				return;
			}
			new OptionManager(location, ScriptType.WALK).scriptExec(player);
		}
	}
}