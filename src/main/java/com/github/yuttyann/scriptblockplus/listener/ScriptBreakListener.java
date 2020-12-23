package com.github.yuttyann.scriptblockplus.listener;

import com.github.yuttyann.scriptblockplus.enums.Permission;
import com.github.yuttyann.scriptblockplus.event.ScriptBlockBreakEvent;
import com.github.yuttyann.scriptblockplus.file.config.SBConfig;
import com.github.yuttyann.scriptblockplus.file.json.BlockScriptJson;
import com.github.yuttyann.scriptblockplus.listener.item.ItemAction;
import com.github.yuttyann.scriptblockplus.script.ScriptRead;
import com.github.yuttyann.scriptblockplus.script.ScriptType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

/**
 * ScriptBlockPlus ScriptBreakListener クラス
 * @author yuttyann44581
 */
public class ScriptBreakListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (ItemAction.has(player, player.getInventory().getItemInMainHand(), true)) {
            event.setCancelled(true);
            return;
        }
        Block block = event.getBlock();
        Location location = block.getLocation();
        if (BlockScriptJson.has(location, ScriptType.BREAK)) {
            ScriptBlockBreakEvent breakEvent = new ScriptBlockBreakEvent(player, block);
            Bukkit.getPluginManager().callEvent(breakEvent);
            if (breakEvent.isCancelled()) {
                return;
            }
            if (!Permission.has(player, ScriptType.BREAK, false)) {
                SBConfig.NOT_PERMISSION.send(player);
                return;
            }
            new ScriptRead(player, location, ScriptType.BREAK).read(0);
        }
    }
}