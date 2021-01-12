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

import com.github.yuttyann.scriptblockplus.file.json.PlayerTempJson;
import com.github.yuttyann.scriptblockplus.script.option.Option;
import com.github.yuttyann.scriptblockplus.script.option.OptionTag;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * ScriptBlockPlus Cooldown オプションクラス
 * @author yuttyann44581
 */
@OptionTag(name = "cooldown", syntax = "@cooldown:")
public class Cooldown extends TimerOption {

    @Override
    @NotNull
    public Option newInstance() {
        return new Cooldown();
    }

    @Override
    protected boolean isValid() throws Exception {
        if (inCooldown()) {
            return false;
        }
        var params = new long[] { System.currentTimeMillis(), Integer.parseInt(getOptionValue()) * 1000L, 0L };
        params[2] = params[0] + params[1];

        var tempJson = new PlayerTempJson(getFileUniqueId());
        tempJson.load().getTimerTemp().add(new TimerTemp(getFileUniqueId(), getLocation(), getScriptKey()).set(params));
        tempJson.saveFile();
        return true;
    }

    @Override
    @NotNull
    protected Optional<TimerTemp> getTimerTemp() {
        var timers = new PlayerTempJson(getFileUniqueId()).load().getTimerTemp();
        return get(timers, new TimerTemp(getFileUniqueId(), getLocation(), getScriptKey()));
    }
}