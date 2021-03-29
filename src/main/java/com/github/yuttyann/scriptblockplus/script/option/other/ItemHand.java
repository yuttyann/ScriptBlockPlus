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

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

/**
 * ScriptBlockPlus ItemHand オプションクラス
 * @author yuttyann44581
 */
@OptionTag(name = "itemhand", syntax = "@hand:")
public final class ItemHand extends BaseOption {

    @Override
    protected boolean isValid() throws Exception {
        var space = StringUtils.split(getOptionValue(), ' ');
        var itemId = StringUtils.split(StringUtils.removeStart(space.get(0), Utils.MINECRAFT), ':');
        if (Calculation.REALNUMBER_PATTERN.matcher(itemId.get(0)).matches()) {
            throw new IllegalAccessException("Numerical values can not be used");
        }
        var material = ItemUtils.getMaterial(itemId.get(0));
        int damage = itemId.size() > 1 ? Integer.parseInt(itemId.get(1)) : 0;
        int amount = Integer.parseInt(space.get(1));
        var create = space.size() > 2 ? StringUtils.createString(space, 2) : null;
        var name = StringUtils.isEmpty(create) ? material.name() : StringUtils.setColor(create);

        var player = getPlayer();
        var handItems = getHandStream(player);
        if (handItems.noneMatch(i -> i.getAmount() >= amount && ItemUtils.getDamage(i) == damage && ItemUtils.compare(i, material, name))) {
            SBConfig.ERROR_HAND.replace(material, amount, damage, StringUtils.setColor(create)).send(player);
            return false;
        }
        return true;
    }

    @NotNull
    private Stream<ItemStack> getHandStream(@NotNull Player player) {
        var inventory = player.getInventory();
        return Stream.of(new ItemStack[] { inventory.getItemInMainHand(), inventory.getItemInOffHand() });
    }
}