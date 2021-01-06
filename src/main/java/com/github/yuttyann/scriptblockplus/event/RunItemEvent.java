package com.github.yuttyann.scriptblockplus.event;

import com.github.yuttyann.scriptblockplus.item.RunItem;
import org.bukkit.Location;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RunItemEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private final RunItem runItem;

    private boolean cancelled;

    public RunItemEvent(@NotNull RunItem runItem) {
        super(runItem.getPlayer());
        this.runItem = runItem;
    }

    @NotNull
    public ItemStack getItem() {
        return runItem.getItem();
    }

    @Nullable
    public Location getLocation() {
        return runItem.getLocation();
    }

    @NotNull
    public Action getAction() {
        return runItem.getAction();
    }

    public boolean isAIR() {
        return runItem.isAIR();
    }

    public boolean isSneaking() {
        return runItem.isSneaking();
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }

    @Override
    @NotNull
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}