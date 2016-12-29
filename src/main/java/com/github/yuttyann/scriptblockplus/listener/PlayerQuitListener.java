package com.github.yuttyann.scriptblockplus.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import com.github.yuttyann.scriptblockplus.manager.MetadataManager;

public class PlayerQuitListener implements Listener {

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerQuit(PlayerQuitEvent event) {
		MetadataManager.removeAllMetadata(event.getPlayer());
	}
}
