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
package com.github.yuttyann.scriptblockplus.event;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * ScriptBlockPlus BlockClickEvent イベントクラス
 * @author yuttyann44581
 */
public class BlockClickEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private Block block;
    private ItemStack item;
    private Action action;
    private BlockFace blockFace;
    private EquipmentSlot hand;
    private boolean isAnimation;
    private boolean isInvalid;
    private boolean cancelled;

    public BlockClickEvent(@NotNull final PlayerInteractEvent event, final boolean isAnimation) {
        super(event.getPlayer());
        this.block = event.getClickedBlock();
        this.item = event.getItem();
        this.action = event.getAction();
        this.blockFace = event.getBlockFace();
        this.hand = event.getHand();
        this.isAnimation = isAnimation;
    }

    @Nullable
    public Block getBlock() {
        return block;
    }

    @Nullable
    public Location getLocation() {
        return block == null ? null : block.getLocation();
    }

    @Nullable
    public ItemStack getItem() {
        return item;
    }

    @NotNull
    public Material getMaterial() {
        if (!hasItem()) {
            return Material.AIR;
        }
        return item.getType();
    }

    @NotNull
    public Action getAction() {
        return action;
    }

    @NotNull
    public BlockFace getBlockFace() {
        return blockFace;
    }

    @NotNull
    public EquipmentSlot getHand() {
        return hand;
    }

    public boolean hasItem() {
        return item != null;
    }

    public boolean isBlockInHand() {
        if (!hasItem()) {
            return false;
        }
        return item.getType().isBlock();
    }

    public boolean isAnimation() {
        return isAnimation;
    }

    public boolean isInvalid() {
        return isInvalid;
    }

    public void setInvalid(boolean isInvalid) {
        this.isInvalid = isInvalid;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}