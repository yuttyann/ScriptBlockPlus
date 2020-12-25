package com.github.yuttyann.scriptblockplus.event;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * ScriptBlockPlus ScriptBlockEvent イベントクラス
 * @author yuttyann44581
 */
public abstract class ScriptBlockEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    protected Block block;
    protected Location location;
    protected ItemStack mainHand;
    protected ItemStack offHand;

    public ScriptBlockEvent(@NotNull final Player player) {
        super(player);
    }

    public ScriptBlockEvent(@NotNull final Player player, @NotNull final Block block) {
        super(player);
        this.block = block;
        this.location = block.getLocation();
        this.mainHand = player.getInventory().getItemInMainHand();
        this.offHand = player.getInventory().getItemInOffHand();
    }

    @NotNull
    public final Block getBlock() {
        return block;
    }

    @NotNull
    public final Location getLocation() {
        return location;
    }

    @NotNull
    public ItemStack getItemInMainHand() {
        return mainHand;
    }

    @NotNull
    public ItemStack getItemInOffHand() {
        return offHand;
    }

    public ItemStack getItem(boolean isMainHand) {
        return isMainHand ? getItemInMainHand() : getItemInOffHand();
    }

    @Override
    public abstract boolean isCancelled();

    @Override
    public abstract void setCancelled(boolean cancel);

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