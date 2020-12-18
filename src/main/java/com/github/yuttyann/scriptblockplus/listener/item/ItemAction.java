package com.github.yuttyann.scriptblockplus.listener.item;

import com.github.yuttyann.scriptblockplus.utils.ItemUtils;
import com.github.yuttyann.scriptblockplus.utils.unmodifiable.UnmodifiableItemStack;
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
import java.util.stream.Stream;

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

    private boolean equals(@Nullable ItemStack item) {
        return item != null && ItemUtils.isItem(this.item, item.getType(), ItemUtils.getName(item));
    }

    @Override
    public int hashCode() {
        return item.hashCode();
    }

    public boolean hasPermission(@NotNull Permissible permissible) {
        return true;
    }

    public void slot(@NotNull ChangeSlot changeSlot) {
        // アイテムスロットを移動した際に呼ばれる
    }

    public abstract boolean run(@NotNull RunItem runItem);

    public static boolean has(@NotNull Permissible permissible, @Nullable ItemStack item, boolean permission) {
        Optional<ItemAction> itemAction = ITEMS.stream().filter(i -> i.equals(item)).findFirst();
        return itemAction.filter(i -> !permission || i.hasPermission(permissible)).isPresent();
    }

    public static void callSlot(@NotNull Player player, @Nullable ItemStack item, boolean isNewSlot) {
        Stream<ItemAction> itemAction = ITEMS.stream().filter(i -> i.equals(item)).filter(i -> i.hasPermission(player));
        itemAction.findFirst().ifPresent(i ->  i.clone().slot(new ChangeSlot(player, item, isNewSlot)));
    }

    public static boolean callRun(@NotNull Player player, @Nullable ItemStack item, @Nullable Location location, @NotNull Action action) {
        Stream<ItemAction> itemAction = ITEMS.stream().filter(i -> i.equals(item)).filter(i -> i.hasPermission(player));
        return itemAction.findFirst().map(i -> i.clone().run(new RunItem(player, item, location, action))).orElse(false);
    }
}