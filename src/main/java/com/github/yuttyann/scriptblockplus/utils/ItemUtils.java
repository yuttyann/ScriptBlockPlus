/**
 * ScriptBlockPlus - Allow you to add script to any blocks.
 * Copyright (C) 2021 yuttyann44581
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <https://www.gnu.org/licenses/>.
 */
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
 * @author yuttyann44581
 */
public class ItemUtils {

    private static final Map<String, Material> KEY_MATERIALS;

    static {
        if (!Utils.isCBXXXorLater("1.13") && PackageType.HAS_NMS) {
            KEY_MATERIALS = new HashMap<>();
            try {
                KEY_MATERIALS.putAll(NMSHelper.getItemRegistry());
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
        name = StringUtils.removeStart(name, Utils.MINECRAFT);
        name = name.replaceAll("\\s+", "_").replaceAll("\\W", "");
        if (KEY_MATERIALS != null) {
            var key = name.toLowerCase(Locale.ROOT);
            if (KEY_MATERIALS.containsKey(key)) {
                return KEY_MATERIALS.get(key);
            }
        }
        var material = Material.getMaterial(name.toUpperCase(Locale.ROOT));
        return material == null ? Material.AIR : material;
    }

    @NotNull
    public static String getKey(@NotNull Material material) {
        if (KEY_MATERIALS == null) {
            return material.getKey().toString();
        }
        var filter = (Predicate<Entry<?, ?>>) e -> e.getValue() == material;
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

    public static boolean compare(@Nullable ItemStack item, @Nullable Material material, @NotNull String name) {
        return compare(item, material, name::equals);
    }

    public static boolean compare(@Nullable ItemStack item, @Nullable Material material, @NotNull Predicate<String> filter) {
        return (item == null || material == null) ? false : item.getType() == material && filter.test(getName(item));
    }
}