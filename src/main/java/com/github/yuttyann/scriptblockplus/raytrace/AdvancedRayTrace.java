package com.github.yuttyann.scriptblockplus.raytrace;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * ScriptBlockPlus AdvancedRayTrace クラス
 * @author yuttyann44581
 */
public final class AdvancedRayTrace {
    
    private final Vector start;
    private final Vector direction;

    public AdvancedRayTrace(@NotNull Player player) {
        this(player.getEyeLocation().toVector(), player.getEyeLocation().getDirection());
    }

    public AdvancedRayTrace(@NotNull Vector start, @NotNull Vector direction) {
        this.start = start;
        this.direction = direction;
    }

    @NotNull
    public Vector getPostion(final double distance) {
        return new SBVector(start).add(new SBVector(direction).multiply(distance));
    }

    public boolean isOnLine(@NotNull Vector position) {
        double t = (position.getX() - start.getX()) / direction.getX();
        if (position.getBlockY() == start.getY() + (t * direction.getY()) && position.getBlockZ() == start.getZ() + (t * direction.getZ())) {
            return true;
        }
        return false;
    }

    @NotNull
    public List<Vector> traverse(final double distance, final double accuracy) {
        var positions = new ArrayList<Vector>();
        for (double d = 0.0D; d <= distance; d += accuracy) {
            positions.add(getPostion(d));
        }
        return positions;
    }

    @Nullable
    public Vector positionOfIntersection(@NotNull Vector min, @NotNull Vector max, final double distance, final double accuracy) {
        var positions = traverse(distance, accuracy);
        for (var position : positions) {
            if (intersects(position, min, max)) {
                return position;
            }
        }
        return null;
    }

    public boolean intersects(@NotNull Vector min, @NotNull Vector max, final double distance, final double accuracy) {
        var positions = traverse(distance, accuracy);
        for (var position : positions) {
            if (intersects(position, min, max)) {
                return true;
            }
        }
        return false;
    }

    @Nullable
    public Vector positionOfIntersection(@NotNull SBBoundingBox boundingBox, final double distance, final double accuracy) {
        var positions = traverse(distance, accuracy);
        for (var position : positions) {
            if (intersects(position, boundingBox.getMin(), boundingBox.getMax())) {
                return position;
            }
        }
        return null;
    }

    public boolean intersects(@NotNull SBBoundingBox boundingBox, final double distance, final double accuracy) {
        var positions = traverse(distance, accuracy);
        for (var position : positions) {
            if (intersects(position, boundingBox.getMin(), boundingBox.getMax())) {
                return true;
            }
        }
        return false;
    }

    public static boolean intersects(@NotNull Vector position, @NotNull Vector min, @NotNull Vector max) {
        if (position.getX() < min.getX() || position.getX() > max.getX()) {
            return false;
        } else if (position.getY() < min.getY() || position.getY() > max.getY()) {
            return false;
        } else if (position.getZ() < min.getZ() || position.getZ() > max.getZ()) {
            return false;
        }
        return true;
    }
}