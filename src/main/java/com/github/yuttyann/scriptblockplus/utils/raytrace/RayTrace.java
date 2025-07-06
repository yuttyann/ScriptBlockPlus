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
package com.github.yuttyann.scriptblockplus.utils.raytrace;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.bukkit.FluidCollisionMode;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.Levelled;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.github.yuttyann.scriptblockplus.utils.ItemUtils;
import com.github.yuttyann.scriptblockplus.utils.NMSHelper;
import com.github.yuttyann.scriptblockplus.utils.server.NetMinecraft;
import com.github.yuttyann.scriptblockplus.utils.version.McVersion;

/**
 * ScriptBlockPlus RayTrace クラス
 * @author yuttyann44581, 
 */
public final class RayTrace {

    private final Vector start, direction;

    /**
     * コンストラクタ
     * @param player - プレイヤー
     */
    public RayTrace(@NotNull Player player) {
        this(player.getEyeLocation().toVector(), player.getEyeLocation().getDirection());
    }

    /**
     * コンストラクタ
     * @param start - 開始位置
     * @param direction - 方向
     */
    public RayTrace(@NotNull Vector start, @NotNull Vector direction) {
        this.start = start;
        this.direction = direction;
    }

    /**
     * プレイヤーの射線上に存在するブロックを取得します。
     * @param player - プレイヤー
     * @param maxDistance - 距離
     * @return {@link RayResult} - ブロック
     */
    @Nullable
    public static RayResult rayTraceBlocks(@NotNull Player player, final double maxDistance) {
        if (McVersion.V_1_13_2.isSupported()) {
            var rayTraceResult = player.rayTraceBlocks(maxDistance, FluidCollisionMode.NEVER);
            if (rayTraceResult == null || rayTraceResult.getHitBlock() == null) {
                return null;
            }
            return new RayResult(rayTraceResult.getHitBlock(), rayTraceResult.getHitBlockFace());
        } else {
            if (NetMinecraft.hasNMS()) {
                try {
                    return NMSHelper.rayTraceBlocks(player, maxDistance);
                } catch (ReflectiveOperationException e) {
                    e.printStackTrace();
                }
            } else {
                return new RayTrace(player).rayTraceBlock(player.getWorld(), FluidCollisionMode.NEVER, maxDistance, 0.01D);
            }
            return null;
        }
    }

    // 仮実装
    @Nullable
    private RayResult rayTraceBlock(@NotNull World world, @NotNull FluidCollisionMode fluidCollisionMode, final double maxDistance, final double accuracy) {
        if (direction.lengthSquared() <= 0.0D || maxDistance < 0.0D) {
            return null;
        }
        var oldBlock = (Block) null;
        Vector start = new Vector(), direction = new Vector();
        for (var d = 0.0D; d <= maxDistance; d += accuracy) {
            var position = copy(start, this.start).add(copy(direction, this.direction).multiply(d));
            var block = world.getBlockAt(position.getBlockX(), position.getBlockY(), position.getBlockZ());
            if (Objects.equals(oldBlock, oldBlock = block) || ItemUtils.isAIR(block.getType())) {
                continue;
            }
            if (fluidCollisionMode != FluidCollisionMode.NEVER && block.getBlockData() instanceof Levelled) {
                if (fluidCollisionMode == FluidCollisionMode.ALWAYS) {
                    continue;
                }
                var fluid = (Levelled) block;
                if (fluid.getLevel() == fluid.getMaximumLevel()) {
                    continue;
                }
            }
            var hitResult = block.getBoundingBox().rayTrace(this.start, this.direction, maxDistance);
            if (hitResult != null) {
                return new RayResult(block, hitResult.getHitBlockFace());
            }
        }
        return null;
    }

    /**
     * プレイヤー射線上に存在するブロックの一覧を取得します。
     * @param player - プレイヤー
     * @param distance - 距離
     * @param accuracy - 精度
     * @param square - 正方形のブロックとして判定する場合は{@code true}
     * @return {@link Set}&lt;{@link Block}&gt; - ブロックの一覧
     */
    @NotNull
    public static Set<Block> rayTraceBlocks(@NotNull Player player, final double distance, final double accuracy, final boolean square) {
        var world = player.getWorld();
        var start = new Vector();
        var direction = new Vector();
        var rayTrace = new RayTrace(player);
        var boundingBox = new SBBoundingBox();
        var result = new LinkedHashSet<Block>();
        for (var d = 0.0D; d <= distance; d += accuracy) {
            var position = copy(start, rayTrace.start).add(copy(direction, rayTrace.direction).multiply(d));
            var block = world.getBlockAt(position.getBlockX(), position.getBlockY(), position.getBlockZ());
            boundingBox.setBlock(block, square);
            if(intersects(position, boundingBox)) {
                result.add(block);
            }
        }
        return result;
    }

