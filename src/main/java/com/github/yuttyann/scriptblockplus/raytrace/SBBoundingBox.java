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
package com.github.yuttyann.scriptblockplus.raytrace;

import com.github.yuttyann.scriptblockplus.enums.reflection.PackageType;
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

    private double minX, minY, minZ;
    private double maxX, maxY, maxZ;

    public SBBoundingBox() {
        setXYZ(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
    }

    public SBBoundingBox(@NotNull Vector min, @NotNull Vector max) {
        setVector(min, max);
    }

    public SBBoundingBox(@NotNull Block block, final boolean square) {
        setBlock(block, square);
    }

    public double getMinX() {
        return minX;
    }

    public double getMinY() {
        return minY;
    }

    public double getMinZ() {
        return minZ;
    }

    public double getMaxX() {
        return maxX;
    }

    public double getMaxY() {
        return maxY;
    }

    public double getMaxZ() {
        return maxZ;
    }

    public void setBlock(@NotNull Block block, final boolean square) {
        if (square || ItemUtils.isAIR(block.getType())) {
            setSquare(block);
        } else {
            if (Utils.isCBXXXorLater("1.13.2")) {
                var box = block.getBoundingBox();
                double minX = box.getMinX(), minY = box.getMinY(), minZ = box.getMinZ();
                double maxX = box.getMaxX(), maxY = box.getMaxY(), maxZ = box.getMaxZ();
                setXYZ(minX, minY, minZ, maxX, maxY, maxZ);
            } else {
                try {
                    setAxisAlignedBB(block);
                } catch (ReflectiveOperationException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void setAxisAlignedBB(@NotNull Block block) throws ReflectiveOperationException {
        if (PackageType.HAS_NMS) {
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

    public void setSquare(@NotNull Block block) {
        int x = block.getX(), y = block.getY(), z = block.getZ();
        setXYZ(x, y, z, x + 1.0D, y + 1.0D, z + 1.0D);
    }

    public void setVector(@NotNull Vector min, @NotNull Vector max) {
        setXYZ(min.getX(), min.getY(), min.getZ(), max.getX(), max.getY(), max.getZ());
    }

    public void setXYZ(double x1, double y1, double z1, double x2, double y2, double z2) {
        this.minX = Math.min(x1, x2);
        this.minY = Math.min(y1, y2);
        this.minZ = Math.min(z1, z2);
        this.maxX = Math.max(x1, x2);
        this.maxY = Math.max(y1, y2);
        this.maxZ = Math.max(z1, z2);
    }
}