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

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.script.ScriptKey;
import com.google.gson.annotations.SerializedName;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

/**
 * ScriptBlockPlus TimerTemp クラス
 * @author yuttyann44581
 */
public class TimerTemp {

    @SerializedName("params")
    private long[] params;

    @SerializedName("uuid")
    private final UUID uuid;

    @SerializedName("fullCoords")
    private final String fullCoords;

    @SerializedName(value = "scriptkey", alternate = { "scriptType" })
    private final ScriptKey scriptKey;

    public TimerTemp(@NotNull Location location, @NotNull ScriptKey scriptKey) {
        this(null, location, scriptKey);
    }

    public TimerTemp(@Nullable UUID uuid, @NotNull Location location, @NotNull ScriptKey scriptKey) {
        this.uuid = uuid;
        this.fullCoords = BlockCoords.getFullCoords(location);
        this.scriptKey = scriptKey;
    }
    
    TimerTemp set(long[] params) {
        this.params = params;
        return this;
    }

    public int getSecond() {
        if (params != null && params[2] > System.currentTimeMillis()) {
            return Math.toIntExact((params[2] - System.currentTimeMillis()) / 1000L);
        }
        return 0;
    }

    @Nullable
    public UUID getUniqueId() {
        return uuid;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof TimerTemp)) {
            return false;
        }
        var temp = (TimerTemp) obj;
        return Objects.equals(uuid, temp.uuid) && fullCoords.equals(temp.fullCoords) && scriptKey.equals(temp.scriptKey);
    }

    @Override
    public int hashCode() {
        int hash = 1;
        int prime = 31;
        boolean isOldCooldown = uuid == null;
        hash = prime * hash + Boolean.hashCode(isOldCooldown);
        if (!isOldCooldown) {
            hash = prime * hash + uuid.hashCode();
        }
        hash = prime * hash + fullCoords.hashCode();
        hash = prime * hash + scriptKey.hashCode();
        return hash;
    }
}