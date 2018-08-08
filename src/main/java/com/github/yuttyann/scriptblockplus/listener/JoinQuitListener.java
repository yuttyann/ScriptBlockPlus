package com.github.yuttyann.scriptblockplus.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.player.BaseSBPlayer;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;

public class JoinQuitListener implements Listener {

	private ScriptBlock plugin;

	public JoinQuitListener(ScriptBlock plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerJoinEvent(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		SBPlayer sbPlayer = SBPlayer.fromPlayer(player);
		((BaseSBPlayer) sbPlayer).setPlayer(player);
		if (!sbPlayer.hasOldFullCoords()) {
			BlockCoords blockCoords = new BlockCoords(sbPlayer.getLocation());
			sbPlayer.setOldFullCoords(blockCoords.subtract(0.0D, 1.0D, 0.0D).getFullCoords());
		}
		if (sbPlayer.isOp()) {
			plugin.checkUpdate(sbPlayer, false);
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerQuit(PlayerQuitEvent event) {
		SBPlayer sbPlayer = SBPlayer.fromPlayer(event.getPlayer());
		sbPlayer.setScriptLine(null);
		sbPlayer.setActionType(null);
		sbPlayer.setClipboard(null);
	}
}