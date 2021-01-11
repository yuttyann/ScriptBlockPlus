package com.github.yuttyann.scriptblockplus.utils;

import com.github.yuttyann.scriptblockplus.enums.reflection.PackageType;
import com.github.yuttyann.scriptblockplus.file.config.SBConfig;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;

/**
 * ScriptBlockPlus ItemUtils クラス
 * 
 * @author yuttyann44581
 */
public class ItemUtils {

    private static final Map<String, Material> KEY_MATERIALS;

    static {
        if (!Utils.isCBXXXorLater("1.13") && PackageType.HAS_NMS) {
            KEY_MATERIALS = new HashMap<>();
            try {
                KEY_MATERIALS.putAll(PackageType.getItemRegistry());
            } catch (ReflectiveOperationException e) {
                e.printStackTrace();
            }
        } else {
            KEY_MATERIALS = null;
        }
    }

    @NotNull
    public static ItemStack getBlockSelector() {
        var item = new ItemStack(Material.STICK);
        var meta = item.getItemMeta();
        meta.setDisplayName("§dBlock Selector");
        meta.setLore(StringUtils.setListColor(SBConfig.BLOCK_SELECTOR.getValue()));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
        return item;
    }

    @NotNull
    public static ItemStack getScriptEditor() {
        var item = new ItemStack(Material.BLAZE_ROD);
        var meta = item.getItemMeta();
        meta.setDisplayName("§dScript Editor");
        meta.setLore(StringUtils.setListColor(SBConfig.SCRIPT_EDITOR.getValue()));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
        return item;
    }

    @NotNull
    public static ItemStack getScriptViewer() {
        var item = new ItemStack(getMaterial(Utils.isCBXXXorLater("1.13") ? "CLOCK" : "WATCH"));
        var meta = item.getItemMeta();
        meta.setDisplayName("§dScript Viewer");
        meta.setLore(StringUtils.setListColor(SBConfig.SCRIPT_VIEWER.getValue()));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
        return item;
    }

    @NotNull
    public static Material getMaterial(@NotNull String name) {
        if (KEY_MATERIALS != null) {
            var filter = name.toLowerCase(Locale.ROOT);
            filter = filter.startsWith(Utils.MINECRAFT) ? filter : Utils.MINECRAFT + filter;
            if (KEY_MATERIALS.containsKey(filter)) {
                return KEY_MATERIALS.get(filter);
            }
        }
        name = StringUtils.removeStart(name, Utils.MINECRAFT);
        name = name.toUpperCase(Locale.ROOT);
        name = name.replaceAll("\\s+", "_").replaceAll("\\W", "");
        var material = Material.getMaterial(name);
        return material == null ? Material.AIR : material;
    }

    @NotNull
    public static String getKey(@NotNull Material material) {
        if (KEY_MATERIALS == null) {
            return material.getKey().toString();
        }
        var filter = (Predicate<Entry<?, ?>>) (e) -> e.getValue() == material;
        return KEY_MATERIALS.entrySet().stream().filter(filter).findFirst().get().getKey();
    }

    @NotNull
    public static String getName(@NotNull ItemStack item) {
        return getName(item, item.getType().name());
    }

    @NotNull
    public static String getName(@NotNull ItemStack item, @Nullable String def) {
        def = def == null ? item.getType().name() : def;
        if (item.getType() == Material.AIR) {
            return def;
        }
        var meta = item.getItemMeta();
        return meta == null ? def : meta.hasDisplayName() ? meta.getDisplayName() : def;
    }

    @SuppressWarnings("deprecation")
    public static int getDamage(@NotNull ItemStack item) {
        if (Utils.isCBXXXorLater("1.13")) {
            var meta = item.getItemMeta();
            return meta instanceof Damageable ? ((Damageable) meta).getDamage() : 0;
        }
        return item.getDurability();
    }

    public static boolean isItem(@Nullable ItemStack item, @Nullable Material material, @NotNull String name) {
        return isItem(item, material, name::equals);
    }

    public static boolean isItem(@Nullable ItemStack item, @Nullable Material material, @NotNull Predicate<String> filter) {
        return (item == null || material == null) ? false : item.getType() == material && filter.test(getName(item));
    }
}