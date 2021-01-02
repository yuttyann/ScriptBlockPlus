package com.github.yuttyann.scriptblockplus.listener.trigger;

import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.event.BlockClickEvent;
import com.github.yuttyann.scriptblockplus.file.config.SBConfig;
import com.github.yuttyann.scriptblockplus.listener.TriggerListener;
import com.github.yuttyann.scriptblockplus.script.ScriptType;
import com.github.yuttyann.scriptblockplus.script.option.other.ScriptAction;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
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
public class InteractTrigger extends TriggerListener<BlockClickEvent> {

    public InteractTrigger(@NotNull ScriptBlock plugin) {
        super(plugin, ScriptType.INTERACT);
    }

    @Override
    @Nullable
    @EventHandler(priority = EventPriority.HIGH)
    protected Trigger create(@NotNull BlockClickEvent event) {
        var block = event.getBlock();
        if (event.isInvalid() || event.getHand() != EquipmentSlot.HAND || block == null) {
            return null;
        }
        return new Trigger(event.getPlayer(), block, event);
    }

    @Override
    @NotNull
    protected Result interrupt(@NotNull Trigger trigger) {
        switch (trigger.getProgress()) {
            case EVENT:
                var block = trigger.getBlock();
                var action = trigger.getEvent().getAction();
                return isPowered(block) || isOpen(block) || isDisableArm(action) ? Result.FAILURE : Result.SUCCESS;
            case READ:
                trigger.getSBRead().ifPresent(s -> s.put(ScriptAction.KEY, trigger.getEvent().getAction()));
                return Result.SUCCESS;
            default:
                return super.interrupt(trigger);
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