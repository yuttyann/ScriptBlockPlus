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

import static com.github.yuttyann.scriptblockplus.utils.server.minecraft.Minecraft.*;
import static com.github.yuttyann.scriptblockplus.utils.version.McVersion.*;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.ProxiedCommandSender;
import org.bukkit.command.RemoteConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.CommandMinecart;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.github.yuttyann.scriptblockplus.utils.raytrace.RayResult;
import com.github.yuttyann.scriptblockplus.utils.server.CraftBukkit;
import com.github.yuttyann.scriptblockplus.utils.server.NetMinecraft;
import com.github.yuttyann.scriptblockplus.utils.server.minecraft.Minecraft;
import com.github.yuttyann.scriptblockplus.utils.version.McVersion;

/**
 * ScriptBlockPlus NMSHelper クラス
 * @author yuttyann44581
 */
public final class NMSHelper {

    private static final double R = 0.017453292D;

    public static int getPing(@NotNull Player player) throws ReflectiveOperationException {
        return NetMinecraft.SERVER_LEVEL.getField("EntityPlayer", NetMinecraft.isLegacy() ? "ping" : "e").getInt(getServerPlayer(player));
    }

    @NotNull
    public static Integer getContainerId(@NotNull Object serverPlayer, @NotNull Object containerMenu) throws ReflectiveOperationException {
        if (V_1_14.isSupported()) {
            return Minecraft.getContainerId(containerMenu);
        }
        return Minecraft.getNextContainerId(serverPlayer);
    }

    public static void setActiveContainerId(@NotNull Object containerMenu, @NotNull Integer containerId) throws ReflectiveOperationException {
        if (V_1_14.isUnSupported()) {
            NetMinecraft.LEGACY_PATH.setFieldValue("Container", "windowId", containerMenu, containerId);
        }
    }

    @NotNull
    public static Object getICommandListener(@NotNull CommandSender sender) throws ReflectiveOperationException {
        if (V_1_13_2.isSupported()) {
            return CraftBukkit.COMMAND.invokeMethod(false, (Object) null, "VanillaCommandWrapper", "getListener", new Class<?>[] { CommandSender.class }, sender);
        }
        if (sender instanceof Player) {
            var entity = CraftBukkit.ENTITY.invokeMethod(sender, "CraftPlayer", "getHandle");
            return NetMinecraft.LEGACY_PATH.invokeMethod(entity, "Entity", "getCommandListener");
        } else if (sender instanceof BlockCommandSender) {
            return CraftBukkit.COMMAND.invokeMethod(sender, "CraftBlockCommandSender", "getWrapper");
        } else if (sender instanceof CommandMinecart) {
            var cart = CraftBukkit.ENTITY.invokeMethod(sender, "CraftMinecartCommand", "getHandle");
            var command = NetMinecraft.LEGACY_PATH.invokeMethod(cart, "EntityMinecartCommandBlock", "getCommandBlock");
            return NetMinecraft.LEGACY_PATH.invokeMethod(command, "CommandBlockListenerAbstract", "getWrapper");
        } else if (sender instanceof RemoteConsoleCommandSender) {
            var server = NetMinecraft.LEGACY_PATH.invokeMethod(null, "MinecraftServer", "getServer");
            var remote = NetMinecraft.LEGACY_PATH.getFieldValue("DedicatedServer", "remoteControlCommandListener", server);
            return NetMinecraft.LEGACY_PATH.invokeMethod(remote, "RemoteControlCommandListener", "f");
        } else if (sender instanceof ConsoleCommandSender) {
            var server = CraftBukkit.CRAFTBUKKIT.invokeMethod(sender.getServer(), "CraftServer", "getServer");
            return NetMinecraft.LEGACY_PATH.invokeMethod(server, "MinecraftServer", "getServerCommandListener");
        } else if (sender instanceof ProxiedCommandSender) {
            return CraftBukkit.COMMAND.invokeMethod(sender, "ProxiedNativeCommandSender", "getHandle");
        }
        throw new IllegalArgumentException("Cannot make " + sender + " a vanilla command listener");
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
        var vec3d1 = Minecraft.newVec3(x, y, z);
        var vec3d2 = Minecraft.newVec3(x + (f5 * distance), y + (f4 * distance), z + (f6 * distance));
        var arguments = (Object[]) null;
        if (McVersion.V_1_13.isSupported()) {
            arguments = new Object[] { vec3d1, vec3d2, NetMinecraft.NET_MINECRAFT.getEnumValueOf("FluidCollisionOption", "NEVER"), false, false };
        } else {
            arguments = new Object[] { vec3d1, vec3d2, false };
        }
        var world = player.getWorld();
        var nmsWorld = Minecraft.getServerLevel(world);
        var rayTrace = NetMinecraft.NET_MINECRAFT.invokeMethod(nmsWorld, "World", "rayTrace", arguments);
        if (rayTrace != null) {
            var direction = NetMinecraft.NET_MINECRAFT.getFieldValue("MovingObjectPosition", "direction", rayTrace);
            var position = NetMinecraft.NET_MINECRAFT.invokeMethod(rayTrace, "MovingObjectPosition", "a");
            int bx = (int) NetMinecraft.NET_MINECRAFT.invokeMethod(position, "BaseBlockPosition", "getX");
            int by = (int) NetMinecraft.NET_MINECRAFT.invokeMethod(position, "BaseBlockPosition", "getY");
            int bz = (int) NetMinecraft.NET_MINECRAFT.invokeMethod(position, "BaseBlockPosition", "getZ");
            return new RayResult(world.getBlockAt(bx, by, bz), BlockFace.valueOf(((Enum<?>) direction).name()));
        }
        return null;
    }

