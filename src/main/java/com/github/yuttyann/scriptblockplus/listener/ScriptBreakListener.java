package com.github.yuttyann.scriptblockplus.listener;

import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.enums.Permission;
import com.github.yuttyann.scriptblockplus.event.ScriptBlockBreakEvent;
import com.github.yuttyann.scriptblockplus.file.config.SBConfig;
import com.github.yuttyann.scriptblockplus.listener.item.ItemAction;
import com.github.yuttyann.scriptblockplus.script.ScriptRead;
import com.github.yuttyann.scriptblockplus.script.ScriptType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
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
		if (ItemAction.has(player, player.getInventory().getItemInMainHand(), true)) {
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