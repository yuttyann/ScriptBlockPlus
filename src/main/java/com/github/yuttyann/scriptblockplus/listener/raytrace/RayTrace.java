package com.github.yuttyann.scriptblockplus.listener.raytrace;

import java.util.LinkedHashSet;
import java.util.Set;

import com.github.yuttyann.scriptblockplus.enums.reflection.PackageType;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
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
    public RayResult rayTraceBlocks(@NotNull Player player, final double distance) {
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
                var arguments = (Object[]) null;
                if (Utils.isCBXXXorLater("1.13")) {
                    var NEVER = PackageType.NMS.getEnumValueOf("FluidCollisionOption", "NEVER");
                    arguments = new Object[] { vec3d1, vec3d2, NEVER, false, false };
                } else {
                    arguments = new Object[] { vec3d1, vec3d2, false };
                }
                var nmsWorld = PackageType.CB.invokeMethod(world, "CraftWorld", "getHandle");
                var rayTrace = PackageType.NMS.invokeMethod(nmsWorld, "World", "rayTrace", arguments);
                if (rayTrace != null) {
                    var position = PackageType.NMS.invokeMethod(rayTrace, "MovingObjectPosition", "a");
                    int x = (int) PackageType.NMS.invokeMethod(position, "BaseBlockPosition", "getX");
                    int y = (int) PackageType.NMS.invokeMethod(position, "BaseBlockPosition", "getY");
                    int z = (int) PackageType.NMS.invokeMethod(position, "BaseBlockPosition", "getZ");
                    return new RayResult(world.getBlockAt(x, y, z), notchToBlockFace(rayTrace));
                }
            } catch (ReflectiveOperationException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @NotNull
    public Set<Location> rayTraceBlocks(@NotNull Player player, final double distance, final double raySize, final boolean square) {
        var rayTrace = new AdvancedRayTrace(player);
        var locations = new LinkedHashSet<Location>();
        for(var position : rayTrace.traverse(distance, raySize)) {
            var location = position.toLocation(world);
            if(rayTrace.intersects(new SBBoundingBox(world.getBlockAt(location), square), distance, raySize)){
                locations.add(location);
            }
        }
        return locations;
    }

    @NotNull
    private BlockFace notchToBlockFace(@NotNull Object rayTrace) throws ReflectiveOperationException {
        var direction = (Enum<?>) PackageType.NMS.getField("MovingObjectPosition", "direction").get(rayTrace);
        return direction == null ? BlockFace.SELF : BlockFace.valueOf(direction.name());
    }
}