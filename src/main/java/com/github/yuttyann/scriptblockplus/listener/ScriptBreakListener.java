package com.github.yuttyann.scriptblockplus.listener;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.enums.Permission;
import com.github.yuttyann.scriptblockplus.enums.ScriptType;
import com.github.yuttyann.scriptblockplus.event.ScriptBlockBreakEvent;
import com.github.yuttyann.scriptblockplus.file.Lang;
import com.github.yuttyann.scriptblockplus.manager.ScriptManager;
import com.github.yuttyann.scriptblockplus.script.ScriptRead;
import com.github.yuttyann.scriptblockplus.utils.Utils;

public class ScriptBreakListener extends ScriptManager implements Listener {

	public ScriptBreakListener(ScriptBlock plugin) {
		super(plugin, ScriptType.BREAK);
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		ItemStack item = Utils.getItemInHand(player);
		if (Utils.checkItem(item, Material.BLAZE_ROD, Lang.ITEM_SCRIPTEDITOR)
				&& Permission.has(Permission.SCRIPTBLOCKPLUS_TOOL_SCRIPTEDITOR, player)) {
			event.setCancelled(true);
			return;
		}
		Block block = event.getBlock();
		BlockCoords blockCoords = new BlockCoords(block.getLocation());
		if (mapManager.containsLocation(blockCoords, scriptType)) {
			ScriptBlockBreakEvent breakEvent = new ScriptBlockBreakEvent(player, event.getBlock(), item, blockCoords);
			Utils.callEvent(breakEvent);
			if (breakEvent.isCancelled()) {
				return;
			}
			if (!Permission.has(Permission.SCRIPTBLOCKPLUS_BREAK_USE, player)) {
				Utils.sendPluginMessage(player, Lang.getNotPermissionMessage());
				return;
			}
			new ScriptRead(this, player, blockCoords).read(0);
		}
	}
}