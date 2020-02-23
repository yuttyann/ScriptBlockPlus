package com.github.yuttyann.scriptblockplus.listener;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.enums.Permission;
import com.github.yuttyann.scriptblockplus.event.ScriptBlockBreakEvent;
import com.github.yuttyann.scriptblockplus.file.SBConfig;
import com.github.yuttyann.scriptblockplus.script.ScriptRead;
import com.github.yuttyann.scriptblockplus.script.ScriptType;
import com.github.yuttyann.scriptblockplus.script.ScriptType.SBPermission;
import com.github.yuttyann.scriptblockplus.utils.ItemUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import org.jetbrains.annotations.NotNull;

public class ScriptBreakListener extends ScriptListener {

	public ScriptBreakListener(@NotNull ScriptBlock plugin) {
		super(plugin, ScriptType.BREAK);
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockBreakEvent(BlockBreakEvent event) {
		Player player = event.getPlayer();
		ItemStack item = ItemUtils.getItemInMainHand(player);
		if (ItemUtils.isBlockSelector(item) && Permission.TOOL_BLOCKSELECTOR.has(player)
				|| ItemUtils.isScriptEditor(item) && Permission.TOOL_SCRIPTEDITOR.has(player)) {
			event.setCancelled(true);
			return;
		}
		Block block = event.getBlock();
		Location location = block.getLocation();
		if (mapManager.containsCoords(location, scriptType)) {
			ScriptBlockBreakEvent breakEvent = new ScriptBlockBreakEvent(player, block);
			Bukkit.getPluginManager().callEvent(breakEvent);
			if (breakEvent.isCancelled()) {
				return;
			}
			if (!SBPermission.has(player, ScriptType.BREAK, false)) {
				Utils.sendMessage(player, SBConfig.getNotPermissionMessage());
				return;
			}
			new ScriptRead(player, location, this).read(0);
		}
	}
}