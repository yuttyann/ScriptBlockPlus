package com.github.yuttyann.scriptblockplus.item;

import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * ScriptBlockPlus ChangeSlot クラス
 * @author yuttyann44581
 */
public class ChangeSlot {

    private final Player player;
    private final int newSlot;
    private final int oldSlot;

    public ChangeSlot(@NotNull Player player, int newSlot, int oldSlot) {
        this.player = player;
        this.newSlot = newSlot;
        this.oldSlot = oldSlot;
    }

    @NotNull
    public Player getPlayer() {
        return player;
    }

    @NotNull
    public SBPlayer getSBPlayer() {
        return SBPlayer.fromPlayer(player);
    }

    @NotNull
    public ItemStack getNewItem() {
        return Optional.ofNullable(player.getInventory().getItem(newSlot)).orElse(new ItemStack(Material.AIR));
    }

    @NotNull
    public ItemStack getOldItem() {
        return Optional.ofNullable(player.getInventory().getItem(oldSlot)).orElse(new ItemStack(Material.AIR));
    }

    public int getNewSlot() {
        return newSlot;
    }

    public int getOldSlot() {
        return oldSlot;
    }
}