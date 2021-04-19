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
package com.github.yuttyann.scriptblockplus.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.ProxiedCommandSender;
import org.bukkit.command.RemoteConsoleCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.CommandMinecart;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.raytrace.RayResult;

import static com.github.yuttyann.scriptblockplus.enums.reflection.PackageType.*;

/**
 * ScriptBlockPlus NMSHelper クラス
 * @author yuttyann44581
 */
public final class NMSHelper {

    private static final double R = 0.017453292D;
    private static final Object[] ARGMENT_ENTITY = { false, false };
    private static final Class<?>[] ARGMENT_ENTITY_CLASS = { boolean.class, boolean.class };
    private static final Class<?>[] COMMAND_LISTENER_CLASS = { CommandSender.class };

    public static void sendPacket(@NotNull Object packet) throws ReflectiveOperationException {
        for (var player : Bukkit.getOnlinePlayers()) {
            sendPacket(player, packet);
        }
    }

    public static void sendPacket(@NotNull Player player, @NotNull Object packet) throws ReflectiveOperationException {
        var handle = CB_ENTITY.invokeMethod(player, "CraftPlayer", "getHandle");
        var connection = NMS.getFieldValue("EntityPlayer", "playerConnection", handle);
        NMS.getMethod("PlayerConnection", "sendPacket", NMS.getClass("Packet")).invoke(connection, packet);
    }

    public static void sendPackets(@NotNull Player player, @NotNull Object... packets) throws ReflectiveOperationException {
        var handle = CB_ENTITY.invokeMethod(player, "CraftPlayer", "getHandle");
        var connection = NMS.getFieldValue("EntityPlayer", "playerConnection", handle);
        var sendPacket = NMS.getMethod("PlayerConnection", "sendPacket", NMS.getClass("Packet"));
        var tempArgs = new Object[] { null };
        for (var packet : packets) {
            tempArgs[0] = packet;
            sendPacket.invoke(connection, tempArgs);
        }
    }

    public static void sendActionBar(@NotNull Player player, @NotNull String text) throws ReflectiveOperationException {
        var component = NMS.invokeMethod(null, "IChatBaseComponent$ChatSerializer", "a", "{\"text\": \"" + text + "\"}");
        var classes = new Class<?>[] { NMS.getClass("IChatBaseComponent"), byte.class };
        var value = (Object) null;
        if (Utils.isCBXXXorLater("1.12")) {
            classes[1] = (value = NMS.getEnumValueOf("ChatMessageType", "GAME_INFO")).getClass();
        } else {
            value = (byte) 2;
        }
        sendPacket(player, NMS.getConstructor("PacketPlayOutChat", classes).newInstance(component, value));
    }

    @Nullable
    public static RayResult rayTraceBlocks(@NotNull Player player, final double distance) throws ReflectiveOperationException {
        var location = player.getLocation();
        double x = location.getX();
        double y = location.getY() + player.getEyeHeight();
        double z = location.getZ();
        float yaw = location.getYaw();
        float pitch = location.getPitch();
        float f1 = (float) Math.cos((-yaw * R) - Math.PI);
        float f2 = (float) Math.sin((-yaw * R) - Math.PI);
        float f3 = (float) -Math.cos(-pitch * R);
        float f4 = (float) Math.sin(-pitch * R);
        float f5 = f2 * f3;
        float f6 = f1 * f3;
        var vec3d1 = newVec3D(x, y, z);
        var vec3d2 = newVec3D(x + (f5 * distance), y + (f4 * distance), z + (f6 * distance));
        var arguments = (Object[]) null;
        if (Utils.isCBXXXorLater("1.13")) {
            var NEVER = NMS.getEnumValueOf("FluidCollisionOption", "NEVER");
            arguments = new Object[] { vec3d1, vec3d2, NEVER, false, false };
        } else {
            arguments = new Object[] { vec3d1, vec3d2, false };
        }
        var world = player.getWorld();
        var nmsWorld = CB.invokeMethod(world, "CraftWorld", "getHandle");
        var rayTrace = NMS.invokeMethod(nmsWorld, "World", "rayTrace", arguments);
        if (rayTrace != null) {
            var direction = NMS.getFieldValue("MovingObjectPosition", "direction", rayTrace);
            var position = NMS.invokeMethod(rayTrace, "MovingObjectPosition", "a");
            int bx = (int) NMS.invokeMethod(position, "BaseBlockPosition", "getX");
            int by = (int) NMS.invokeMethod(position, "BaseBlockPosition", "getY");
            int bz = (int) NMS.invokeMethod(position, "BaseBlockPosition", "getZ");
            return new RayResult(world.getBlockAt(bx, by, bz), BlockFace.valueOf(((Enum<?>) direction).name()));
        }
        return null;
    }