    @NotNull
    public static Object getAxisAlignedBB(@NotNull Block block) throws ReflectiveOperationException {
        var world = CraftBukkit.CRAFTBUKKIT.invokeMethod(block.getWorld(), "CraftWorld", "getHandle");
        var position = NetMinecraft.NET_MINECRAFT.newInstance("BlockPosition", block.getX(), block.getY(), block.getZ());
        var blockData = NetMinecraft.NET_MINECRAFT.invokeMethod(world, "WorldServer", "getType", position);
        if (McVersion.V_1_13.isSupported()) {
            var name = CraftBukkit.getLegacyPackageVersion().equals("v1_13_R2") ? "i" : "g";
            var voxelShape = NetMinecraft.NET_MINECRAFT.getMethod("IBlockData", name, NetMinecraft.NET_MINECRAFT.getClass("IBlockAccess"), position.getClass());
            return NetMinecraft.NET_MINECRAFT.invokeMethod(voxelShape.invoke(blockData, world, position), "VoxelShape", "a");
        } else {
            var name = McVersion.V_1_11.isSupported() ? "b" : "a";
            var axisAlignedBB = NetMinecraft.NET_MINECRAFT.getMethod("Block", name, NetMinecraft.NET_MINECRAFT.getClass("IBlockData"), NetMinecraft.NET_MINECRAFT.getClass("IBlockAccess"), position.getClass());
            return axisAlignedBB.invoke(NetMinecraft.NET_MINECRAFT.invokeMethod(blockData, "IBlockData", "getBlock"), blockData, world, position);
        }
    }

    @NotNull
    public static Map<String, Material> getItemRegistry() throws ReflectiveOperationException {
        var materials = new HashMap<String, Material>();
        var registry = NetMinecraft.NET_MINECRAFT.getFieldValue("Item", "REGISTRY", null);
        var registryMap = (Map<?, ?>) NetMinecraft.NET_MINECRAFT.getFieldValue(true, "RegistrySimple", "c", registry);
        var bukkitMaterial = CraftBukkit.UTIL.getMethod("CraftMagicNumbers", "getMaterial", NetMinecraft.NET_MINECRAFT.getClass("Item"));
        for (var entry : registryMap.entrySet()) {
            materials.put(getKey(entry.getKey()), (Material) bukkitMaterial.invoke(null, entry.getValue()));
        }
        return materials;
    }

    public static void sendActionBar(@NotNull Player player, @NotNull String text) throws ReflectiveOperationException {
        var component = NetMinecraft.NET_MINECRAFT.invokeMethod(null, "IChatBaseComponent$ChatSerializer", "a", "{\"text\": \"" + text + "\"}");
        var classes = new Class<?>[] { NetMinecraft.NET_MINECRAFT.getClass("IChatBaseComponent"), byte.class };
        var value = (Object) null;
        if (McVersion.V_1_12.isSupported()) {
            classes[1] = (value = NetMinecraft.NET_MINECRAFT.getEnumValueOf("ChatMessageType", "GAME_INFO")).getClass();
        } else {
            value = (byte) 2;
        }
        var serverPlayer = Minecraft.getServerPlayer(player);
        Minecraft.sendPacket(serverPlayer, NetMinecraft.NET_MINECRAFT.getConstructor("PacketPlayOutChat", classes).newInstance(component, value));
    }

    @NotNull
    private static String getKey(@NotNull Object minecraftKey) throws ReflectiveOperationException {
        return (String) NetMinecraft.NET_MINECRAFT.invokeMethod(minecraftKey, "MinecraftKey", McVersion.V_1_12.isSupported() ? "getKey" : "a");
    }
}