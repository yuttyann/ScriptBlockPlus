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
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * ScriptBlockPlus ScriptBlockEvent イベントクラス
 * @author yuttyann44581
 */
public abstract class ScriptBlockEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    protected Block block;
    protected Location location;
    protected ItemStack mainHand;
    protected ItemStack offHand;

    public ScriptBlockEvent(@NotNull final Player player) {
        super(player);
    }

    public ScriptBlockEvent(@NotNull final Player player, @NotNull final Block block) {
        super(player);
        this.block = block;
        this.location = block.getLocation();
        this.mainHand = player.getInventory().getItemInMainHand();
        this.offHand = player.getInventory().getItemInOffHand();
    }

    /**
     * ブロックを取得します。
     * @return {@link Block} - ブロック
     */
    @NotNull
    public final Block getBlock() {
        return block;
    }

    /**
     * ブロックの座標を取得します。
     * @return {@link Location} - ブロックの座標
     */
    @NotNull
    public final Location getLocation() {
        return location;
    }

    /**
    * メインハンドのアイテムを取得します。
    * @return {@link ItemStack} - アイテム
    */
    @NotNull
    public ItemStack getItemInMainHand() {
        return mainHand;
    }

    /**
     * オフハンドのアイテムを取得します。
     * @return {@link ItemStack} - アイテム
     */
    @NotNull
    public ItemStack getItemInOffHand() {
        return offHand;
    }

    /**
     * アイテムを取得します。
     * @param isMainHand - メインハンドの場合は{@code true}
     * @return {@link ItemStack} - アイテム
     */
    public ItemStack getItem(boolean isMainHand) {
        return isMainHand ? getItemInMainHand() : getItemInOffHand();
    }

    /**
     * このイベントのキャンセルする場合は{@code true}を返します。
     * @return {@code boolean} - イベントのキャンセルする場合は{@code true}
     */
    @Override
    public abstract boolean isCancelled();

    /**
     * このイベントをキャンセルするのかを設定します。
     * @param cancel - イベントのキャンセルする場合は{@code true}
     */
    @Override
    public abstract void setCancelled(boolean cancel);

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