package com.github.yuttyann.scriptblockplus.listener.trigger;

import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.listener.TriggerListener;
import com.github.yuttyann.scriptblockplus.item.ItemAction;
import com.github.yuttyann.scriptblockplus.script.ScriptKey;

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
        super(plugin, ScriptKey.BREAK, EventPriority.HIGH);
    }

    @Override
    @Nullable
    public Trigger create(@NotNull BlockBreakEvent event) {
        var player = event.getPlayer();
        if (ItemAction.has(player, player.getInventory().getItemInMainHand(), true)) {
            event.setCancelled(true);
        }
        return event.isCancelled() ? null : new Trigger(player, event.getBlock(), event);
    }
}