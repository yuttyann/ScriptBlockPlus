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
package com.github.yuttyann.scriptblockplus.item;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;

import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * ScriptBlockPlus RunItem クラス
 * @author yuttyann44581
 */
public final class RunItem {

    private final ItemStack item;
    private final SBPlayer sbPlayer;
    private final Action action;
    private final BlockCoords blockCoords;

    /**
     * コンストラクタ
     * @param item - アイテム
     * @param sbPlayer - プレイヤー
     * @param action - アクション
     * @param blockCoords - 座標
     */
    public RunItem(@NotNull ItemStack item, @NotNull SBPlayer sbPlayer, @NotNull Action action, @Nullable BlockCoords blockCoords) {
        this.item = item;
        this.sbPlayer = sbPlayer;
        this.action = action;
        this.blockCoords = blockCoords;
    }

    /**
     * アイテムを取得します。
     * @return {@link ItemStack} - アイテム
     */
    @NotNull
    public ItemStack getItem() {
        return item;
    }

    /**
     * {@code BukkitAPI}の{@code org.bukkit.entity.Player}を取得します。
     * @return {@link Player} - プレイヤー
     */
    @Deprecated
    @NotNull
    public Player getPlayer() {
        return sbPlayer.getPlayer();
    }

    /**
     * プレイヤーを取得します。
     * @return {@link SBPlayer} - プレイヤー
     */
    @NotNull
    public SBPlayer getSBPlayer() {
        return sbPlayer;
    }

    /**
     * アクションを取得します。
     * @return {@link Player} - アクション
     */
    @NotNull
    public Action getAction() {
        return action;
    }

    /**
     * 座標を取得します。
     * @return {@link BlockCoords} - 座標
     */
    @Nullable
    public BlockCoords getBlockCoords() {
        return blockCoords;
    }

    /**
     * 空気関連のアクションの場合は{@code true}を返します。
     * @return {@code boolean} - 空気関連のアクションの場合は{@code true}
     */
    public boolean isAIR() {
        return action.name().endsWith("_CLICK_AIR");
    }

    /**
     * プレイヤーがスニーク状態の場合は{@code true}を返します。
     * @return {@code boolean} - プレイヤーがスニーク状態の場合は{@code true}
     */
    public boolean isSneaking() {
        return sbPlayer.isSneaking();
    }
}