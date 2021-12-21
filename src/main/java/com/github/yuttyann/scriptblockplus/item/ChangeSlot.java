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

import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * ScriptBlockPlus ChangeSlot クラス
 * @author yuttyann44581
 */
public final class ChangeSlot {

    private final SBPlayer sbPlayer;
    private final int newSlot, oldSlot;

    /**
     * コンストラクタ
     * @param sbPlayer - プレイヤー
     * @param newSlot - 移動先のスロット
     * @param oldSlot - 移動前のスロット
     */
    public ChangeSlot(@NotNull SBPlayer sbPlayer, int newSlot, int oldSlot) {
        this.sbPlayer = sbPlayer;
        this.newSlot = newSlot;
        this.oldSlot = oldSlot;
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

    @NotNull
    public ItemStack getNewItem() {
        return Optional.ofNullable(sbPlayer.getInventory().getItem(newSlot)).orElse(new ItemStack(Material.AIR));
    }

    @NotNull
    public ItemStack getOldItem() {
        return Optional.ofNullable(sbPlayer.getInventory().getItem(oldSlot)).orElse(new ItemStack(Material.AIR));
    }

    public int getNewSlot() {
        return newSlot;
    }

    public int getOldSlot() {
        return oldSlot;
    }
}