package com.github.yuttyann.scriptblockplus.listener;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import com.github.yuttyann.scriptblockplus.enums.Permission;
import com.github.yuttyann.scriptblockplus.enums.ScriptType;
import com.github.yuttyann.scriptblockplus.event.ScriptBlockBreakEvent;
import com.github.yuttyann.scriptblockplus.file.Messages;
import com.github.yuttyann.scriptblockplus.manager.MapManager;
import com.github.yuttyann.scriptblockplus.manager.OptionManager;
import com.github.yuttyann.scriptblockplus.utils.BlockLocation;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public class BlockListener implements Listener {

	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		ItemStack item = Utils.getItemInHand(player);
		if (Utils.checkItem(item, Material.BLAZE_ROD, "§dScript Editor")
				&& Permission.has(Permission.SCRIPTBLOCKPLUS_TOOL_SCRIPTEDITOR, player)) {
			event.setCancelled(true);
		}
		Block block = event.getBlock();
		BlockLocation location = BlockLocation.fromLocation(block.getLocation());
		if (MapManager.getBreakCoords().contains(location.getFullCoords())) {
			ScriptBlockBreakEvent scEvent = new ScriptBlockBreakEvent(player, location.getBlock(), item, location);
			Utils.callEvent(scEvent);
			if (scEvent.isCancelled()) {
				return;
			}
			if (!Permission.has(Permission.SCRIPTBLOCKPLUS_BREAK_USE, player)) {
				Utils.sendPluginMessage(player, Messages.notPermissionMessage);
				return;
			}
			new OptionManager(location, ScriptType.BREAK).scriptExec(player);
		}
	}
}