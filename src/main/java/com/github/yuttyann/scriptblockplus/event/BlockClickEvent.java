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
    private boolean cancelled;

    /**
     * コンストラクタ
     * @param event - イベント
     * @param isAnimation - アニメーションイベント経由の場合は{@code true}
     */
    public BlockClickEvent(@NotNull final PlayerInteractEvent event, final boolean isAnimation) {
        super(event.getPlayer());
        this.block = event.getClickedBlock();
        this.item = event.getItem();
        this.action = event.getAction();
        this.blockFace = event.getBlockFace();
        this.hand = event.getHand();
        this.isAnimation = isAnimation;
    }

    /**
     * ブロックを取得します。
     * @return {@link Block} - ブロック
     */
    @Nullable
    public Block getBlock() {
        return block;
    }

    /**
     * ブロックの座標を取得します。
     * @return {@link Location} - ブロックの座標
     */
    @Nullable
    public Location getLocation() {
        return block == null ? null : block.getLocation();
    }

    /**
     * アイテムを取得します。
     * @return {@link ItemStack} - アイテム
     */
    @Nullable
    public ItemStack getItem() {
        return item;
    }

    /**
     * アイテムの種類を取得します。
     * @return {@link Material} - アイテムの種類
     */
    @NotNull
    public Material getMaterial() {
        if (!hasItem()) {
            return Material.AIR;
        }
        return item.getType();
    }

    /**
     * アクションを取得します。
     * @return {@link Action} - アクション
     */
    @NotNull
    public Action getAction() {
        return action;
    }

    /**
     * ブロックの側面を取得します。
     * @return {@link BlockFace} - ブロックの側面を取得します。
     */
    @NotNull
    public BlockFace getBlockFace() {
        return blockFace;
    }

    /**
     * プレイヤーの部位を取得します。
     * @return {@link EquipmentSlot} - プレイヤーの部位
     */
    @NotNull
    public EquipmentSlot getHand() {
        return hand;
    }

    /**
     * アイテムを持っている場合は{@code true}を返します。
     * @return {@code boolean} - アイテムを持っている場合は{@code true}
     */
    public boolean hasItem() {
        return item != null;
    }

    /**
     * ブロックを持っている場合は{@code true}を返します。
     * @return {@code boolean} - ブロックを持っている場合は{@code true}
     */
    public boolean isBlockInHand() {
        if (!hasItem()) {
            return false;
        }
        return item.getType().isBlock();
    }

    /**
     * アニメーションイベント経由の場合は{@code true}を返します。
     * @return {@code boolean} - アニメーションイベント経由の場合は{@code true}
     */
    public boolean isAnimation() {
        return isAnimation;
    }

    /**
     * このイベントのキャンセルする場合は{@code true}を返します。
     * @return {@code boolean} - イベントのキャンセルする場合は{@code true}
     */
    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * このイベントをキャンセルするのかを設定します。
     * @param cancel - イベントのキャンセルする場合は{@code true}
     */
    @Override
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }

    @Override
    @NotNull
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}