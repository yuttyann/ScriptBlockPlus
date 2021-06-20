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

import com.github.yuttyann.scriptblockplus.enums.MatchType;
import com.github.yuttyann.scriptblockplus.file.config.SBConfig;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.script.option.OptionTag;
import com.github.yuttyann.scriptblockplus.utils.ItemUtils;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;

import org.bukkit.inventory.ItemStack;

/**
 * ScriptBlockPlus ItemHand オプションクラス
 * @author yuttyann44581
 */
@OptionTag(name = "itemhand", syntax = "@hand:", description = "<id>[:damage] <amount> [name][:lore]")
public final class ItemHand extends BaseOption {

    @Override
    protected boolean isValid() throws Exception {
        var space = StringUtils.split(getOptionValue(), ' ');
        var itemId = StringUtils.split(StringUtils.removeStart(space.get(0), Utils.MINECRAFT), ':');
        if (Calculation.REALNUMBER_PATTERN.matcher(itemId.get(0)).matches()) {
            throw new IllegalAccessException("Numerical values can not be used");
        }
        var material = ItemUtils.getMaterial(itemId.get(0));
        int damage = itemId.size() > 1 ? Integer.parseInt(itemId.get(1)) : -1;
        int amount = Integer.parseInt(space.get(1));
        var create = space.size() > 2 ? StringUtils.createString(space, 2) : null;
            create = StringUtils.isEmpty(create) ? material.name() : StringUtils.setColor(create);

        var names = StringUtils.split(create, ':');
        var inventory = getPlayer().getInventory();
        for (var hand : new ItemStack[] { inventory.getItemInMainHand(), inventory.getItemInOffHand() }) {
            if (!ItemUtils.compare(MatchType.TYPE, hand, material)
                || !ItemUtils.compare(MatchType.NAME, hand, names.get(0))
                || !ItemUtils.compare(MatchType.AMOUNT, hand, amount)
                || damage != -1 && !ItemUtils.compare(MatchType.META, hand, damage)
                || names.size() > 1 && !ItemUtils.compare(MatchType.LORE, hand, names.get(1))) {
                continue;
            }
            return true;
        }
        SBConfig.ERROR_HAND.replace(material, amount, damage, StringUtils.setColor(names.get(0))).send(getPlayer());
        return false;
    }
}