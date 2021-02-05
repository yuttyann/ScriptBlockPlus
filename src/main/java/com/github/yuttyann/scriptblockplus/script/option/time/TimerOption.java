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

import com.github.yuttyann.scriptblockplus.file.config.SBConfig;
import com.github.yuttyann.scriptblockplus.file.json.derived.PlayerTempJson;
import com.github.yuttyann.scriptblockplus.file.json.element.TimerTemp;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.utils.StreamUtils;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

/**
 * ScriptBlockPlus TimerOption オプションクラス
 * @author yuttyann44581
 */
public abstract class TimerOption extends BaseOption {

    @NotNull
    protected abstract Optional<TimerTemp> getTimerTemp();

    @NotNull
    protected final Optional<TimerTemp> getTimerTemp(@NotNull TimerTemp timerTemp) {
        var timers = PlayerTempJson.get(getFileUniqueId()).load().getTimerTemp();
        return StreamUtils.filterFirst(timers, t -> t.equals(timerTemp));
    }

    @NotNull
    protected UUID getFileUniqueId() {
        return getUniqueId();
    }

    protected boolean inCooldown() {
        var timer = getTimerTemp();
        if (timer.isPresent()) {
            int time = timer.get().getSecond();
            if (time > 0) {
                short hour = (short) (time / 3600);
                byte minute = (byte) (time % 3600 / 60);
                byte second = (byte) (time % 3600 % 60);
                SBConfig.ACTIVE_COOLDOWN.replace(hour, minute, second).send(getSBPlayer());
                return true;
            } else {
                PlayerTempJson.get(getFileUniqueId()).load().getTimerTemp().remove(timer.get());
            }
        }
        return false;
    }
}