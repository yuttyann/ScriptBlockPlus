package com.github.yuttyann.scriptblockplus.event;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ScriptBlockHitEvent extends TriggerEvent {

    public ScriptBlockHitEvent(@NotNull final Player player, @NotNull final Block block) {
        super(player, block);
    }
}