    @NotNull
    public static List<Entity> selectEntities(@NotNull CommandSender sender, @NotNull Location location, @NotNull String selector) throws ReflectiveOperationException {
        var vector = newVec3D(location.getBlockX() + 0.5D, location.getBlockY(), location.getBlockZ() + 0.5D);
        var reader = MJN.newInstance("StringReader", selector);
        var entity = NMS.newInstance(true, "ArgumentEntity", ARGMENT_ENTITY_CLASS, ARGMENT_ENTITY);
        var wrapper = NMS.invokeMethod(getICommandListener(sender), "CommandListenerWrapper", "a", vector);
        var parse = NMS.invokeMethod(entity, "ArgumentEntity", "parse", Utils.isCBXXXorLater("1.13.2") ? new Object[] { reader, true } : new Object[] { reader });
        var nmsList = (List<?>) NMS.invokeMethod(parse, "EntitySelector", Utils.isCBXXXorLater("1.14") ? "getEntities" : "b", wrapper);
        var resultList = new ArrayList<Entity>(nmsList.size());
        for (var nmsEntity : nmsList) {
            resultList.add((Entity) NMS.invokeMethod(nmsEntity, "Entity", "getBukkitEntity"));
        }
        return resultList;
    }

    @NotNull
    public static Object getICommandListener(@NotNull CommandSender sender) throws ReflectiveOperationException {
        if (Utils.isCBXXXorLater("1.13.2")) {
            return CB_COMMAND.invokeMethod(false, (Object) null, "VanillaCommandWrapper", "getListener", COMMAND_LISTENER_CLASS, sender);
        }
        if (sender instanceof SBPlayer) {
            sender = ((SBPlayer) sender).getPlayer();
        }
        if (sender instanceof Player) {
            var entity = CB_ENTITY.invokeMethod(sender, "CraftPlayer", "getHandle");
            return NMS.invokeMethod(entity, "Entity", "getCommandListener");
        } else if (sender instanceof BlockCommandSender) {
            return CB_COMMAND.invokeMethod(sender, "CraftBlockCommandSender", "getWrapper");
        } else if (sender instanceof CommandMinecart) {
            var cart = CB_ENTITY.invokeMethod(sender, "CraftMinecartCommand", "getHandle");
            var command = NMS.invokeMethod(cart, "EntityMinecartCommandBlock", "getCommandBlock");
            return NMS.invokeMethod(command, "CommandBlockListenerAbstract", "getWrapper");
        } else if (sender instanceof RemoteConsoleCommandSender) {
            var server = NMS.invokeMethod(null, "MinecraftServer", "getServer");
            var remote = NMS.getFieldValue("DedicatedServer", "remoteControlCommandListener", server);
            return NMS.invokeMethod(remote, "RemoteControlCommandListener", "f");
        } else if (sender instanceof ConsoleCommandSender) {
            var server = CB.invokeMethod(sender.getServer(), "CraftServer", "getServer");
            return NMS.invokeMethod(server, "MinecraftServer", "getServerCommandListener");
        } else if (sender instanceof ProxiedCommandSender) {
            return CB_COMMAND.invokeMethod(sender, "ProxiedNativeCommandSender", "getHandle");
        }
        throw new IllegalArgumentException("Cannot make " + sender + " a vanilla command listener");
    }

    @NotNull
    public static Object getAxisAlignedBB(@NotNull Block block) throws ReflectiveOperationException {
        var world = CB.invokeMethod(block.getWorld(), "CraftWorld", "getHandle");
        var position = NMS.newInstance("BlockPosition", block.getX(), block.getY(), block.getZ());
        var blockData = NMS.invokeMethod(world, "WorldServer", "getType", position);
        if (Utils.isCBXXXorLater("1.13")) {
            var name = Utils.getPackageVersion().equals("v1_13_R2") ? "i" : "g";
            var voxelShape = NMS.getMethod("IBlockData", name, NMS.getClass("IBlockAccess"), position.getClass());
            return NMS.invokeMethod(voxelShape.invoke(blockData, world, position), "VoxelShape", "a");
        } else {
            var name = Utils.isCBXXXorLater("1.11") ? "b" : "a";
            var axisAlignedBB = NMS.getMethod("Block", name, NMS.getClass("IBlockData"), NMS.getClass("IBlockAccess"), position.getClass());
            return axisAlignedBB.invoke(NMS.invokeMethod(blockData, "IBlockData", "getBlock"), blockData, world, position);
        }
    }

    @NotNull
    public static Map<String, Material> getItemRegistry() throws ReflectiveOperationException {
        var materials = new HashMap<String, Material>();
        var registry = NMS.getFieldValue("Item", "REGISTRY", null);
        var registryMap = (Map<?, ?>) NMS.getFieldValue(true, "RegistrySimple", "c", registry);
        var bukkitMaterial = CB_UTIL.getMethod("CraftMagicNumbers", "getMaterial", NMS.getClass("Item"));
        for (var entry : registryMap.entrySet()) {
            materials.put(getKey(entry.getKey()), (Material) bukkitMaterial.invoke(null, entry.getValue()));
        }
        return materials;
    }

    @NotNull
    private static String getKey(@NotNull Object minecraftKey) throws ReflectiveOperationException {
        var name = Utils.isCBXXXorLater("1.12") ? "getKey" : "a";
        return (String) NMS.invokeMethod(minecraftKey, "MinecraftKey", name);
    }

    @NotNull
    public static Object newVec3D(double x, double y, double z) throws ReflectiveOperationException {
        return NMS.newInstance("Vec3D", x, y, z);
    }
}