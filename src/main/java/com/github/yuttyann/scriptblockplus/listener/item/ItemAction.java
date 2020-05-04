package com.github.yuttyann.scriptblockplus.listener.item;

import com.github.yuttyann.scriptblockplus.utils.ItemUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * ScriptBlockPlus ItemAction クラス
 * @author yuttyann44581
 */
public abstract class ItemAction {

    private static final Set<ItemAction> ITEMS = new HashSet<>();

    protected final ItemStack item;

    protected Player player;
    protected Action action;
    protected Location location;
    protected boolean isAIR;
    protected boolean isSneaking;

    public ItemAction(@NotNull ItemStack item) {
        this.item = item;
    }

    @NotNull
    public ItemStack getItem() {
        return item.clone();
    }

    public boolean equals(@NotNull ItemStack item) {
        return ItemUtils.isItem(this.item, item.getType(), ItemUtils.getName(item));
    }

    public void put() {
        ITEMS.add(this);
    }

    public abstract boolean run();

    public static boolean run(@Nullable ItemStack item, @NotNull Player player, @NotNull Action action, @Nullable Location location, boolean isAIR, boolean isSneaking) {
        if (item == null) {
            return false;
        }
        Optional<ItemAction> itemAction = ITEMS.stream().filter(i -> i.equals(item)).findFirst();
        if (itemAction.isPresent()) {
            ItemAction value = itemAction.get();
            value.player = player;
            value.action = action;
            value.location = location;
            value.isAIR = isAIR;
            value.isSneaking = isSneaking;
            return value.run();
        }
        return false;
    }
}