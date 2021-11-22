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
package com.github.yuttyann.scriptblockplus.file.json.derived.element;

import java.util.Objects;
import java.util.UUID;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.file.json.basic.ThreeJson.ThreeElement;
import com.github.yuttyann.scriptblockplus.script.ScriptKey;
import com.google.gson.InstanceCreator;
import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * ScriptBlockPlus PlayerCooldown クラス
 * @author yuttyann44581
 */
public final class PlayerTimer extends ThreeElement<UUID, ScriptKey, BlockCoords> {

    public static final InstanceCreator<PlayerTimer> INSTANCE = t -> new PlayerTimer(null, ScriptKey.INTERACT, BlockCoords.ZERO);

    @SerializedName("time")
    private long[] time;

    @SerializedName("uuid")
    private final UUID uuid;

    @SerializedName("scriptkey")
    private final ScriptKey scriptKey;

    @SerializedName("blockcoords")
    private final BlockCoords blockCoords;

    public PlayerTimer(@Nullable UUID uuid, @NotNull ScriptKey scriptKey, @NotNull BlockCoords blockCoords) {
        this.uuid = uuid;
        this.scriptKey = Objects.requireNonNull(scriptKey);
        this.blockCoords = Objects.requireNonNull(blockCoords);
    }

    @Override
    @NotNull
    protected UUID getA() {
        return uuid;
    }

    @Override
    @NotNull
    protected ScriptKey getB() {
        return scriptKey;
    }

    @Override
    @NotNull
    protected BlockCoords getC() {
        return blockCoords;
    }

    @Override
    public boolean isElement(@Nullable UUID uuid, @NotNull ScriptKey scriptKey, @NotNull BlockCoords blockCoords) {
        if (this.uuid != null && !this.uuid.equals(uuid)) {
            return false;
        }
        return this.scriptKey.ordinal() == scriptKey.ordinal() && this.blockCoords.compare(blockCoords);
    }

    @NotNull
    public void setTime(@NotNull long... time) {
        this.time = time;
    }

    public int getSecond() {
        if (time == null) {
            return 0;
        }
        var milli = System.currentTimeMillis();
        return time[2] > milli ? Math.toIntExact((time[2] - milli) / 1000L) : 0;
    }
}