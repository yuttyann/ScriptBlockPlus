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
package com.github.yuttyann.scriptblockplus.listener.trigger;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.event.BlockClickEvent;
import com.github.yuttyann.scriptblockplus.file.config.SBConfig;
import com.github.yuttyann.scriptblockplus.listener.TriggerListener;
import com.github.yuttyann.scriptblockplus.script.ScriptKey;
import com.github.yuttyann.scriptblockplus.script.option.other.PlayerAction;

import org.bukkit.block.Block;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.material.Openable;
import org.bukkit.material.Redstone;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * ScriptBlockPlus InteractTrigger クラス
 * @author yuttyann44581
 */
public final class InteractTrigger extends TriggerListener<BlockClickEvent> {

    public InteractTrigger(@NotNull ScriptBlock plugin) {
        super(plugin, ScriptKey.INTERACT, EventPriority.HIGH);
    }

    @Override
    @Nullable
    protected Trigger create(@NotNull BlockClickEvent event) {
        var block = event.getBlock();
        if (event.isInvalid() || event.getHand() != EquipmentSlot.HAND || block == null) {
            return null;
        }
        if (isPowered(block) || isOpen(block) || isDisableArm(event.getAction())) {
            return null;
        }
        return new Trigger(event, event.getPlayer(), BlockCoords.of(block));
    }

    @Override
    @NotNull
    protected Result handle(@NotNull Trigger trigger) {
        switch (trigger.getProgress()) {
            case READ:
                trigger.getTempMap().get().put(PlayerAction.KEY, trigger.getEvent().getAction());
                return Result.SUCCESS;
            default:
                return super.handle(trigger);
        }
    }

    private boolean isDisableArm(@NotNull Action action) {
        switch (action) {
            case LEFT_CLICK_BLOCK:
                return !SBConfig.ACTIONS_INTERACT_LEFT.getValue();
            case RIGHT_CLICK_BLOCK:
                return !SBConfig.ACTIONS_INTERACT_RIGHT.getValue();
            default:
                return false;
        }
    }

    private boolean isPowered(Block block) {
        var data = block.getState().getData();
        return data instanceof Redstone && ((Redstone) data).isPowered();
    }

    private boolean isOpen(Block block) {
        var data = block.getState().getData();
        return data instanceof Openable && ((Openable) data).isOpen();
    }
}