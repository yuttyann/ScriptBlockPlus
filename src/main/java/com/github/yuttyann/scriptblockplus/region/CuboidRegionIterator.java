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

import java.util.Iterator;
import java.util.NoSuchElementException;

import com.github.yuttyann.scriptblockplus.BlockCoords;

import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

/**
 * ScriptBlockPlus CuboidRegionIterator クラス
 * @author yuttyann44581
 */
public final class CuboidRegionIterator implements Iterator<BlockCoords> {

    private final BlockCoords table;

    private final Region region;
    private final int baseX, baseY, baseZ, sizeX, sizeY, sizeZ;

    private boolean hasNext;
    private int nextX, nextY, nextZ, volume;

    public CuboidRegionIterator(@NotNull Region region) {
        var min = region.getMinimumPoint();
        var max = region.getMaximumPoint();
        this.region = region;
        this.baseX = min.getX();
        this.baseY = min.getY();
        this.baseZ = min.getZ();
        this.sizeX = Math.abs(max.getX() - baseX) + 1;
        this.sizeY = Math.abs(max.getY() - baseY) + 1;
        this.sizeZ = Math.abs(max.getZ() - baseZ) + 1;
        this.table = BlockCoords.zero(region.getWorld());
        reset();
    }

    public void reset() {
        this.nextX = nextZ = nextY = volume = 0;
        hasNext();
    }

    public int getVolume() {
        return volume;
    }

    @NotNull
    public World getWorld() {
        return region.getWorld();
    }

    @NotNull
    public BlockCoords getMinimumPoint() {
        return region.getMinimumPoint();
    }

    @NotNull
    public BlockCoords getMaximumPoint() {
        return region.getMaximumPoint();
    }

    @Override
    public boolean hasNext() {
        return hasNext = (nextX < sizeX && nextY < sizeY && nextZ < sizeZ);
    }

    @Override
    public BlockCoords next() {
        if (!hasNext) {
            throw new NoSuchElementException();
        }
        table.setX(baseX + nextX);
        table.setY(baseY + nextY);
        table.setZ(baseZ + nextZ);
        if (++nextX >= sizeX) {
            nextX = 0;
            if (++nextY >= sizeY) {
                nextY = 0;
                ++nextZ;
            }
        }
        volume++;
        return table;
    }
}