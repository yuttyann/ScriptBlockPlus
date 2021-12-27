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

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.enums.server.NetMinecraft;
import com.github.yuttyann.scriptblockplus.utils.ItemUtils;
import com.github.yuttyann.scriptblockplus.utils.NMSHelper;
import com.github.yuttyann.scriptblockplus.utils.Utils;

import org.bukkit.block.Block;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

/**
 * ScriptBlockPlus SBBoundingBox クラス
 * @author yuttyann44581
 */
public final class SBBoundingBox {

    /**
     * 最小値
     */
    private double minX, minY, minZ;

    /**
     * 最大値
     */
    private double maxX, maxY, maxZ;

    /**
     * コンストラクタ(全ての値が{@code 0})
     */
    public SBBoundingBox() {
        setXYZ(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
    }

    /**
     * コンストラクタ
     * @param min - 最小値
     * @param max - 最大値
     */
    public SBBoundingBox(@NotNull BlockCoords min, @NotNull BlockCoords max) {
        this(min.toVector(), max.toVector());
    }

    /**
     * コンストラクタ
     * @param min - 最小値
     * @param max - 最大値
     */
    public SBBoundingBox(@NotNull Vector min, @NotNull Vector max) {
        setVector(min, max);
    }

    /**
     * コンストラクタ
     * @param block - ブロック
     * @param square - 正方形のブロックとして設定する場合は{@code true}
     */
    public SBBoundingBox(@NotNull Block block, final boolean square) {
        setBlock(block, square);
    }

    /**
     * Xの最小値を取得します。
     * @return {@code double} - X
     */
    public double getMinX() {
        return minX;
    }

    /**
     * Yの最小値を取得します。
     * @return {@code double} - Y
     */
    public double getMinY() {
        return minY;
    }

    /**
     * Zの最小値を取得します。
     * @return {@code double} - Z
     */
    public double getMinZ() {
        return minZ;
    }

    /**
     * Xの最大値を取得します。
     * @return {@code double} - X
     */
    public double getMaxX() {
        return maxX;
    }

    /**
     * Yの最大値を取得します。
     * @return {@code double} - Y
     */
    public double getMaxY() {
        return maxY;
    }

    /**
     * Zの最大値を取得します。
     * @return {@code double} - Z
     */
    public double getMaxZ() {
        return maxZ;
    }

    /**
     * ブロックのヒットボックスを設定します。
     * @param block - ブロック
     * @param square - 正方形のブロックとして設定する場合は{@code true}
     */
    public void setBlock(@NotNull Block block, final boolean square) {
        if (square || ItemUtils.isAIR(block.getType())) {
            setSquare(block);
        } else {
            if (Utils.isCBXXXorLater("1.13.2")) {
                var boundingBox = block.getBoundingBox();
                double minX = boundingBox.getMinX(), minY = boundingBox.getMinY(), minZ = boundingBox.getMinZ();
                double maxX = boundingBox.getMaxX(), maxY = boundingBox.getMaxY(), maxZ = boundingBox.getMaxZ();
                setXYZ(minX, minY, minZ, maxX, maxY, maxZ);
            } else {
                try {
                    // 古いバージョンの場合はNMSを利用して設定
                    setAxisAlignedBB(block);
                } catch (ReflectiveOperationException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * {@code NMS}を利用してブロックのヒットボックスを設定します。
     * @throws ReflectiveOperationException リフレクション関係で例外が発生した際にスローされます。
     * @param block - ブロック
     */
    private void setAxisAlignedBB(@NotNull Block block) throws ReflectiveOperationException {
        if (NetMinecraft.hasNMS()) {
            setSquare(block);
            return;
        }
        var axisAlignedBB = NMSHelper.getAxisAlignedBB(block);
        if (axisAlignedBB == null) {
            setSquare(block);
        } else {
            var fields = axisAlignedBB.getClass().getFields();
            double minX = fields[0].getDouble(axisAlignedBB);
            double minY = fields[1].getDouble(axisAlignedBB);
            double minZ = fields[2].getDouble(axisAlignedBB);
            double maxX = fields[3].getDouble(axisAlignedBB);
            double maxY = fields[4].getDouble(axisAlignedBB);
            double maxZ = fields[5].getDouble(axisAlignedBB);
            int x = block.getX(), y = block.getY(), z = block.getZ(); 
            setXYZ(x + minX, y + minY, z + minZ, x + maxX, y + maxY, z + maxZ);
        }
    }

    /**
     * 正方形のヒットボックスとして設定します。
     * @param block - ブロック
     */
    public void setSquare(@NotNull Block block) {
        int x = block.getX(), y = block.getY(), z = block.getZ();
        setXYZ(x, y, z, x + 1.0D, y + 1.0D, z + 1.0D);
    }

    /**
     * 最小値と最大値を設定します。
     * @param min - 最小値
     * @param max - 最大値
     */
    public void setVector(@NotNull Vector min, @NotNull Vector max) {
        setXYZ(min.getX(), min.getY(), min.getZ(), max.getX(), max.getY(), max.getZ());
    }

    /**
     * 最小値と最大値を設定します。
     * @param x1 - Xの最小値
     * @param y1 - Yの最小値
     * @param z1 - Zの最小値
     * @param x2 - Xの最大値
     * @param y2 - Yの最大値
     * @param z2 - Zの最大値
     */
    public void setXYZ(double x1, double y1, double z1, double x2, double y2, double z2) {
        this.minX = Math.min(x1, x2);
        this.minY = Math.min(y1, y2);
        this.minZ = Math.min(z1, z2);
        this.maxX = Math.max(x1, x2);
        this.maxY = Math.max(y1, y2);
        this.maxZ = Math.max(z1, z2);
    }
}