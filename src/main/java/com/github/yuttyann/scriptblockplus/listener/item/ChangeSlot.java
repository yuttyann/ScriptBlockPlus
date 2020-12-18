package com.github.yuttyann.scriptblockplus.listener.item;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * ScriptBlockPlus ChangeSlot クラス
 * @author yuttyann44581
 */
public class ChangeSlot {

    private final Player player;
    private final ItemStack item;
    private final boolean isNewSlot;

    public ChangeSlot(@NotNull Player player, @Nullable ItemStack item, boolean isNewSlot) {
        this.player = player;
        this.item = item;
        this.isNewSlot = isNewSlot;
    }

    @NotNull
    public Player getPlayer() {
        return player;
    }

    @NotNull
    public ItemStack getItem() {
        return Objects.requireNonNull(item);
    }

    public boolean isNewSlot() {
        return isNewSlot;
    }
}
