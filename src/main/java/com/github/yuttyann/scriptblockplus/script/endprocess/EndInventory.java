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
package com.github.yuttyann.scriptblockplus.script.endprocess;

import com.github.yuttyann.scriptblockplus.script.ScriptRead;
import com.github.yuttyann.scriptblockplus.script.option.other.ItemCost;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * ScriptBlockPlus EndInventory エンドプロセスクラス
 * @author yuttyann44581
 */
public final class EndInventory implements EndProcess {

    private static final ItemStack[] EMPTY_ARRAY = new ItemStack[0];

    @Override
    public void success(@NotNull ScriptRead scriptRead) {
        var sbPlayer = scriptRead.getSBPlayer();
        if (sbPlayer.isOnline()) {
            sbPlayer.toPlayer().updateInventory();
        }
    }

    @Override
    public void failed(@NotNull ScriptRead scriptRead) {
        var sbPlayer = scriptRead.getSBPlayer();
        var inventoryItems = scriptRead.get(ItemCost.KEY_OPTION, EMPTY_ARRAY);
        if (inventoryItems.length > 0 && sbPlayer.isOnline()) {
            sbPlayer.getInventory().setContents(inventoryItems);
            sbPlayer.toPlayer().updateInventory();
        }
    }
}