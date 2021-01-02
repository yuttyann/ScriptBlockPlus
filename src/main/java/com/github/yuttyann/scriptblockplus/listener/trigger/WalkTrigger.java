package com.github.yuttyann.scriptblockplus.listener.trigger;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.listener.TriggerListener;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.script.ScriptType;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * ScriptBlockPlus WalkTrigger クラス
 * @author yuttyann44581
 */
public class WalkTrigger extends TriggerListener<PlayerMoveEvent> {

    public WalkTrigger(@NotNull ScriptBlock plugin) {
        super(plugin, ScriptType.WALK);
    }

    @Override
    @Nullable
    @EventHandler(priority = EventPriority.HIGH)
    public Trigger create(@NotNull PlayerMoveEvent event) {
        var sbPlayer = SBPlayer.fromPlayer(event.getPlayer());
        var blockCoords = new BlockCoords(sbPlayer.getLocation()).subtract(0, 1, 0);
        if (blockCoords.equals(sbPlayer.getOldBlockCoords().orElse(null))) {
            return null;
        } else {
            sbPlayer.setOldBlockCoords(blockCoords);
        }
        var location = blockCoords.toLocation();
        return new Trigger(sbPlayer.getPlayer(), location.getBlock(), event);
    }
}