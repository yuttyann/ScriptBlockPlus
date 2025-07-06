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
 * <p>
 * {@code 位置1}と{@code 位置2}の範囲内を取得することができます。
 * @author yuttyann44581
 */
public final class CuboidRegion implements Region {

    private BlockCoords position1, position2;

    /**
     * {@code 位置1}を設定します。
     * @param position1 - 位置
     */
    public void setPosition1(@Nullable BlockCoords position1) {
        this.position1 = position1;
    }

    /**
     * {@code 位置2}を設定します。
     * @param position2 - 位置2
     */
    public void setPosition2(@Nullable BlockCoords position2) {
        this.position2 = position2;
    }

    @Override
    @Nullable
    public World getWorld() {
        if (position1 == null || position2 == null) {
            return null;
        }
        return Objects.equals(position1.getWorld(), position2.getWorld()) ? position1.getWorld() : null;
    }

    @Override
    @NotNull
    public String getName() {
        var world = getWorld();
        return world == null ? "null" : world.getName();
    }

    @Override
    public boolean hasPositions() {
        return getWorld() != null && position1 != null && position2 != null;
    }

    @Override
    @NotNull
    public BlockCoords getMinimumPoint() {
        double minX = min(position1.getX(), position2.getX());
        double minY = min(position1.getY(), position2.getY());
        double minZ = min(position1.getZ(), position2.getZ());
        return BlockCoords.of(getWorld(), minX, minY, minZ);
    }

    @Override
    @NotNull
    public BlockCoords getMaximumPoint() {
        double maxX = max(position1.getX(), position2.getX());
        double maxY = max(position1.getY(), position2.getY());
        double maxZ = max(position1.getZ(), position2.getZ());
        return BlockCoords.of(getWorld(), maxX, maxY, maxZ);
    }
}