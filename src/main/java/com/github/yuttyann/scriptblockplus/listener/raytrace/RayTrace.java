package com.github.yuttyann.scriptblockplus.listener.raytrace;

import com.github.yuttyann.scriptblockplus.listener.raytrace.SBBlockIterator.Queue;
import com.github.yuttyann.scriptblockplus.enums.reflection.PackageType;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * ScriptBlockPlus RayTrace クラス
 * @author yuttyann44581
 */
public class RayTrace {

    private static final double R = 0.017453292D;
    private static final String KEY_BPS = Utils.isCBXXXorLater("1.13.2") ? "getBlockPosition" : "a";

    private final World world;

    public RayTrace(@NotNull World world) {
        this.world = world;
    }

    public RayResult rayTrace(@NotNull Player player, double distance) {
        if (Utils.isCBXXXorLater("1.13.2")) {
            RayTraceResult rayTraceResult = player.rayTraceBlocks(distance, FluidCollisionMode.NEVER);
            if (rayTraceResult != null && rayTraceResult.getHitBlock() != null) {
                return new RayResult(rayTraceResult.getHitBlock(), rayTraceResult.getHitBlockFace());
            }
        } else if (Utils.isPlatform()) {
            Location location = player.getLocation();
            double x = location.getX();
            double y = location.getY() + player.getEyeHeight();
            double z = location.getZ();
            float pitch = location.getPitch();
            float yaw = location.getYaw();
            float f1 = (float) Math.cos((-yaw * R) - Math.PI);
            float f2 = (float) Math.sin((-yaw * R) - Math.PI);
            float f3 = (float) -Math.cos(-pitch * R);
            float f4 = (float) Math.sin(-pitch * R);
            float f5 = f2 * f3;
            float f6 = f1 * f3;
            SBVector vector1 = new SBVector(x, y, z);
            SBVector vector2 = vector1.add(f5 * distance, f4 * distance, f6 * distance);
            try {
                Object vec3d1 = vector1.toNMSVec3D();
                Object vec3d2 = vector2.toNMSVec3D();
                Object[] args = { vec3d1, vec3d2, false };
                if (Utils.isCBXXXorLater("1.13")) {
                    Enum<?> NEVER = PackageType.NMS.getEnumValueOf("FluidCollisionOption", "NEVER");
                    args = new Object[] { vec3d1, vec3d2, NEVER, false, false };
                }
                Object nmsWorld = PackageType.CB.invokeMethod(world, "CraftWorld", "getHandle");
                Object rayTrace = PackageType.NMS.invokeMethod(nmsWorld, "World", "rayTrace", args);
                if (rayTrace != null) {
                    Object pos = PackageType.NMS.invokeMethod(rayTrace, "MovingObjectPosition", KEY_BPS);
                    int bx = (int) PackageType.NMS.invokeMethod(pos, "BaseBlockPosition", "getX");
                    int by = (int) PackageType.NMS.invokeMethod(pos, "BaseBlockPosition", "getY");
                    int bz = (int) PackageType.NMS.invokeMethod(pos, "BaseBlockPosition", "getZ");
                    return new RayResult(world.getBlockAt(bx, by, bz), notchToBlockFace(getDirection(rayTrace)));
                }
            } catch (ReflectiveOperationException e) {
                e.printStackTrace();
            }
        } else {
            // 互換性を保つため、"Raytrace"が使えない関係で"BlockIterator"を使用
            SBBlockIterator iterator = new SBBlockIterator(player, 5);
            while (iterator.hasNext()) {
                Queue next = iterator.next();
                Block block = next.getBlock();
                if (block.getType() != Material.AIR && player.getEyeLocation().distanceSquared(block.getLocation()) <= 30.77) {
                    return new RayResult(block, next.getFace());
                }
            }
        }
        return null;
    }

    @Nullable
    private Enum<?> getDirection(@NotNull Object rayTrace) throws ReflectiveOperationException {
        return (Enum<?>) PackageType.NMS.getField("MovingObjectPosition", "direction").get(rayTrace);
    }

    @NotNull
    private BlockFace notchToBlockFace(@Nullable Enum<?> direction) {
        if (direction == null) {
            return BlockFace.SELF;
        }
        switch (direction.ordinal()) {
            case 1:
                return BlockFace.DOWN;
            case 2:
                return BlockFace.UP;
            case 3:
                return BlockFace.NORTH;
            case 4:
                return BlockFace.SOUTH;
            case 5:
                return BlockFace.WEST;
            case 6:
                return BlockFace.EAST;
            default:
                return BlockFace.SELF;
        }
    }
}