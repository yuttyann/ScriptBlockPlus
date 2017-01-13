package com.github.yuttyann.scriptblockplus.listener;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import com.github.yuttyann.scriptblockplus.Permission;
import com.github.yuttyann.scriptblockplus.event.ScriptBlockWalkEvent;
import com.github.yuttyann.scriptblockplus.manager.MapManager;
import com.github.yuttyann.scriptblockplus.manager.OptionManager;
import com.github.yuttyann.scriptblockplus.manager.OptionManager.ScriptType;
import com.github.yuttyann.scriptblockplus.util.BlockLocation;
import com.github.yuttyann.scriptblockplus.util.Utils;

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
			ScriptBlockWalkEvent scriptEvent = new ScriptBlockWalkEvent(event, player, location.getBlock(), Utils.getItemInHand(player), location);
			Bukkit.getServer().getPluginManager().callEvent(scriptEvent);
			if (scriptEvent.isCancelled()) {
				return;
			}
			if (!Permission.has(Permission.SCRIPTBLOCKPLUS_WALK_USE, player)) {
				Utils.sendPluginMessage(player, "§cパーミッションが無いため、実行できません。");
				return;
			}
			OptionManager.scriptExec(player, location, ScriptType.WALK);
		}
	}
}
