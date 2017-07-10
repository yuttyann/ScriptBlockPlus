package com.github.yuttyann.scriptblockplus.listener;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.enums.Metadata;
import com.github.yuttyann.scriptblockplus.manager.MapManager;

public class JoinQuitListener implements Listener {

	private ScriptBlock plugin;
	private MapManager mapManager;

	public JoinQuitListener(ScriptBlock plugin) {
		this.plugin = plugin;
		this.mapManager = plugin.getMapManager();
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		UUID uuid = player.getUniqueId();
		if (!mapManager.getOldLocation().containsKey(uuid)) {
			Location location = player.getLocation().clone();
			mapManager.getOldLocation().put(uuid, BlockCoords.getFullCoords(location.subtract(0.0D, 1.0D, 0.0D)));
		}
		plugin.getUpdater().sendCheckMessage(player);
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerQuit(PlayerQuitEvent event) {
		Metadata.removeAll(event.getPlayer(), Metadata.PLAYERCLICK, Metadata.SCRIPTFILE, Metadata.SCRIPTTEXT);
	}
}