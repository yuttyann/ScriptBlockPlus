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

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * ScriptBlockPlus CuboidRegion クラス
 * @author yuttyann44581
 */
public class CuboidRegion implements Region {

    private World world;
    private Vector pos1;
    private Vector pos2;

    public void setWorld(@Nullable World world) {
        if (this.world != null && !Objects.equals(this.world, world)) {
            setVector1(null);
            setVector2(null);
        }
        this.world = world;
    }

    public void setVector1(@Nullable Vector pos1) {
        this.pos1 = pos1;
    }

    public void setVector2(@Nullable Vector pos2) {
        this.pos2 = pos2;
    }

    @Override
    @Nullable
    public World getWorld() {
        return world;
    }

    @Override
    @NotNull
    public String getName() {
        return world == null ? "null" : world.getName();
    }

    @Override
    @NotNull
    public Location getMinimumPoint() {
        return toLocation(min(pos1.getX(), pos2.getX()), min(pos1.getY(), pos2.getY()), min(pos1.getZ(), pos2.getZ()));
    }

    @Override
    @NotNull
    public Location getMaximumPoint() {
        return toLocation(max(pos1.getX(), pos2.getX()), max(pos1.getY(), pos2.getY()), max(pos1.getZ(), pos2.getZ()));
    }

    @Override
    public boolean hasPositions() {
        return world != null && pos1 != null && pos2 != null;
    }

    private Location toLocation(double x, double y, double z) {
        return new Location(world, x, y, z);
    }

    private double min(double a, double b) {
        return Math.min(a, b);
    }

    private double max(double a, double b) {
        return Math.max(a, b);
    }
}