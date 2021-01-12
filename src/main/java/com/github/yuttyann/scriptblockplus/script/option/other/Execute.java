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

import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.ScriptBlockAPI;
import com.github.yuttyann.scriptblockplus.script.ScriptKey;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.script.option.Option;
import com.github.yuttyann.scriptblockplus.script.option.OptionTag;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

/**
 * ScriptBlockPlus Execute オプションクラス
 * @author yuttyann44581
 */
@OptionTag(name = "execute", syntax = "@execute:")
public class Execute extends BaseOption {

    private static final ScriptBlockAPI API = ScriptBlock.getInstance().getAPI();

    @Override
    @NotNull
    public Option newInstance() {
        return new Execute();
    }

    @Override
    protected boolean isValid() throws Exception {
        var array = StringUtils.split(getOptionValue(), '/');
        var wxyz = StringUtils.split(array[1], ',');
        var scriptKey = ScriptKey.valueOf(array[0]);
        int x = Integer.parseInt(wxyz[1]), y = Integer.parseInt(wxyz[2]), z = Integer.parseInt(wxyz[3]);
        return API.read(getPlayer(), new Location(Utils.getWorld(wxyz[0]), x, y, z), scriptKey, 0);
    }
}