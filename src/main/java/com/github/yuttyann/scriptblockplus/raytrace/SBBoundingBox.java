package com.github.yuttyann.scriptblockplus.raytrace;

import com.github.yuttyann.scriptblockplus.enums.reflection.PackageType;
import com.github.yuttyann.scriptblockplus.utils.Utils;

import org.bukkit.block.Block;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

/**
 * ScriptBlockPlus SBBoundingBox クラス
 * @author yuttyann44581
 */
public class SBBoundingBox {

    private Vector min;
    private Vector max;

    public SBBoundingBox(@NotNull Vector min, @NotNull Vector max) {
        this.min = min;
        this.max = max;
    }

    public SBBoundingBox(@NotNull Block block, final boolean square) {
        if (square || block.getType().name().contains("AIR")) {
            setSquare(block);
        } else {
            if (Utils.isCBXXXorLater("1.13.2")) {
                var boundingBox = block.getBoundingBox();
                this.min = boundingBox.getMin();
                this.max = boundingBox.getMax();
            } else {
                try {
                    var axisAlignedBB = getAxisAlignedBB(block);
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
                        setVector(x + minX, y + minY, z + minZ, x + maxX, y + maxY, z + maxZ);
                    }
                } catch (ReflectiveOperationException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void setSquare(@NotNull Block block) {
        int x = block.getX(), y = block.getY(), z = block.getZ();
        setVector(x, y, z, x + 1.0D, y + 1.0D, z + 1.0D);
    }

    private void setVector(double x1, double y1, double z1, double x2, double y2, double z2) {
        this.min = new Vector(Math.min(x1, x2), Math.min(y1, y2), Math.min(z1, z2));
        this.max = new Vector(Math.max(x1, x2), Math.max(y1, y2), Math.max(z1, z2));
    }

    @NotNull
    private Object getAxisAlignedBB(@NotNull Block block) throws ReflectiveOperationException {
        var world = PackageType.CB.invokeMethod(block.getWorld(), "CraftWorld", "getHandle");
        var position = PackageType.NMS.newInstance("BlockPosition", block.getX(), block.getY(), block.getZ());
        var blockData = PackageType.NMS.invokeMethod(world, "WorldServer", "getType", position);
        if (Utils.isCBXXXorLater("1.13")) {
            var name = PackageType.getVersionName().equals("v1_13_R2") ? "i" : "g";
            var getVoxelShape = PackageType.NMS.getMethod("IBlockData", name, PackageType.NMS.getClass("IBlockAccess"), position.getClass());
            return PackageType.NMS.invokeMethod(getVoxelShape.invoke(blockData, world, position), "VoxelShape", "a");
        } else {
            var name = Utils.isCBXXXorLater("1.11") ? "b" : "a";
            var getAxisAlignedBB = PackageType.NMS.getMethod("Block", name, PackageType.NMS.getClass("IBlockData"), PackageType.NMS.getClass("IBlockAccess"), position.getClass());
            return getAxisAlignedBB.invoke(PackageType.NMS.invokeMethod(blockData, "IBlockData", "getBlock"), blockData, world, position);
        }
    }
    
    @NotNull
    public Vector getMin() {
        return min;
    }

    @NotNull
    public Vector getMax() {
        return max;
    }
}