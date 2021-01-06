package com.github.yuttyann.scriptblockplus.item;

import com.github.yuttyann.scriptblockplus.event.RunItemEvent;
import com.github.yuttyann.scriptblockplus.utils.ItemUtils;
import com.github.yuttyann.scriptblockplus.utils.unmodifiable.UnmodifiableItemStack;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permissible;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

/**
 * ScriptBlockPlus ItemAction クラス
 * @author yuttyann44581
 */
public abstract class ItemAction implements Cloneable {

    private static final Set<ItemAction> ITEMS = new HashSet<>();

    private final ItemStack item;

    public ItemAction(@NotNull ItemStack item) {
        this.item = new UnmodifiableItemStack(item);
    }

    public static <T extends ItemAction> void register(@NotNull T itemAction) {
        ITEMS.add(itemAction);
    }

    @NotNull
    public static Set<ItemAction> getItems() {
        return ITEMS;
    }

    @NotNull
    public ItemStack getItem() {
        return item;
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

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof ItemAction)) {
            return false;
        }
        return equals(((ItemAction) obj).item);
    }

    public boolean equals(@Nullable ItemStack item) {
        return item != null && ItemUtils.isItem(this.item, item.getType(), ItemUtils.getName(item));
    }

    @Override
    public int hashCode() {
        return item.hashCode();
    }

    public boolean hasPermission(@NotNull Permissible permissible) {
        return true;
    }

    public static boolean has(@NotNull Permissible permissible, @Nullable ItemStack item, boolean permission) {
        var itemAction = ITEMS.stream().filter(i -> i.equals(item)).findFirst();
        return itemAction.filter(i -> !permission || i.hasPermission(permissible)).isPresent();
    }

    public abstract void slot(@NotNull ChangeSlot changeSlot);

    public abstract void run(@NotNull RunItem runItem);

    public static void callSlot(@NotNull Player player, @Nullable ItemStack item, int newSlot, int oldSlot) {
        var itemAction = ITEMS.stream().filter(i -> i.equals(item)).filter(i -> i.hasPermission(player));
        itemAction.findFirst().ifPresent(i ->  i.clone().slot(new ChangeSlot(player, newSlot, oldSlot)));
    }

    public static boolean callRun(@NotNull Player player, @Nullable ItemStack item, @Nullable Location location, @NotNull Action action) {
        var itemAction = ITEMS.stream().filter(i -> i.equals(item)).filter(i -> i.hasPermission(player)).findFirst();
        if (itemAction.isPresent()) {
            var runItem = new RunItem(player, item, location, action);
            var runItemEvent = new RunItemEvent(runItem);
            Bukkit.getPluginManager().callEvent(runItemEvent);
            if (!runItemEvent.isCancelled()) {
                itemAction.get().clone().run(runItem);
            }
            return true;
        }
        return false;
    }
}