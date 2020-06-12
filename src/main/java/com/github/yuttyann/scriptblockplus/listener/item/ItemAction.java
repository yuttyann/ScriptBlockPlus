package com.github.yuttyann.scriptblockplus.listener.item;

import com.github.yuttyann.scriptblockplus.utils.ItemUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permissible;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * ScriptBlockPlus ItemAction クラス
 * @author yuttyann44581
 */
public abstract class ItemAction implements Cloneable {

    private static final Set<ItemAction> ITEMS = new HashSet<>();

    protected final ItemStack item;

    protected Player player;
    protected Action action;
    protected Location location;
    protected boolean isAIR;
    protected boolean isSneaking;

    public ItemAction(@NotNull ItemStack item) {
        this.item = new UnmodifiableItemStack(item);
    }

    @NotNull
    public static Set<ItemAction> getItems() {
        return ITEMS;
    }

    public final void put() {
        ITEMS.add(this);
    }

    @NotNull
    public ItemStack getItem() {
        return item;
    }

    public boolean hasPermission(@NotNull Permissible permissible) {
        return true;
    }

    public boolean equals(@Nullable ItemStack item) {
        return item != null && ItemUtils.isItem(this.item, item.getType(), ItemUtils.getName(item));
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof ItemAction)) {
            return false;
        }
        return equals(((ItemAction) obj).item);
    }

    @Override
    public int hashCode() {
        return item.hashCode();
    }

    @Override
    @NotNull
    public ItemAction clone() {
        try {
            return (ItemAction) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new Error(e);
        }
    }

    public abstract boolean run();

    public static synchronized boolean run(@Nullable ItemStack item, @NotNull Player player, @NotNull Action action, @Nullable Location location, boolean isAIR, boolean isSneaking) {
        if (item == null) {
            return false;
        }
        Optional<ItemAction> itemAction = ITEMS.stream().filter(i -> i.equals(item)).findFirst();
        if (itemAction.isPresent()) {
            ItemAction value = itemAction.get().clone();
            value.player = player;
            value.action = action;
            value.location = location;
            value.isAIR = isAIR;
            value.isSneaking = isSneaking;
            return value.run();
        }
        return false;
    }

    public static boolean has(@NotNull Permissible permissible, @Nullable ItemStack item, boolean permission) {
        Optional<ItemAction> itemAction = ITEMS.stream().filter(i -> i.equals(item)).findFirst();
        return itemAction.filter(i -> !permission || i.hasPermission(permissible)).isPresent();
    }
}