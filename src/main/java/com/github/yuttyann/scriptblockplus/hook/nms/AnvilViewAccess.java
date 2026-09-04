package com.github.yuttyann.scriptblockplus.hook.nms;

import static com.github.yuttyann.scriptblockplus.utils.reflect.Reflection.method;

import java.lang.reflect.Method;

import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** InventoryView のクラス／インターフェース変更と金床 API の世代差を吸収します。 */
final class AnvilViewAccess {

    private static final Method GET_TOP_INVENTORY = method(InventoryView.class)
        .name("getTopInventory")
        .returnType(Inventory.class)
        .emptyParameterTypes()
        .findFirst();
    private static final Method SET_VIEW_REPAIR_COST = findViewRepairCost();
    private static final Method SET_INVENTORY_REPAIR_COST = method(AnvilInventory.class)
        .name("setRepairCost")
        .returnType(void.class)
        .parameterTypes(int.class)
        .findFirstOrNull();

    private AnvilViewAccess() { }

    @NotNull
    static Inventory getTopInventory(@NotNull InventoryView view) throws ReflectiveOperationException {
        return (Inventory) GET_TOP_INVENTORY.invoke(view);
    }

    static boolean supportsRepairCost() {
        return SET_VIEW_REPAIR_COST != null || SET_INVENTORY_REPAIR_COST != null;
    }

    static void setRepairCost(@NotNull InventoryEvent event, int cost) throws ReflectiveOperationException {
        if (SET_VIEW_REPAIR_COST != null) {
            SET_VIEW_REPAIR_COST.invoke(event.getView(), cost);
        } else if (SET_INVENTORY_REPAIR_COST != null) {
            SET_INVENTORY_REPAIR_COST.invoke(event.getInventory(), cost);
        }
    }

    @Nullable
    private static Method findViewRepairCost() {
        try {
            var viewType = Class.forName("org.bukkit.inventory.view.AnvilView", false, InventoryView.class.getClassLoader());
            return method(viewType)
                .name("setRepairCost")
                .returnType(void.class)
                .parameterTypes(int.class)
                .findFirstOrNull();
        } catch (ClassNotFoundException ex) {
            return null;
        }
    }
}
