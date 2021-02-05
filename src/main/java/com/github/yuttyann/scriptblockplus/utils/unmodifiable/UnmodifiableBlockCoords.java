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
package com.github.yuttyann.scriptblockplus.utils.unmodifiable;

import com.github.yuttyann.scriptblockplus.BlockCoords;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

/**
 * ScriptBlockPlus UnmodifiableBlockCoords クラス
 * <p>
 * 座標が変更されることが無いため、
 * <p>
 * {@link UnmodifiableBlockCoords#toLocation()}の情報はキャッシュされる。
 * @author yuttyann44581
 */
public final class UnmodifiableBlockCoords extends BlockCoords {

    private Location location;

    public UnmodifiableBlockCoords(@NotNull BlockCoords blockCoords) {
        super(blockCoords.getWorld(), blockCoords.getX(), blockCoords.getY(), blockCoords.getZ());
    }

    @Override
    public void setX(int x) {
        throw new UnsupportedOperationException(this.getClass().getName());
    }

    @Override
    public void setY(int y) {
        throw new UnsupportedOperationException(this.getClass().getName());
    }

    @Override
    public void setZ(int z) {
        throw new UnsupportedOperationException(this.getClass().getName());
    }

    @Override
    @NotNull
    public BlockCoords add(int x, int y, int z) {
        throw new UnsupportedOperationException(this.getClass().getName());
    }

    @Override
    @NotNull
    public BlockCoords subtract(int x, int y, int z) {
        throw new UnsupportedOperationException(this.getClass().getName());
    }

    @Override
    @NotNull
    public Location toLocation() {
        return location == null ? location = super.toLocation() : location;
    }
}