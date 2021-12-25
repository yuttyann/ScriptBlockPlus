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

import com.github.yuttyann.scriptblockplus.utils.Utils;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import static com.github.yuttyann.scriptblockplus.enums.MatchType.*;
import static com.github.yuttyann.scriptblockplus.utils.ItemUtils.*;
import static com.github.yuttyann.scriptblockplus.utils.StringUtils.*;

/**
 * ScriptBlockPlus ItemCost オプションクラス
 * @author yuttyann44581
 */
@OptionTag(name = "itemcost", syntax = "$item:", description = "<id>[:damage] <amount> [name] [lore]")
public final class ItemCost extends BaseOption {

    public static final String KEY_OPTION = Utils.randomUUID();
    public static final String KEY_PLAYER = Utils.randomUUID();

    @Override
    protected boolean isValid() throws Exception {
        var space = split(getOptionValue(), ' ', false);
        var itemId = split(removeStart(space.get(0), Utils.MINECRAFT), ':', false);
        if (IfAction.REALNUMBER_PATTERN.matcher(itemId.get(0)).matches()) {
            throw new IllegalAccessException("Numerical values can not be used");
        }
        var material = getMaterial(itemId.get(0));
        int damage = itemId.size() > 1 ? Integer.parseInt(itemId.get(1)) : -1;
        int amount = Integer.parseInt(space.get(1));
        var name = space.size() > 2 ? escape(space.get(2)) : null;
        var lore = space.size() > 3 ? escape(space.get(3)) : null;

        var player = getPlayer();
        var contents = player.getInventory().getContents();
        if (!getTempMap().has(KEY_OPTION)) {
            getTempMap().put(KEY_OPTION, copyItems(contents));
        }
        int result = amount;
        for (var item : contents) {
            if (!compare(TYPE, item, material)
                || damage != -1 && !compare(META, item, damage)
                || name != null && !compare(NAME, item, name)
                || lore != null && !compare(LORE, item, lore)) {
                continue;
            }
            if ((result -= result > 0 ? setAmount(item, item.getAmount() - result) : 0) == 0) {
                break;
            }
        }
        if (result > 0) {
            sendMessage(SBConfig.ERROR_ITEM.replace(material, amount, damage, name, lore));
            return false;
        }
        player.getInventory().setContents(contents);
        return true;
    }

    private int setAmount(@NotNull ItemStack item, final int amount) {
        int oldAmount = item.getAmount();
        item.setAmount(amount);
        return oldAmount;
    }

    @NotNull
    private ItemStack[] copyItems(@NotNull ItemStack[] items) {
        var copy = new ItemStack[items.length];
        for (int i = 0; i < copy.length; i++) {
            copy[i] = items[i] == null ? null : items[i].clone();
        }
        return copy;
    }
}