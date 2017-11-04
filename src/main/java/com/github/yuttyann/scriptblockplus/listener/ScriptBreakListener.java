package com.github.yuttyann.scriptblockplus.listener;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.enums.Permission;
import com.github.yuttyann.scriptblockplus.enums.ScriptType;
import com.github.yuttyann.scriptblockplus.event.ScriptBlockBreakEvent;
import com.github.yuttyann.scriptblockplus.file.SBConfig;
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
		if (isScriptEditor(player) && Permission.TOOL_SCRIPTEDITOR.has(player)) {
			event.setCancelled(true);
			return;
		}
		Block block = event.getBlock();
		Location location = block.getLocation();
		if (mapManager.containsCoords(scriptType, location)) {
			ScriptBlockBreakEvent breakEvent = new ScriptBlockBreakEvent(player, block);
			Bukkit.getPluginManager().callEvent(breakEvent);
			if (breakEvent.isCancelled()) {
				return;
			}
			if (!Permission.BREAK_USE.has(player)) {
				Utils.sendMessage(player, SBConfig.getNotPermissionMessage());
				return;
			}
			new ScriptRead(this, player, location).read(0);
		}
	}

	private boolean isScriptEditor(Player player) {
		ItemStack item = Utils.getItemInMainHand(player);
		return Utils.checkItem(item, Material.BLAZE_ROD, "§dScript Editor");
	}
}