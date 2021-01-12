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
public class ChangeSlot {

    private final Player player;
    private final int newSlot;
    private final int oldSlot;

    public ChangeSlot(@NotNull Player player, int newSlot, int oldSlot) {
        this.player = player;
        this.newSlot = newSlot;
        this.oldSlot = oldSlot;
    }

    @NotNull
    public Player getPlayer() {
        return player;
    }

    @NotNull
    public SBPlayer getSBPlayer() {
        return SBPlayer.fromPlayer(player);
    }

    @NotNull
    public ItemStack getNewItem() {
        return Optional.ofNullable(player.getInventory().getItem(newSlot)).orElse(new ItemStack(Material.AIR));
    }

    @NotNull
    public ItemStack getOldItem() {
        return Optional.ofNullable(player.getInventory().getItem(oldSlot)).orElse(new ItemStack(Material.AIR));
    }

    public int getNewSlot() {
        return newSlot;
    }

    public int getOldSlot() {
        return oldSlot;
    }
}