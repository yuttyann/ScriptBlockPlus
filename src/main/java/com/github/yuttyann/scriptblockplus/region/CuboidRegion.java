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
package com.github.yuttyann.scriptblockplus.region;

import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import com.github.yuttyann.scriptblockplus.BlockCoords;

import static java.lang.Math.min;
import static java.lang.Math.max;

/**
 * ScriptBlockPlus CuboidRegion クラス
 * @author yuttyann44581
 */
public final class CuboidRegion implements Region {

    private BlockCoords pos1;
    private BlockCoords pos2;

    public void setPos1(@Nullable BlockCoords pos1) {
        this.pos1 = pos1;
    }

    public void setPos2(@Nullable BlockCoords pos2) {
        this.pos2 = pos2;
    }

    @Override
    @Nullable
    public World getWorld() {
        if (pos1 != null && pos2 != null && Objects.equals(pos1.getWorld(), pos2.getWorld())) {
            return pos1.getWorld();
        }
        return null;
    }

    @Override
    @NotNull
    public String getName() {
        var world = getWorld();
        return world == null ? "null" : world.getName();
    }

    @Override
    public boolean hasPositions() {
        return getWorld() != null && pos1 != null && pos2 != null;
    }

    @Override
    @NotNull
    public BlockCoords getMinimumPoint() {
        double minX = min(pos1.getX(), pos2.getX());
        double minY = min(pos1.getY(), pos2.getY());
        double minZ = min(pos1.getZ(), pos2.getZ());
        return BlockCoords.of(getWorld(), minX, minY, minZ);
    }

    @Override
    @NotNull
    public BlockCoords getMaximumPoint() {
        double maxX = max(pos1.getX(), pos2.getX());
        double maxY = max(pos1.getY(), pos2.getY());
        double maxZ = max(pos1.getZ(), pos2.getZ());
        return BlockCoords.of(getWorld(), maxX, maxY, maxZ);
    }
}