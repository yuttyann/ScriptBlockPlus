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

import com.github.yuttyann.scriptblockplus.script.ScriptKey;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * ScriptBlockPlus TriggerEvent イベントクラス
 * @author yuttyann44581
 */
public class TriggerEvent extends ScriptBlockEvent {

    private final ScriptKey scriptKey;

    private boolean cancelled;

    public TriggerEvent(@NotNull final Player player, @NotNull final Block block, @NotNull final ScriptKey scriptKey) {
        super(player, block);
        this.scriptKey = scriptKey;
    }

    @NotNull
    public ScriptKey getScriptKey() {
        return scriptKey;
    }

    @NotNull
    public Material getMaterial(boolean isMainHand) {
        if (!hasItem(isMainHand)) {
            return Material.AIR;
        }
        return getItem(isMainHand).getType();
    }

    public boolean hasItem(boolean isMainHand) {
        return getItem(isMainHand) != null;
    }

    public boolean isBlockInHand(boolean isMainHand) {
        if (!hasItem(isMainHand)) {
            return false;
        }
        return getItem(isMainHand).getType().isBlock();
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
}