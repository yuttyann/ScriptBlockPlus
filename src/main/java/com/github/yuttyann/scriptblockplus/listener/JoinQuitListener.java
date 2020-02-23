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
import com.github.yuttyann.scriptblockplus.player.ObjectMap;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.region.CuboidRegion;
import com.github.yuttyann.scriptblockplus.script.option.other.ItemCost;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import org.jetbrains.annotations.NotNull;

public class JoinQuitListener implements Listener {

	private ScriptBlock plugin;

	public JoinQuitListener(@NotNull ScriptBlock plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerJoinEvent(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		BaseSBPlayer sbPlayer = plugin.fromPlayer(player);
		sbPlayer.setPlayer(player).setOnline(true);
		if (!sbPlayer.hasOldFullCoords()) {
			BlockCoords blockCoords = new BlockCoords(player.getLocation());
			sbPlayer.setOldFullCoords(blockCoords.subtract(0.0D, 1.0D, 0.0D).getFullCoords());
		}
		if (player.isOp()) {
			plugin.checkUpdate(sbPlayer, false);
		}

		// ItemCost アイテム返却
		ObjectMap objectMap = sbPlayer.getObjectMap();
		if (objectMap.has(ItemCost.KEY_ITEM_PLAYER)) {
			player.getInventory().setContents(objectMap.get(ItemCost.KEY_ITEM_PLAYER));
			objectMap.remove(ItemCost.KEY_ITEM_PLAYER);
			Utils.updateInventory(player);
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerQuit(PlayerQuitEvent event) {
		SBPlayer sbPlayer = SBPlayer.fromPlayer(event.getPlayer());
		((BaseSBPlayer) sbPlayer).setOnline(false);
		sbPlayer.setScriptLine(null);
		sbPlayer.setActionType(null);
		sbPlayer.setClipboard(null);

		CuboidRegion region = ((CuboidRegion) sbPlayer.getRegion());
		region.setWorld(null);
		region.setPos1(null);
		region.setPos2(null);
	}
}