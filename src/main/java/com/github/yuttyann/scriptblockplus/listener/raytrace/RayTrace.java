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

    private final World world;

    public RayTrace(@NotNull World world) {
        this.world = world;
    }

    @Nullable
    public RayResult rayTrace(@NotNull Player player, final double distance) {
        if (Utils.isCBXXXorLater("1.13.2")) {
            RayTraceResult rayTraceResult = player.rayTraceBlocks(distance, FluidCollisionMode.NEVER);
            if (rayTraceResult != null && rayTraceResult.getHitBlock() != null) {
                return new RayResult(rayTraceResult.getHitBlock(), rayTraceResult.getHitBlockFace());
            }
        } else if (Utils.isPlatform()) {
            Location eyeLocation = player.getEyeLocation();
            SBVector direction = new SBVector(eyeLocation.getDirection()).normalize().multiply(distance);
			SBVector start = new SBVector(eyeLocation.toVector());
            SBVector end = start.add(direction.getX(), direction.getY(), direction.getZ());
            try {
                Object vec3d1 = start.toNMSVec3D();
                Object vec3d2 = end.toNMSVec3D();
                Object[] args = { vec3d1, vec3d2, false };
                if (Utils.isCBXXXorLater("1.13")) {
                    Enum<?> NEVER = PackageType.NMS.getEnumValueOf("FluidCollisionOption", "NEVER");
                    args = new Object[] { vec3d1, vec3d2, NEVER, false, false };
                }
                Object nmsWorld = PackageType.CB.invokeMethod(world, "CraftWorld", "getHandle");
                Object rayTrace = PackageType.NMS.invokeMethod(nmsWorld, "World", "rayTrace", args);
                if (rayTrace != null) {
                    Object pos = PackageType.NMS.invokeMethod(rayTrace, "MovingObjectPosition", "a");
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
        return direction == null ? BlockFace.SELF : BlockFace.valueOf(direction.name());
    }
}