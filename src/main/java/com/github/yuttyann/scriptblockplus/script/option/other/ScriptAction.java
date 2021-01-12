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
import com.github.yuttyann.scriptblockplus.script.option.Option;
import com.github.yuttyann.scriptblockplus.script.option.OptionTag;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import org.bukkit.event.block.Action;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

/**
 * ScriptBlockPlus ScriptAction オプションクラス
 * @author yuttyann44581
 */
@OptionTag(name = "scriptaction", syntax = "@scriptaction:")
public class ScriptAction extends BaseOption {

    public static final String KEY = Utils.randomUUID();

    @Override
    @NotNull
    public Option newInstance() {
        return new ScriptAction();
    }

    @Override
    protected boolean isValid() throws Exception {
        if (!getTempMap().has(KEY)) {
            return false;
        }
        var action = (Action) getTempMap().get(KEY);
        return Stream.of(StringUtils.split(getOptionValue(), ',')).allMatch(s -> equals(action, s));
    }

    private boolean equals(@Nullable Action action, @NotNull String type) {
        if (type.equalsIgnoreCase("shift")) {
            return getPlayer().isSneaking();
        }
        return ScriptKey.INTERACT.equals(getScriptKey()) && action == getAction(type);
    }

    @Nullable
    private Action getAction(@NotNull String action) {
        if (action.equalsIgnoreCase("left")) {
            return Action.LEFT_CLICK_BLOCK;
        } else if (action.equalsIgnoreCase("right")) {
            return Action.RIGHT_CLICK_BLOCK;
        }
        return null;
    }
}