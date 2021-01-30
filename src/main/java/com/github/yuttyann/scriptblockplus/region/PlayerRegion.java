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
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * ScriptBlockPlus PlayerRegion クラス
 * @author yuttyann44581
 */
public class PlayerRegion implements Region {

    private final World world;
    private final int x;
    private final int y;
    private final int z;
    private final int range;

    public PlayerRegion(@NotNull Player player, int range) {
        var location = player.getLocation();
        this.world = location.getWorld();
        this.x = location.getBlockX();
        this.y = location.getBlockY();
        this.z = location.getBlockZ();
        this.range = Math.max(range, 1);
    }

    @Override
    @NotNull
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
        return new Location(world, x - range, y - range, z - range);
    }

    @Override
    @NotNull
    public Location getMaximumPoint() {
        return new Location(world, x + range, y + range, z + range);
    }

    @Override
    public boolean hasPositions() {
        return true;
    }
}