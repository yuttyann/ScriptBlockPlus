package com.github.yuttyann.scriptblockplus.item;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * ScriptBlockPlus RunItem クラス
 * @author yuttyann44581
 */
public class RunItem {

    private final Player player;
    private final ItemStack item;
    private final Location location;
    private final Action action;
    private final boolean isAIR;
    private final boolean isSneaking;

    public RunItem(@NotNull Player player, @NotNull ItemStack item, @Nullable Location location, @NotNull Action action) {
        this.player = player;
        this.item = item;
        this.location = location;
        this.action = action;
        this.isAIR = action.name().endsWith("_CLICK_AIR");
        this.isSneaking = player.isSneaking();
    }

    @NotNull
    public Player getPlayer() {
        return player;
    }

    @NotNull
    public ItemStack getItem() {
        return item;
    }

    @Nullable
    public Location getLocation() {
        return location;
    }

    @NotNull
    public Action getAction() {
        return action;
    }

    public boolean isAIR() {
        return isAIR;
    }

    public boolean isSneaking() {
        return isSneaking;
    }
}