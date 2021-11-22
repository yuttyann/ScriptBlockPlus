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

import com.github.yuttyann.scriptblockplus.enums.MatchType;
import com.github.yuttyann.scriptblockplus.enums.server.NetMinecraft;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * ScriptBlockPlus ItemUtils クラス
 * @author yuttyann44581
 */
public class ItemUtils {

    private static final Map<String, Material> KEY_MATERIALS;

    static {
        if (!Utils.isCBXXXorLater("1.13") && NetMinecraft.hasNMS()) {
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
    public static ItemStack getGlassPane(@NotNull int color) {
        if (Utils.isCBXXXorLater("1.13")) {
            var material = (Material) null;
            switch (color) {
                case 0:
                    material = getMaterial("WHITE_STAINED_GLASS_PANE");
                    break;
                case 1:
                    material = getMaterial("ORANGE_STAINED_GLASS_PANE");
                    break;
                case 2:
                    material = getMaterial("MAGENTA_STAINED_GLASS_PANE");
                    break;
                case 3:
                    material = getMaterial("LIGHT_BLUE_STAINED_GLASS_PANE");
                    break;
                case 4:
                    material = getMaterial("YELLOW_STAINED_GLASS_PANE");
                    break;
                case 5:
                    material = getMaterial("LIME_STAINED_GLASS_PANE");
                    break;
                case 6:
                    material = getMaterial("PINK_STAINED_GLASS_PANE");
                    break;
                case 7:
                    material = getMaterial("GRAY_STAINED_GLASS_PANE");
                    break;
                case 8:
                    material = getMaterial("LIGHT_GRAY_STAINED_GLASS_PANE");
                    break;
                case 9:
                    material = getMaterial("CYAN_STAINED_GLASS_PANE");
                    break;
                case 10:
                    material = getMaterial("PURPLE_STAINED_GLASS_PANE");
                    break;
                case 11:
                    material = getMaterial("BLUE_STAINED_GLASS_PANE");
                    break;
                case 12:
                    material = getMaterial("BROWN_STAINED_GLASS_PANE");
                    break;
                case 13:
                    material = getMaterial("GREEN_STAINED_GLASS_PANE");
                    break;
                case 14:
                    material = getMaterial("RED_STAINED_GLASS_PANE");
                    break;
                case 15:
                    material = getMaterial("BLACK_STAINED_GLASS_PANE");
                    break;
                default:
                    material = getMaterial("STAINED_GLASS_PANE");
                    break;
            }
            return new ItemStack(material, 1);
        } else {
            @SuppressWarnings("deprecation")
            var item = new ItemStack(getMaterial("STAINED_GLASS_PANE"), 1, (short) color);
            return item;
        }
    }

    @NotNull
    public static Material getClockMaterial() {
        return getMaterial("CLOCK", "WATCH");
    }

    @NotNull
    public static Material getOakSignMaterial() {
        return getMaterial("OAK_SIGN", "SIGN");
    }

    @NotNull
    public static Material getWritableBookMaterial() {
        return getMaterial("WRITABLE_BOOK", "BOOK_AND_QUILL");
    }

    @NotNull
    public static Material getCommandMaterial() {
        return getMaterial("COMMAND_BLOCK", "COMMAND");
    }

    @NotNull
    public static Material getChainCommandMaterial() {
        return getMaterial("CHAIN_COMMAND_BLOCK", "COMMAND_CHAIN");
    }

    @NotNull
    public static Material getMaterial(@NotNull String name) {
        return getMaterial(name, null);
    }

    @NotNull
    public static Material getMaterial(@NotNull String name, @Nullable String def) {
        name = StringUtils.removeStart(name.replace(' ', '_'), Utils.MINECRAFT);
        if (KEY_MATERIALS != null) {
            var key = name.toLowerCase(Locale.ROOT);
            if (KEY_MATERIALS.containsKey(key)) {
                return KEY_MATERIALS.get(key);
            }
        }
        var material = Material.getMaterial(name.toUpperCase(Locale.ROOT));
        return material == null ? def == null ? Material.AIR : Objects.requireNonNull(getMaterial(def)) : material;
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
    public static ItemStack setName(@NotNull ItemStack item, @Nullable String name) {
        var itemMeta = item.getItemMeta();
        if (itemMeta == null) {
            return item;
        }
        itemMeta.setDisplayName(name);
        item.setItemMeta(itemMeta);
        return item;
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
        var itemMeta = item.getItemMeta();
        return itemMeta == null ? def : itemMeta.hasDisplayName() ? itemMeta.getDisplayName() : def;
    }

    @NotNull
    public static ItemStack setLore(@NotNull ItemStack item, @Nullable List<String> lore) {
        var itemMeta = item.getItemMeta();
        if (itemMeta == null) {
            return item;
        }
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }

    @NotNull
    public static List<String> getLore(@NotNull ItemStack item) {
        var itemMeta = item.getItemMeta();
        return itemMeta != null && itemMeta.hasLore() ? itemMeta.getLore() : Collections.emptyList();
    }

    @SuppressWarnings("deprecation")
    public static int getDamage(@NotNull ItemStack item) {
        if (Utils.isCBXXXorLater("1.13")) {
            var itemMeta = item.getItemMeta();
            return itemMeta instanceof Damageable ? ((Damageable) itemMeta).getDamage() : 0;
        }
        return item.getDurability();
    }

    public static boolean isAIR(@NotNull Material material) {
        if (Utils.isCBXXXorLater("1.14.4")) {
            return material.isAir();
        } else if (!Utils.isCBXXXorLater("1.13")) {
            return material == Material.AIR;
        }
        return material.name().endsWith("AIR");
    }

    public static boolean compare(@NotNull MatchType matchType, @Nullable ItemStack item, @Nullable Object value) {
        if (item == null || value == null) {
            return false;
        }
        switch (matchType) {
            case TYPE:
                return value instanceof Material ? item.getType().equals((Material) value) : false;
            case META:
                return value instanceof Integer ? ((Integer) value).equals(getDamage(item)) : false;
            case NAME:
                return value instanceof String ? getName(item).equals(StringUtils.setColor((String) value)) : false;
            case LORE:
                return value instanceof String ? getLore(item).stream().collect(Collectors.joining("\\n")).contains(StringUtils.setColor((String) value)) : false;
            case AMOUNT:
                return value instanceof Integer ? item.getAmount() >= (Integer) value : false;
        }
        return false;
    }
}