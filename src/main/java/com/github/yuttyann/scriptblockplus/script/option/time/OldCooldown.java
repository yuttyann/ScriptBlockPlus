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

import com.github.yuttyann.scriptblockplus.file.json.derived.PlayerTempJson;
import com.github.yuttyann.scriptblockplus.file.json.element.TimerTemp;
import com.github.yuttyann.scriptblockplus.script.option.OptionTag;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

/**
 * ScriptBlockPlus OldCooldown オプションクラス
 * @author yuttyann44581
 */
@OptionTag(name = "oldcooldown", syntax = "@oldcooldown:")
public class OldCooldown extends TimerOption {

    private static final UUID UUID_OLDCOOLDOWN = UUID.nameUUIDFromBytes(OldCooldown.class.getName().getBytes());

    @Override
    protected boolean isValid() throws Exception {
        if (inCooldown()) {
            return false;
        }
        var params = new long[] { System.currentTimeMillis(), Integer.parseInt(getOptionValue()) * 1000L, 0L };
        params[2] = params[0] + params[1];

        var tempJson = PlayerTempJson.get(getFileUniqueId());
        tempJson.load().getTimerTemp().add(new TimerTemp(getScriptKey(), getBlockCoords()).setParams(params));
        tempJson.saveJson();
        return true;
    }

    @Override
    @NotNull
    protected UUID getFileUniqueId() {
        return UUID_OLDCOOLDOWN;
    }

    @Override
    @NotNull
    protected Optional<TimerTemp> getTimerTemp() {
        return getTimerTemp(new TimerTemp(getScriptKey(), getBlockCoords()));
    }
}