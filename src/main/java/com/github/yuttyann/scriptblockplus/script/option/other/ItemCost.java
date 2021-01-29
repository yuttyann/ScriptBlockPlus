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
package com.github.yuttyann.scriptblockplus.script.option.other;

import com.github.yuttyann.scriptblockplus.file.config.SBConfig;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.script.option.OptionTag;
import com.github.yuttyann.scriptblockplus.utils.ItemUtils;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * ScriptBlockPlus ItemCost オプションクラス
 * @author yuttyann44581
 */
@OptionTag(name = "itemcost", syntax = "$item:")
public class ItemCost extends BaseOption {

    public static final String KEY_OPTION = Utils.randomUUID();
    public static final String KEY_PLAYER = Utils.randomUUID();

    @Override
    protected boolean isValid() throws Exception {
        var array = StringUtils.split(getOptionValue(), ' ');
        var param = StringUtils.split(StringUtils.removeStart(array[0], Utils.MINECRAFT), ':');
        if (Calculation.REALNUMBER_PATTERN.matcher(param[0]).matches()) {
            throw new IllegalAccessException("Numerical values can not be used");
        }
        var material = ItemUtils.getMaterial(param[0]);
        int damage = param.length > 1 ? Integer.parseInt(param[1]) : 0;
        int amount = Integer.parseInt(array[1]);
        var create = array.length > 2 ? StringUtils.createString(array, 2) : null;
        var name = StringUtils.isEmpty(create) ? material.name() : StringUtils.setColor(create);

        var player = getPlayer();
        var inventory = player.getInventory();
        var inventoryItems = inventory.getContents();
        if (!getTempMap().has(KEY_OPTION)) {
            getTempMap().put(KEY_OPTION, copyItems(inventoryItems));
        }
        int result = amount;
        for (var item : inventoryItems) {
            if (item != null && ItemUtils.getDamage(item) == damage && ItemUtils.isItem(item, material, name)) {
                result -= result > 0 ? setAmount(item, item.getAmount() - result) : 0;
            }
        }
        if (result > 0) {
            SBConfig.ERROR_ITEM.replace(material, amount, damage, name).send(player);
            return false;
        }
        inventory.setContents(inventoryItems);
        return true;
    }

    private int setAmount(@NotNull ItemStack item, int amount) {
        int oldAmount = item.getAmount();
        item.setAmount(amount);
        return oldAmount;
    }

    @NotNull
    private ItemStack[] copyItems(@NotNull ItemStack[] items) {
        var copy = new ItemStack[items.length];
        for (int i = 0; i < copy.length; i++) {
            copy[i] = items[i] == null ? new ItemStack(Material.AIR) : items[i].clone();
        }
        return copy;
    }
}