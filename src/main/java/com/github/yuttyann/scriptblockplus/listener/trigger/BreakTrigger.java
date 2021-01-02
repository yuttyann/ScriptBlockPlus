package com.github.yuttyann.scriptblockplus.listener.trigger;

import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.event.ScriptBlockBreakEvent;
import com.github.yuttyann.scriptblockplus.event.TriggerEvent;
import com.github.yuttyann.scriptblockplus.listener.TriggerListener;
import com.github.yuttyann.scriptblockplus.listener.item.ItemAction;
import com.github.yuttyann.scriptblockplus.script.ScriptType;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * ScriptBlockPlus BreakTrigger クラス
 * @author yuttyann44581
 */
public class BreakTrigger extends TriggerListener<BlockBreakEvent> {

    public BreakTrigger(@NotNull ScriptBlock plugin) {
        super(plugin, ScriptType.BREAK);
    }

    @Override
    @Nullable
    @EventHandler(priority = EventPriority.HIGH)
    public Trigger createTrigger(@NotNull BlockBreakEvent event) {
        var player = event.getPlayer();
        if (ItemAction.has(player, player.getInventory().getItemInMainHand(), true)) {
            event.setCancelled(true);
        }
        return event.isCancelled() ? null : new Trigger(player, event.getBlock(), event);
    }

    @Override
    @Nullable
    public TriggerEvent getTriggerEvent(@NotNull Trigger trigger) {
        return new ScriptBlockBreakEvent(trigger.getPlayer(), trigger.getBlock());
    }
}