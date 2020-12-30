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
        return toLocation(x - range, y - range, z - range);
    }

    @Override
    @NotNull
    public Location getMaximumPoint() {
        return toLocation(x + range, y + range, z + range);
    }

    @Override
    public boolean hasPositions() {
        return true;
    }

    @NotNull
    private Location toLocation(double x, double y, double z) {
        return new Location(world, x, y, z);
    }
}