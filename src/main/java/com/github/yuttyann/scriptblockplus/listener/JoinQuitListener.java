package com.github.yuttyann.scriptblockplus.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;

public class JoinQuitListener implements Listener {

	private ScriptBlock plugin;

	public JoinQuitListener(ScriptBlock plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerJoin(PlayerJoinEvent event) {
		SBPlayer sbPlayer = SBPlayer.get(event.getPlayer());
		sbPlayer.setPlayer(event.getPlayer());
		if (sbPlayer.getOldFullCoords() == null) {
			BlockCoords blockCoords = new BlockCoords(sbPlayer.getLocation());
			sbPlayer.setOldFullCoords(blockCoords.subtract(0.0D, 1.0D, 0.0D).getFullCoords());
		}
		plugin.getUpdater().sendCheckMessage(sbPlayer);
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerQuit(PlayerQuitEvent event) {
		SBPlayer sbPlayer = SBPlayer.get(event.getPlayer());
		sbPlayer.setPlayer(null);
		sbPlayer.setScriptLine(null);
		sbPlayer.setClickAction(null);
		sbPlayer.setClipboard(null);
	}
}