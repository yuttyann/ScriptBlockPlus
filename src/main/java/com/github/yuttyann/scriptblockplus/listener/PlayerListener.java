package com.github.yuttyann.scriptblockplus.listener;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.event.ScriptReadEndEvent;
import com.github.yuttyann.scriptblockplus.event.ScriptReadStartEvent;
import com.github.yuttyann.scriptblockplus.hook.plugin.ProtocolLib;
import com.github.yuttyann.scriptblockplus.listener.item.ItemAction;
import com.github.yuttyann.scriptblockplus.listener.item.action.ScriptViewer;
import com.github.yuttyann.scriptblockplus.player.BaseSBPlayer;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.region.CuboidRegion;
import com.github.yuttyann.scriptblockplus.script.endprocess.EndInventory;
import com.github.yuttyann.scriptblockplus.script.option.chat.ActionBar;
import com.github.yuttyann.scriptblockplus.script.option.other.ItemCost;
import com.github.yuttyann.scriptblockplus.utils.StreamUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * ScriptBlockPlus JoinQuitListener クラス
 * @author yuttyann44581
 */
public class PlayerListener implements Listener {

    private static final String KEY_INVENTORY = Utils.randomUUID();

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerJoin(PlayerJoinEvent event) {
        var sbPlayer = SBPlayer.fromPlayer(event.getPlayer());
        ((BaseSBPlayer) sbPlayer).setOnline(true);
        if (!sbPlayer.getOldBlockCoords().isPresent()) {
            sbPlayer.setOldBlockCoords(new BlockCoords(sbPlayer.getLocation()).subtract(0, 1, 0));
        }
        if (sbPlayer.isOp()) {
            ScriptBlock.getInstance().checkUpdate(sbPlayer, false);
        }
        var objectMap = sbPlayer.getObjectMap();
        var items = objectMap.get(ItemCost.KEY_PLAYER, EndInventory.EMPTY_ARRAY);
        if (items.length > 0) {
            try {
                sbPlayer.getInventory().setContents(items);
            } finally {
                objectMap.remove(ItemCost.KEY_PLAYER);
                Utils.updateInventory(sbPlayer.getPlayer());
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerQuit(PlayerQuitEvent event) {
        var sbPlayer = SBPlayer.fromPlayer(event.getPlayer());
        var cuboidRegion = (CuboidRegion) sbPlayer.getRegion();
        sbPlayer.setScriptLine(null);
        sbPlayer.setScriptEdit(null);
        sbPlayer.setSBClipboard(null);
        cuboidRegion.setWorld(null);
        cuboidRegion.setVector1(null);
        cuboidRegion.setVector2(null);
        try {
            ScriptViewer.PLAYERS.remove(sbPlayer);
            StreamUtils.ifAction(ProtocolLib.INSTANCE.has(), () -> ProtocolLib.GLOW_ENTITY.destroyAll(sbPlayer));
        } finally {
            ((BaseSBPlayer) sbPlayer).setOnline(false);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerItemHeld(PlayerItemHeldEvent event) {
        var player = event.getPlayer();
        var oldSlot = player.getInventory().getItem(event.getPreviousSlot());
        if (ItemAction.has(player, oldSlot, true)) {
            ActionBar.send(SBPlayer.fromPlayer(player), "");
        }
        var newSlot = player.getInventory().getItem(event.getNewSlot());
        ItemAction.callSlot(player, newSlot, event.getNewSlot(), event.getPreviousSlot());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryClick(InventoryClickEvent event) {
        var inventory = Optional.ofNullable(event.getClickedInventory());
        if (!inventory.isPresent() || inventory.get().getType() != InventoryType.PLAYER) {
            return;
        }
        var sbPlayer = SBPlayer.fromUUID(event.getWhoClicked().getUniqueId());
        if (sbPlayer.getObjectMap().get(KEY_INVENTORY, Collections.EMPTY_SET).size() > 0) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onScriptReadStart(ScriptReadStartEvent event) {
        var objectMap = event.getSBRead().getSBPlayer().getObjectMap();
        var uuids = objectMap.get(KEY_INVENTORY, new HashSet<UUID>());
        uuids.add(event.getUniqueId());
        objectMap.put(KEY_INVENTORY, uuids);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onScriptEndStart(ScriptReadEndEvent event) {
        var objectMap = event.getSBRead().getSBPlayer().getObjectMap();
        objectMap.get(KEY_INVENTORY, new HashSet<>()).remove(event.getUniqueId());
    }
}