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
import com.github.yuttyann.scriptblockplus.manager.MapManager;
import com.github.yuttyann.scriptblockplus.manager.MetadataManager;
import com.github.yuttyann.scriptblockplus.manager.MetadataManager.ScriptFile;

public class PlayerJoinQuitListener implements Listener {

	private ScriptBlock plugin;
	private MapManager mapManager;

	public PlayerJoinQuitListener(ScriptBlock plugin) {
		this.mapManager = plugin.getMapManager();
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		UUID uuid = player.getUniqueId();
		if (!mapManager.getOldLocation().containsKey(uuid)) {
			mapManager.getOldLocation().put(uuid, BlockLocation.fromLocation(player.getLocation()).subtract(0, 0.5, 0).getFullCoords());
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		MetadataManager.removeAllMetadata(plugin, player);
		ScriptFile.removeAllMetadata(plugin, player);
		mapManager.removeEvents(player.getUniqueId());
	}
}