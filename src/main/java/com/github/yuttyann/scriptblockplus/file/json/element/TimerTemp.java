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
package com.github.yuttyann.scriptblockplus.file.json.element;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.file.json.annotation.LegacyName;
import com.github.yuttyann.scriptblockplus.file.json.annotation.Exclude;
import com.github.yuttyann.scriptblockplus.script.ScriptKey;
import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

/**
 * ScriptBlockPlus TimerTemp クラス
 * @author yuttyann44581
 */
public final class TimerTemp {

    @Exclude
    private final boolean empty;

    @SerializedName("params")
    private long[] params;

    @SerializedName("uuid")
    private UUID uuid;

    @LegacyName(alternate = { "scripttype", "scriptType" })
    @SerializedName(value = "scriptkey", alternate = { "scripttype", "scriptType" })
    private ScriptKey scriptKey;

    @LegacyName(alternate = { "fullcoords", "fullCoords" })
    @SerializedName(value = "blockcoords", alternate = { "fullcoords", "fullCoords" })
    private BlockCoords blockCoords;

    private TimerTemp() {
        this.empty = true;
    }

    public TimerTemp(@NotNull ScriptKey scriptKey, @NotNull BlockCoords blockCoords) {
        this(null, scriptKey, blockCoords);
    }

    public TimerTemp(@Nullable UUID uuid, @NotNull ScriptKey scriptKey, @NotNull BlockCoords blockCoords) {
        this.empty = false;
        this.uuid = uuid;
        this.scriptKey = scriptKey;
        this.blockCoords = blockCoords;
    }

    @NotNull
    public static TimerTemp empty() {
        return new TimerTemp();
    }
    
    @NotNull
    public TimerTemp setParams(@NotNull long[] params) {
        this.params = params;
        return this;
    }

    @NotNull
    public TimerTemp setUniqueId(@NotNull UUID uuid) {
        if (!isEmpty()) {
            throw new UnsupportedOperationException("not Empty");
        }
        this.uuid = uuid;
        return this;
    }

    @NotNull
    public TimerTemp setScriptKey(@NotNull ScriptKey scriptKey) {
        if (!isEmpty()) {
            throw new UnsupportedOperationException("not Empty");
        }
        this.scriptKey = scriptKey;
        return this;
    }

    @NotNull
    public TimerTemp setBlockCoords(@NotNull BlockCoords blockCoords) {
        if (!isEmpty()) {
            throw new UnsupportedOperationException("not Empty");
        }
        this.blockCoords = blockCoords;
        return this;
    }

    public int getSecond() {
        if (params != null && params[2] > System.currentTimeMillis()) {
            return Math.toIntExact((params[2] - System.currentTimeMillis()) / 1000L);
        }
        return 0;
    }

    public boolean isEmpty() {
        return empty;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof TimerTemp)) {
            return false;
        }
        var timerTemp = (TimerTemp) obj;
        return Objects.equals(uuid, timerTemp.uuid) && Objects.equals(blockCoords, timerTemp.blockCoords) && Objects.equals(scriptKey, timerTemp.scriptKey);
    }

    @Override
    public int hashCode() {
        int hash = 1;
        int prime = uuid == null ? 31 : 11;
        hash = prime * hash + Objects.hash(scriptKey);
        hash = prime * hash + Objects.hash(blockCoords);
        return hash;
    }
}