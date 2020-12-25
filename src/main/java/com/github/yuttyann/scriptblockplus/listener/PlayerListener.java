package com.github.yuttyann.scriptblockplus.listener;

import java.util.Optional;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.event.ScriptReadEndEvent;
import com.github.yuttyann.scriptblockplus.event.ScriptReadStartEvent;
import com.github.yuttyann.scriptblockplus.listener.item.ItemAction;
import com.github.yuttyann.scriptblockplus.player.BaseSBPlayer;
import com.github.yuttyann.scriptblockplus.player.ObjectMap;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.region.CuboidRegion;
import com.github.yuttyann.scriptblockplus.script.option.chat.ActionBar;
import com.github.yuttyann.scriptblockplus.script.option.other.ItemCost;
import com.github.yuttyann.scriptblockplus.utils.Utils;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * ScriptBlockPlus JoinQuitListener クラス
 * @author yuttyann44581
 */
public class PlayerListener implements Listener {

    private static final String KEY_INVENTORY = Utils.randomUUID();

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerJoin(PlayerJoinEvent event) {
        BaseSBPlayer sbPlayer = (BaseSBPlayer) SBPlayer.fromPlayer(event.getPlayer());
        sbPlayer.setOnline(true);
        if (!sbPlayer.getOldBlockCoords().isPresent()) {
            sbPlayer.setOldBlockCoords(new BlockCoords(sbPlayer.getLocation()).subtract(0, 1, 0));
        }
        if (sbPlayer.isOp()) {
            ScriptBlock.getInstance().checkUpdate(sbPlayer, false);
        }

        // ItemCost アイテム返却
        ObjectMap objectMap = sbPlayer.getObjectMap();
        if (objectMap.has(ItemCost.KEY_PLAYER)) {
            sbPlayer.getInventory().setContents(objectMap.get(ItemCost.KEY_PLAYER, new ItemStack[0]));
            objectMap.remove(ItemCost.KEY_PLAYER);
            Utils.updateInventory(sbPlayer.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerQuit(PlayerQuitEvent event) {
        BaseSBPlayer sbPlayer = (BaseSBPlayer) SBPlayer.fromPlayer(event.getPlayer());
        sbPlayer.setOnline(false);
        sbPlayer.setScriptLine(null);
        sbPlayer.setScriptEdit(null);
        sbPlayer.setSBClipboard(null);

        CuboidRegion region = ((CuboidRegion) sbPlayer.getRegion());
        region.setWorld(null);
        region.setPos1(null);
        region.setPos2(null);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerItemHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        ItemStack oldSlot = player.getInventory().getItem(event.getPreviousSlot());
        if (ItemAction.has(player, oldSlot, true)) {
            ActionBar.send(SBPlayer.fromPlayer(player), "");
        }
        ItemStack newSlot = player.getInventory().getItem(event.getNewSlot());
        ItemAction.callSlot(player, newSlot, event.getNewSlot(), event.getPreviousSlot());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryClick(InventoryClickEvent event) {
        Optional<Inventory> inventory = Optional.ofNullable(event.getClickedInventory());
        if (!inventory.isPresent() || inventory.get().getType() != InventoryType.PLAYER) {
            return;
        }
        SBPlayer sbPlayer = SBPlayer.fromUUID(event.getWhoClicked().getUniqueId());
        if (sbPlayer.getObjectMap().getBoolean(KEY_INVENTORY)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onScriptReadStart(ScriptReadStartEvent event) {
        event.getSBRead().getSBPlayer().getObjectMap().put(KEY_INVENTORY, true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onScriptEndStart(ScriptReadEndEvent event) {
        event.getSBRead().getSBPlayer().getObjectMap().remove(KEY_INVENTORY);
    }
}