package com.github.yuttyann.scriptblockplus.listener.raytrace;

import com.github.yuttyann.scriptblockplus.enums.reflection.PackageType;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import org.bukkit.FluidCollisionMode;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
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
            var rayTraceResult = player.rayTraceBlocks(distance, FluidCollisionMode.NEVER);
            if (rayTraceResult != null && rayTraceResult.getHitBlock() != null) {
                return new RayResult(rayTraceResult.getHitBlock(), rayTraceResult.getHitBlockFace());
            }
        } else {
            var eyeLocation = player.getEyeLocation();
            var direction = new SBVector(eyeLocation.getDirection()).normalize().multiply(distance);
            var start = new SBVector(eyeLocation.toVector());
            var end = start.add(direction.getX(), direction.getY(), direction.getZ());
            try {
                var vec3d1 = start.toNMSVec3D();
                var vec3d2 = end.toNMSVec3D();
                var args = new Object[] { vec3d1, vec3d2, false };
                if (Utils.isCBXXXorLater("1.13")) {
                    var NEVER = PackageType.NMS.getEnumValueOf("FluidCollisionOption", "NEVER");
                    args = new Object[] { vec3d1, vec3d2, NEVER, false, false };
                }
                var nmsWorld = PackageType.CB.invokeMethod(world, "CraftWorld", "getHandle");
                var rayTrace = PackageType.NMS.invokeMethod(nmsWorld, "World", "rayTrace", args);
                if (rayTrace != null) {
                    var pos = PackageType.NMS.invokeMethod(rayTrace, "MovingObjectPosition", "a");
                    int bx = (int) PackageType.NMS.invokeMethod(pos, "BaseBlockPosition", "getX");
                    int by = (int) PackageType.NMS.invokeMethod(pos, "BaseBlockPosition", "getY");
                    int bz = (int) PackageType.NMS.invokeMethod(pos, "BaseBlockPosition", "getZ");
                    return new RayResult(world.getBlockAt(bx, by, bz), notchToBlockFace(getDirection(rayTrace)));
                }
            } catch (ReflectiveOperationException e) {
                e.printStackTrace();
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