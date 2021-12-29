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

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.item.RunItem;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * ScriptBlockPlus RunItemEvent イベントクラス
 * @author yuttyann44581
 */
public class RunItemEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private final RunItem runItem;

    private boolean cancelled;

    /**
     * コンストラクタ
     * @param runItem - アイテム
     */
    public RunItemEvent(@NotNull RunItem runItem) {
        super(runItem.getSBPlayer().getPlayer());
        this.runItem = runItem;
    }

    /**
     * アイテムを取得します。
     * @return {@link ItemStack} - アイテム
     */
    @NotNull
    public ItemStack getItem() {
        return runItem.getItem();
    }

    /**
     * アクションを取得します。
     * @return {@link Action} - アクション
     */
    @NotNull
    public Action getAction() {
        return runItem.getAction();
    }

    /**
     * 座標を取得します。
     * @return {@link BlockCoords} - 座標
     */
    @Nullable
    public BlockCoords getBlockCoords() {
        return runItem.getBlockCoords();
    }

    /**
     * 空気関連のアクションの場合は{@code true}を返します。
     * @return {@code boolean} - 空気関連のアクションの場合は{@code true}
     */
    public boolean isAIR() {
        return runItem.isAIR();
    }

    /**
     * プレイヤーがスニーク状態の場合は{@code true}を返します。
     * @return {@code boolean} - プレイヤーがスニーク状態の場合は{@code true}
     */
    public boolean isSneaking() {
        return runItem.isSneaking();
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