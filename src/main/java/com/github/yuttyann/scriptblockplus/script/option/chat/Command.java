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
package com.github.yuttyann.scriptblockplus.script.option.chat;

import com.github.yuttyann.scriptblockplus.enums.CommandLog;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.script.option.OptionTag;
import com.github.yuttyann.scriptblockplus.utils.Utils;

/**
 * ScriptBlockPlus Command オプションクラス
 * @author yuttyann44581
 */
@OptionTag(name = "command", syntax = "@command ", description = "<command>")
public final class Command extends BaseOption {

    @Override
    protected Result isValid() throws Exception {
        var player = getSBPlayer().toPlayer();
        var command = setColor(getOptionValue(), true);
        return CommandLog.supplier(player.getWorld(), () -> toResult(Utils.dispatchCommand(player, getLocation(), command)));
    }
}