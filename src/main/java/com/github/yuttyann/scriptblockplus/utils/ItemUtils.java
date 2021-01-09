package com.github.yuttyann.scriptblockplus.utils;

import com.github.yuttyann.scriptblockplus.file.config.SBConfig;
import org.bukkit.Material;
import org.bukkit.entity.Player;
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

    public static final String MINECRAFT = "minecraft:";

    private static final boolean HAS_NAMESPACED_KEY;
    private static final Map<String, Material> KEY_NAMES;

    static {    
        HAS_NAMESPACED_KEY = StreamUtils.anyMatch(Material.class.getMethods(), s -> s.getName().equals("getKey"));
        if (HAS_NAMESPACED_KEY) {
            KEY_NAMES = null;
        } else {
            KEY_NAMES = new HashMap<>();
            StreamUtils.forEach(Material.values(), m -> KEY_NAMES.put(MINECRAFT + m.name().toLowerCase(Locale.ROOT), m));
        }
    }

    @SuppressWarnings("deprecation")
    public static int getDamage(@NotNull ItemStack item) {
        if (Utils.isCBXXXorLater("1.13")) {
            var meta = item.getItemMeta();
            return meta == null ? 0 : ((Damageable) meta).getDamage();
        }
        return item.getDurability();
    }

    @NotNull
    public static String getKey(@NotNull Material material) {
        if (HAS_NAMESPACED_KEY) {
            return material.getKey().toString();
        }
        var filter = (Predicate<Entry<?, ?>>) (e) -> e.getValue() == material;
        return KEY_NAMES.entrySet().stream().filter(filter).findFirst().get().getKey();
    }

    @NotNull
    public static String removeKey(@NotNull String name) {
        return StringUtils.removeStart(name, ItemUtils.MINECRAFT);
    }

    @NotNull
    public static Material getMaterial(@NotNull String name) {
        var material = (Material) null;
        if (HAS_NAMESPACED_KEY) {
            material = Material.matchMaterial(name);
        } else {
            name = removeKey(name);
            name = name.toUpperCase(Locale.ENGLISH);
            name = name.replaceAll("\\s+", "_").replaceAll("\\W", "");
            material = Material.getMaterial(name);
        }
        return material == null ? Material.AIR : material;
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
    public static ItemStack[] getHandItems(Player player) {
        var inventory = player.getInventory();
        return new ItemStack[] { inventory.getItemInMainHand(), inventory.getItemInOffHand() };
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

    @NotNull
    public static String getName(@NotNull ItemStack item) {
        return getName(item, item.getType().name());
    }

    public static boolean isItem(@Nullable ItemStack item, @Nullable Material material, @NotNull String name) {
        return isItem(item, material, name::equals);
    }

    public static boolean isItem(@Nullable ItemStack item, @Nullable Material material, @NotNull Predicate<String> name) {
        return item != null && material != null && item.getType() == material && name.test(getName(item));
    }
}