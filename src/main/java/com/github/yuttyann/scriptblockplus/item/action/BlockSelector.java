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
package com.github.yuttyann.scriptblockplus.item.action;

import com.github.yuttyann.scriptblockplus.enums.Permission;
import com.github.yuttyann.scriptblockplus.file.config.SBConfig;
import com.github.yuttyann.scriptblockplus.item.ItemAction;
import com.github.yuttyann.scriptblockplus.item.RunItem;

import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.permissions.Permissible;
import org.jetbrains.annotations.NotNull;

import static com.github.yuttyann.scriptblockplus.BlockCoords.*;

/**
 * ScriptBlockPlus BlockSelector クラス
 * @author yuttyann44581
 */
public final class BlockSelector extends ItemAction {

    public BlockSelector() {
        super(Material.STICK, () -> "§dBlock Selector", SBConfig.BLOCK_SELECTOR::setListColor);
        setItemFlags(ItemFlag.HIDE_ATTRIBUTES);
    }

    @Override
    public void run(@NotNull RunItem runItem) {
        var sbPlayer = runItem.getSBPlayer();
        var blockCoords = runItem.isSneaking() ? of(sbPlayer.getLocation()) : runItem.isAIR() ? null : copy(runItem.getBlockCoords());
        if (blockCoords == null) {
            return;
        }
        var region = sbPlayer.getCuboidRegion();
        switch (runItem.getAction()) {
            case LEFT_CLICK_AIR: case LEFT_CLICK_BLOCK:
                region.setPosition1(blockCoords);
                SBConfig.SELECTOR_POS1.replace(region.getName(), blockCoords.getCoords()).send(sbPlayer);
                break;
            case RIGHT_CLICK_AIR: case RIGHT_CLICK_BLOCK:
                region.setPosition2(blockCoords);
                SBConfig.SELECTOR_POS2.replace(region.getName(), blockCoords.getCoords()).send(sbPlayer);
                break;
            default:
        }
    }

    @Override
    public boolean hasPermission(@NotNull Permissible permissible) {
        return Permission.TOOL_BLOCK_SELECTOR.has(permissible);
    }
}