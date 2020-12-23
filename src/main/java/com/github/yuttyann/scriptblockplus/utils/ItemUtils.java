package com.github.yuttyann.scriptblockplus.utils;

import com.github.yuttyann.scriptblockplus.file.config.SBConfig;
import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * ScriptBlockPlus ItemUtils クラス
 * @author yuttyann44581
 */
public class ItemUtils {

    @SuppressWarnings("deprecation")
    public static int getDamage(@NotNull ItemStack item) {
        Validate.notNull(item, "Item cannot be null");
        if (Utils.isCBXXXorLater("1.13")) {
            ItemMeta meta = item.getItemMeta();
            return meta == null ? 0 : ((org.bukkit.inventory.meta.Damageable) meta).getDamage();
        }
        return item.getDurability();
    }

    @NotNull
    public static Material getMaterial(@NotNull String name) {
        Material type = Material.getMaterial(name.toUpperCase());
        return type == null ? Material.AIR : type;
    }

    @NotNull
    public static ItemStack getBlockSelector() {
        ItemStack item = new ItemStack(Material.STICK);
        ItemMeta meta = Objects.requireNonNull(item.getItemMeta());
        meta.setDisplayName("§dBlock Selector");
        meta.setLore(StringUtils.setListColor(SBConfig.BLOCK_SELECTOR.getValue()));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
        return item;
    }

    @NotNull
    public static ItemStack getScriptEditor() {
        ItemStack item = new ItemStack(Material.BLAZE_ROD);
        ItemMeta meta = Objects.requireNonNull(item.getItemMeta());
        meta.setDisplayName("§dScript Editor");
        meta.setLore(StringUtils.setListColor(SBConfig.SCRIPT_EDITOR.getValue()));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
        return item;
    }

    @NotNull
    public static ItemStack getScriptViewer() {
        ItemStack item = new ItemStack(Material.valueOf(Utils.isCBXXXorLater("1.13") ? "CLOCK" : "WATCH"));
        ItemMeta meta = Objects.requireNonNull(item.getItemMeta());
        meta.setDisplayName("§dScript Viewer");
        meta.setLore(StringUtils.setListColor(SBConfig.SCRIPT_VIEWER.getValue()));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
        return item;
    }

    @NotNull
    public static ItemStack[] getHandItems(Player player) {
        PlayerInventory inventory = player.getInventory();
        return new ItemStack[] { inventory.getItemInMainHand(), inventory.getItemInOffHand() };
    }

    @NotNull
    public static String getName(@NotNull ItemStack item, @Nullable String def) {
        def = def == null ? item.getType().name() : def;
        if (item.getType() == Material.AIR) {
            return def;
        }
        ItemMeta meta = item.getItemMeta();
        return meta == null ? def : meta.hasDisplayName() ? meta.getDisplayName() : def;
    }

    @NotNull
    public static String getName(@NotNull ItemStack item) {
        return getName(item, item.getType().name());
    }

    public static boolean isItem(@Nullable ItemStack item, @Nullable Material type, @NotNull String name) {
        return isItem(item, type, name::equals);
    }

    public static boolean isItem(@Nullable ItemStack item, @Nullable Material type, @NotNull Predicate<String> name) {
        return item != null && type != null && item.getType() == type && name.test(getName(item));
    }
}