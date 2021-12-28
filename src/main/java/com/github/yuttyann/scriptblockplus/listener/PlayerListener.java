/**
 * ScriptBlockPlus - Allow you to add script to any blocks.
 * Copyright (C) 2021 yuttyann44581
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <https://www.gnu.org/licenses/>.
 */
package com.github.yuttyann.scriptblockplus.listener;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.event.ScriptReadEndEvent;
import com.github.yuttyann.scriptblockplus.event.ScriptReadStartEvent;
import com.github.yuttyann.scriptblockplus.item.ItemAction;
import com.github.yuttyann.scriptblockplus.item.action.ScriptViewer;
import com.github.yuttyann.scriptblockplus.item.action.TickRunnable;
import com.github.yuttyann.scriptblockplus.item.gui.UserWindow;
import com.github.yuttyann.scriptblockplus.player.BaseSBPlayer;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.script.option.chat.ActionBar;
import com.github.yuttyann.scriptblockplus.utils.Utils;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * ScriptBlockPlus JoinQuitListener クラス
 * @author yuttyann44581
 */
public final class PlayerListener implements Listener {

    private static final String KEY_INVENTORY = Utils.randomUUID();

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerJoin(PlayerJoinEvent event) {
        var sbPlayer = SBPlayer.fromPlayer(event.getPlayer());
        try {
            ((BaseSBPlayer) sbPlayer).setOnline(true);
        } finally {
            sbPlayer.setOldBlockCoords(BlockCoords.of(sbPlayer.getLocation()).subtract(0, 1, 0));
            if (sbPlayer.isOp()) {
                ScriptBlock.getInstance().checkUpdate(sbPlayer.getPlayer(), false);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerQuit(PlayerQuitEvent event) {
        var sbPlayer = SBPlayer.fromPlayer(event.getPlayer());
        try {
            ScriptViewer.PLAYERS.remove(sbPlayer);
            TickRunnable.GLOW_ENTITY.destroyAll(sbPlayer);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        } finally {
            ((BaseSBPlayer) sbPlayer).init();
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
        var sbPlayer = SBPlayer.fromUUID(event.getWhoClicked().getUniqueId());
        var inventory = Optional.ofNullable(event.getClickedInventory());
        if (inventory.isPresent() && inventory.get().getType() == InventoryType.PLAYER) {
            if (sbPlayer.getObjectMap().get(KEY_INVENTORY, Collections.EMPTY_SET).size() > 0) {
                event.setCancelled(true);
            }
        }
        var window = (UserWindow) sbPlayer.getObjectMap().get(UserWindow.KEY_WINDOW);
        if (window == null || event.getSlotType() != SlotType.CONTAINER) {
            return;
        }
        var customGUI = window.getCustomGUI();
        if (!Objects.equals(customGUI.getTitle(), event.getView().getTitle())) {
            return;
        }
        if (customGUI.isCancelled()) {
            event.setCancelled(true);
        }
        var guiItem = window.getItem(event.getSlot());
        if (guiItem == null || guiItem.toBukkit().getType() == Material.AIR) {
            return;
        }
        if (customGUI.isCancelled() && !guiItem.toBukkit().equals(event.getCurrentItem())) {
            window.close();
            return;
        }
        var clickType = event.getClick();
        if ((clickType == ClickType.LEFT || clickType == ClickType.RIGHT) && guiItem.getClicked() != null) {
            guiItem.onClicked(window, clickType);
            window.getCustomGUI().playSoundEffect(sbPlayer);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryClose(InventoryCloseEvent event) {
        var objectMap = SBPlayer.fromUUID(event.getPlayer().getUniqueId()).getObjectMap();
        if (objectMap.has(UserWindow.KEY_WINDOW)) {
            ((UserWindow) objectMap.get(UserWindow.KEY_WINDOW)).close();
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        var sbPlayer = SBPlayer.fromUUID(event.getPlayer().getUniqueId());
        if (sbPlayer.getObjectMap().get(KEY_INVENTORY, Collections.EMPTY_SET).size() > 0) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onScriptReadStart(ScriptReadStartEvent event) {
        var objectMap = event.getScriptRead().getSBPlayer().getObjectMap();
        var uuids = objectMap.get(KEY_INVENTORY, new HashSet<UUID>());
        uuids.add(event.getUniqueId());
        objectMap.put(KEY_INVENTORY, uuids);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onScriptEndStart(ScriptReadEndEvent event) {
        var objectMap = event.getScriptRead().getSBPlayer().getObjectMap();
        objectMap.get(KEY_INVENTORY, new HashSet<>()).remove(event.getUniqueId());
    }
}