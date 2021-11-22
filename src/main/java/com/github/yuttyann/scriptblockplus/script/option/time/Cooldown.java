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
package com.github.yuttyann.scriptblockplus.script.option.time;

import com.github.yuttyann.scriptblockplus.file.json.derived.PlayerTimerJson;
import com.github.yuttyann.scriptblockplus.file.json.derived.element.PlayerTimer;
import com.github.yuttyann.scriptblockplus.script.option.OptionTag;
import org.jetbrains.annotations.Nullable;

/**
 * ScriptBlockPlus Cooldown オプションクラス
 * @author yuttyann44581
 */
@OptionTag(name = "cooldown", syntax = "@cooldown:", description = "<second>")
public final class Cooldown extends TimerOption {

    @Override
    protected boolean isValid() throws Exception {
        if (inCooldown()) {
            return false;
        }
        var time = new long[] { System.currentTimeMillis(), Integer.parseInt(getOptionValue()) * 1000L, 0L };
        time[2] = time[0] + time[1];

        var timerJson = PlayerTimerJson.newJson(getFileUniqueId());
        timerJson.load(getFileUniqueId(), getScriptKey(), getBlockCoords()).setTime(time);
        timerJson.saveJson();
        return true;
    }

    @Override
    @Nullable
    protected PlayerTimer getPlayerTimer() {
        return PlayerTimerJson.newJson(getFileUniqueId()).load(getFileUniqueId(), getScriptKey(), getBlockCoords());
    }
}