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

import com.github.yuttyann.scriptblockplus.utils.unmodifiable.UnmodifiableLocation;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

/**
 * ScriptBlockPlus CuboidRegionBlocks クラス
 * @author yuttyann44581
 */
public final class CuboidRegionBlocks {

    private final World world;
    private final Location min;
    private final Location max;

    private int count = -1;

    public CuboidRegionBlocks(@NotNull Region region) {
        this.world = region.getWorld();
        this.min = new UnmodifiableLocation(region.getMinimumPoint());
        this.max = new UnmodifiableLocation(region.getMaximumPoint());
    }

    @NotNull
    public World getWorld() {
        return world;
    }

    @NotNull
    public Location getMinimumPoint() {
        return min;
    }

    @NotNull
    public Location getMaximumPoint() {
        return max;
    }

    public int getCount() {
        if (count == -1) {
            var count = new int[] { 0 };
            forEach(b -> count[0]++);
            this.count = count[0];
        }
        return count;
    }

    @NotNull
    public Set<Block> getBlocks() {
        var set = new HashSet<Block>();
        forEach(b -> set.add(b.getBlock(world)));
        return set;
    }

    public void forEach(@NotNull Consumer<BlockPosition> action) {
        var position = new BlockPosition();
        for (int x = min.getBlockX(); x <= max.getBlockX(); x++) {
            for (int y = min.getBlockY(); y <= max.getBlockY(); y++) {
                for (int z = min.getBlockZ(); z <= max.getBlockZ(); z++) {
                    action.accept(position.setPos(x, y, z));
                }
            }
        }
    }

    public static final class BlockPosition {

        private int x;
        private int y;
        private int z;

        private BlockPosition setPos(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
            return this;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public int getZ() {
            return z;
        }

        @NotNull
        public Block getBlock(@NotNull World world) {
            return world.getBlockAt(x, y, z);
        }
    }
}