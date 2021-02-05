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

import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * ScriptBlockPlus RunItem クラス
 * @author yuttyann44581
 */
public class RunItem {

    private final ItemStack item;
    private final Player player;
    private final Action action;
    private final BlockCoords blockCoords;

    public RunItem(@NotNull ItemStack item, @NotNull Player player, @NotNull Action action, @Nullable BlockCoords blockCoords) {
        this.item = item;
        this.player = player;
        this.action = action;
        this.blockCoords = blockCoords;
    }

    @NotNull
    public ItemStack getItem() {
        return item;
    }

    @NotNull
    public Player getPlayer() {
        return player;
    }

    @NotNull
    public Action getAction() {
        return action;
    }

    @Nullable
    public BlockCoords getBlockCoords() {
        return blockCoords;
    }

    public boolean isAIR() {
        return action.name().endsWith("_CLICK_AIR");
    }

    public boolean isSneaking() {
        return player.isSneaking();
    }
}