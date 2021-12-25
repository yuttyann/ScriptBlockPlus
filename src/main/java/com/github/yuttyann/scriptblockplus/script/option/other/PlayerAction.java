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

import com.github.yuttyann.scriptblockplus.script.ScriptKey;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.script.option.OptionTag;
import com.github.yuttyann.scriptblockplus.utils.StreamUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import org.bukkit.event.block.Action;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * ScriptBlockPlus ScriptAction オプションクラス
 * @author yuttyann44581
 */
@OptionTag(name = "action", syntax = "@action:", description = "<actions>")
public final class PlayerAction extends BaseOption {

    public static final String KEY = Utils.randomUUID();

    @Override
    protected boolean isValid() throws Exception {
        if (!getTempMap().has(KEY)) {
            return false;
        }
        var action = (Action) getTempMap().get(KEY);
        return StreamUtils.allMatch(split(getOptionValue(), ',', false), s -> compare(action, s));
    }

    private boolean compare(@Nullable Action action, @NotNull String type) {
        return type.equalsIgnoreCase("shift") ? getSBPlayer().isSneaking() : ScriptKey.INTERACT.equals(getScriptKey()) && action == getAction(type);
    }

    @Nullable
    private Action getAction(@NotNull String action) {
        return action.equalsIgnoreCase("left") ? Action.LEFT_CLICK_BLOCK : action.equalsIgnoreCase("right") ? Action.RIGHT_CLICK_BLOCK : null;
    }
}