package com.github.yuttyann.scriptblockplus.listener;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.github.yuttyann.scriptblockplus.BlockLocation;
import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.enums.Metadata;
import com.github.yuttyann.scriptblockplus.manager.MapManager;
import com.github.yuttyann.scriptblockplus.metadata.SBMetadata;

public class PlayerJoinQuitListener implements Listener {

	private MapManager mapManager;

	public PlayerJoinQuitListener(ScriptBlock plugin) {
		this.mapManager = plugin.getMapManager();
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		UUID uuid = player.getUniqueId();
		if (!mapManager.getOldLocation().containsKey(uuid)) {
			BlockLocation location = BlockLocation.fromLocation(player.getLocation());
			mapManager.getOldLocation().put(uuid, location.subtract(0.0D, 1.0D, 0.0D).getFullCoords());
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		SBMetadata.removeAll(player, Metadata.PLAYERCLICK, Metadata.SCRIPTFILE, Metadata.SCRIPTTEXT);
		mapManager.removeEvents(player.getUniqueId());
	}
}