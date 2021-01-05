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