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

import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.script.option.OptionTag;
import com.github.yuttyann.scriptblockplus.utils.ItemUtils;
import com.github.yuttyann.scriptblockplus.utils.StreamUtils;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;

import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

/**
 * ScriptBlockPlus BlockType オプションクラス
 * @author yuttyann44581
 */
@OptionTag(name = "blocktype", syntax = "@blocktype:")
public class BlockType extends BaseOption {

    @Override
    protected boolean isValid() throws Exception {
        var array = StringUtils.split(getOptionValue(), ',');
        var block = getLocation().getBlock();
        return StreamUtils.anyMatch(array, s -> equals(block, s));
    }

    private boolean equals(@NotNull Block block, @NotNull String type) {
        if (StringUtils.isEmpty(type)) {
            return false;
        }
        var array = StringUtils.split(StringUtils.removeStart(type, Utils.MINECRAFT), ':');
        if (Calculation.REALNUMBER_PATTERN.matcher(array[0]).matches()) {
            Utils.sendColorMessage(getSBPlayer(), "§cNumerical values can not be used");
            return false;
        }
        var material = ItemUtils.getMaterial(array[0]);
        if (material == null || !material.isBlock()) {
            return false;
        }
        byte data = array.length == 2 ? Byte.parseByte(array[1]) : -1;
        return material == block.getType() && (data == -1 || data == getData(block));
    }

    private byte getData(@NotNull Block block) {
        @SuppressWarnings("deprecation")
        byte data = block.getData();
        return data;
    }
}