    @NotNull
    private static Vector copy(@NotNull Vector from, @NotNull Vector to) {
        from.setX(to.getX());
        from.setY(to.getY());
        from.setZ(to.getZ());
        return from;
    }

    /**
     * 指定した距離の位置を取得する。
     * @param distance - 距離
     * @return {@link Vector} - 位置
     */
    @NotNull
    public Vector getPostion(final double distance) {
        var vector1 = new Vector(start.getX(), start.getY(), start.getZ());
        var vector2 = new Vector(direction.getX(), direction.getY(), direction.getZ());
        return vector1.add(vector2.multiply(distance));
    }

    /**
     * 指定した位置が含まれている場合は{@code true}を返します。
     * @param position - 位置
     * @return {@code boolean} - 指定した位置が含まれている場合は{@code true}
     */
    public boolean isOnLine(@NotNull Vector position) {
        double t = (position.getX() - start.getX()) / direction.getX();
        if (position.getBlockY() == start.getY() + (t * direction.getY()) && position.getBlockZ() == start.getZ() + (t * direction.getZ())) {
            return true;
        }
        return false;
    }

    /**
     * レイトレース上の全ての位置を取得します。
     * @param distance - 距離
     * @param accuracy - 精度
     * @return {@link List}&lt;{@link Vector}&gt; - 全ての位置
     */
    @NotNull
    public List<Vector> traverse(final double distance, final double accuracy) {
        var positions = new ArrayList<Vector>(16);
        for (double d = 0.0D; d <= distance; d += accuracy) {
            positions.add(getPostion(d));
        }
        return positions;
    }

    /**
     * 現在のレイトレースに対する交差点検出を行います。
     * @param min - 最小値
     * @param max - 最大値
     * @param distance - 距離
     * @param accuracy - 精度
     * @return {@link Vector} - 交差点
     */
    @Nullable
    public Vector positionOfIntersection(@NotNull Vector min, @NotNull Vector max, final double distance, final double accuracy) {
        return positionOfIntersection(new SBBoundingBox(min, max), distance, accuracy);
    }

    /**
     * 現在のレイトレースに対する交差点検出を行います。
     * @param boundingBox - バウンディングボックス
     * @param distance - 距離
     * @param accuracy - 精度
     * @return {@link Vector} - 交差点
     */
    @Nullable
    public Vector positionOfIntersection(@NotNull SBBoundingBox boundingBox, final double distance, final double accuracy) {
        var positions = traverse(distance, accuracy);
        for(int i = 0, l = positions.size(); i < l; i++) {
            var position = positions.get(i);
            if (intersects(position, boundingBox)) {
                return position;
            }
        }
        return null;
    }

    /**
     * 現在のレイトレースに対する交差点検出
     * @param min - 最小値
     * @param max - 最大値
     * @param distance - 距離
     * @param accuracy - 精度
     * @return {@code boolean} - 交差点検出した場合は{@code true}
     */
    public boolean intersects(@NotNull Vector min, @NotNull Vector max, final double distance, final double accuracy) {
        return intersects(new SBBoundingBox(min, max), distance, accuracy);
    }

    /**
     * 現在のレイトレースの交差点検出した場合は{@code true}を返します。
     * @param boundingBox - バウンディングボックス
     * @param distance - 距離
     * @param accuracy - 精度
     * @return {@code boolean} - 交差点検出した場合は{@code true}
     */
    public boolean intersects(@NotNull SBBoundingBox boundingBox, final double distance, final double accuracy) {
        var positions = traverse(distance, accuracy);
        for(int i = 0, l = positions.size(); i < l; i++) {
            if (intersects(positions.get(i), boundingBox)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 交差点検出した場合は{@code true}を返します。
     * @param position - 位置
     * @param boundingBox - バウンディングボックス
     * @return {@code boolean} - 交差点検出した場合は{@code true}
     */
    public static boolean intersects(@NotNull Vector position, @NotNull SBBoundingBox boundingBox) {
        if (position.getX() < boundingBox.getMinX() || position.getX() > boundingBox.getMaxX()) {
            return false;
        } else if (position.getY() < boundingBox.getMinY() || position.getY() > boundingBox.getMaxY()) {
            return false;
        } else if (position.getZ() < boundingBox.getMinZ() || position.getZ() > boundingBox.getMaxZ()) {
            return false;
        }
        return true;
    }
}