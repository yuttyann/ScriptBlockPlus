package com.github.yuttyann.scriptblockplus.listener;

import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.enums.Permission;
import com.github.yuttyann.scriptblockplus.event.ScriptBlockBreakEvent;
import com.github.yuttyann.scriptblockplus.file.config.SBConfig;
import com.github.yuttyann.scriptblockplus.script.ScriptRead;
import com.github.yuttyann.scriptblockplus.script.ScriptType;
import com.github.yuttyann.scriptblockplus.utils.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * ScriptBlockPlus ScriptBreakListener クラス
 * @author yuttyann44581
 */
public class ScriptBreakListener extends ScriptListener {

	public ScriptBreakListener(@NotNull ScriptBlock plugin) {
		super(plugin, ScriptType.BREAK);
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockBreakEvent(BlockBreakEvent event) {
		Player player = event.getPlayer();
		ItemStack item = player.getInventory().getItemInMainHand();
		if (ItemUtils.isBlockSelector(item) && Permission.TOOL_BLOCK_SELECTOR.has(player)
				|| ItemUtils.isScriptEditor(item) && Permission.TOOL_SCRIPT_EDITOR.has(player)
					|| ItemUtils.isScriptViewer(item) && Permission.TOOL_SCRIPT_VIEWER.has(player)) {
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
			if (!Permission.has(player, ScriptType.BREAK, false)) {
				SBConfig.NOT_PERMISSION.send(player);
				return;
			}
			new ScriptRead(player, location, this).read(0);
		}
	}
}