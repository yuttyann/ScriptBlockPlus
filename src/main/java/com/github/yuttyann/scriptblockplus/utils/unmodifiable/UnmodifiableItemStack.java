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
package com.github.yuttyann.scriptblockplus.utils.unmodifiable;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Map;

/**
 * ScriptBlockPlus UnmodifiableItemStack クラス
 * @author yuttyann44581
 */
@SuppressWarnings("deprecation")
public final class UnmodifiableItemStack extends ItemStack {

    public UnmodifiableItemStack(@NotNull ItemStack item) {
        super(item);
    }

    @Override
    public void setType(@NotNull Material type) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setAmount(int amount) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setData(@Nullable MaterialData data) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    public void setDurability(short durability) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addEnchantment(@NotNull Enchantment ench, int level) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addEnchantments(@NotNull Map<Enchantment, Integer> enchantments) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addUnsafeEnchantment(@NotNull Enchantment ench, int level) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addUnsafeEnchantments(@NotNull Map<Enchantment, Integer> enchantments) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int removeEnchantment(@NotNull Enchantment ench) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean setItemMeta(@Nullable ItemMeta meta) {
        throw new UnsupportedOperationException();
    }
}