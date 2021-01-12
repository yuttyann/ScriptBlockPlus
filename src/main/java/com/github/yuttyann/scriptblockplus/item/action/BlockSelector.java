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

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.enums.Permission;
import com.github.yuttyann.scriptblockplus.file.config.SBConfig;
import com.github.yuttyann.scriptblockplus.item.ChangeSlot;
import com.github.yuttyann.scriptblockplus.item.ItemAction;
import com.github.yuttyann.scriptblockplus.item.RunItem;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.region.CuboidRegion;
import com.github.yuttyann.scriptblockplus.utils.ItemUtils;
import org.bukkit.permissions.Permissible;
import org.jetbrains.annotations.NotNull;

/**
 * ScriptBlockPlus BlockSelector クラス
 * @author yuttyann44581
 */
public class BlockSelector extends ItemAction {

    public BlockSelector() {
        super(ItemUtils.getBlockSelector());
    }

    @Override
    public boolean hasPermission(@NotNull Permissible permissible) {
        return Permission.TOOL_BLOCK_SELECTOR.has(permissible);
    }

    @Override
    public void slot(@NotNull ChangeSlot changeSlot) {

    }

    @Override
    public void run(@NotNull RunItem runItem) {
        var location = runItem.getLocation();
        var sbPlayer = SBPlayer.fromPlayer(runItem.getPlayer());
        var region = ((CuboidRegion) sbPlayer.getRegion());
        switch (runItem.getAction()) {
            case LEFT_CLICK_AIR:
            case LEFT_CLICK_BLOCK:
                if (runItem.isSneaking()) {
                    region.setVector1((location = sbPlayer.getLocation()).toVector());
                } else if (!runItem.isAIR() && location != null) {
                    region.setVector1(location.toVector());
                }
                if (location != null) {
                    region.setWorld(location.getWorld());
                    SBConfig.SELECTOR_POS1.replace(region.getName(), BlockCoords.getCoords(location)).send(sbPlayer);
                }
                break;
            case RIGHT_CLICK_AIR:
            case RIGHT_CLICK_BLOCK:
                if (runItem.isSneaking()) {
                    region.setVector2((location = sbPlayer.getLocation()).toVector());
                } else if (!runItem.isAIR() && location != null) {
                    region.setVector2(location.toVector());
                }
                if (location != null) {
                    region.setWorld(location.getWorld());
                    SBConfig.SELECTOR_POS2.replace(region.getName(), BlockCoords.getCoords(location)).send(sbPlayer);
                }
                break;
            default:
        }
    }
}