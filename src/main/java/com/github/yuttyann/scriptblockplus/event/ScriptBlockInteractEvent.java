package com.github.yuttyann.scriptblockplus.event;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.jetbrains.annotations.NotNull;

/**
 * ScriptBlockPlus ScriptBlockInteractEvent イベントクラス
 * @author yuttyann44581
 */
public class ScriptBlockInteractEvent extends TriggerEvent {

    private boolean isLeftClick;

    public ScriptBlockInteractEvent(@NotNull final Player player, @NotNull final Block block, @NotNull final Action action) {
        super(player, block);
        this.isLeftClick = action == Action.LEFT_CLICK_BLOCK;
    }

    public boolean isLeftClick() {
        return isLeftClick;
    }

    public boolean isRightClick() {
        return !isLeftClick;
    }
}