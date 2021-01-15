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

import com.github.yuttyann.scriptblockplus.file.Json;
import com.github.yuttyann.scriptblockplus.file.config.SBConfig;
import com.github.yuttyann.scriptblockplus.file.json.PlayerTempJson;
import com.github.yuttyann.scriptblockplus.script.ScriptKey;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.utils.StreamUtils;
import com.google.common.collect.Sets;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * ScriptBlockPlus TimerOption オプションクラス
 * @author yuttyann44581
 */
public abstract class TimerOption extends BaseOption {

    @NotNull
    protected abstract Optional<TimerTemp> getTimerTemp();

    @NotNull
    protected final <T> Optional<T> get(@NotNull Set<T> set, @NotNull TimerTemp timerTemp) {
        int hash = timerTemp.hashCode();
        return StreamUtils.filterFirst(set, t -> t.hashCode() == hash);
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
                var tempJson = new PlayerTempJson(getFileUniqueId());
                tempJson.load().getTimerTemp().remove(timer.get());
                tempJson.saveFile();
            }
        }
        return false;
    }

    public static void removeAll(@NotNull Location location, @NotNull ScriptKey scriptKey) {
        removeAll(Sets.newHashSet(location), scriptKey);
    }

    public static void removeAll(@NotNull Set<Location> locations, @NotNull ScriptKey scriptKey) {
        for (var name : Json.getNames(PlayerTempJson.class)) {
            var uuid = UUID.fromString(name);
            var tempJson = new PlayerTempJson(uuid);
            if (!tempJson.exists()) {
                continue;
            }
            boolean modifiable = false;
            for (var location : locations) {
                if (!tempJson.has()) {
                    continue;
                }
                var timer = tempJson.load().getTimerTemp();
                if (timer.size() > 0) {
                    modifiable = true;
                    timer.remove(new TimerTemp(location, scriptKey));
                    timer.remove(new TimerTemp(uuid, location, scriptKey));
                }
            }
            if (modifiable) {
                tempJson.saveFile();
            }
        }
    }
}