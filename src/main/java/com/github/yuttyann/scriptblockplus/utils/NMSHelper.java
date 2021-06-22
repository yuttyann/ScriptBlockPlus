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
import org.bukkit.World;
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
import java.util.UUID;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.enums.server.CraftBukkit;
import com.github.yuttyann.scriptblockplus.enums.server.Mojang;
import com.github.yuttyann.scriptblockplus.enums.server.NetMinecraft;
import com.github.yuttyann.scriptblockplus.raytrace.RayResult;

/**
 * ScriptBlockPlus NMSHelper クラス
 * @author yuttyann44581
 */
public final class NMSHelper {

    private static final double R = 0.017453292D;
    private static final Object[] ARGUMENT_ENTITY = { false, false };
    private static final Class<?>[] ARGUMENT_ENTITY_CLASS = { boolean.class, boolean.class };
    private static final Class<?>[] COMMAND_LISTENER_CLASS = { CommandSender.class };

    public static void sendPacket(@NotNull Object packet) throws ReflectiveOperationException {
        for (var player : Bukkit.getOnlinePlayers()) {
            sendPacket(player, packet);
        }
    }

    public static void sendPacket(@NotNull Player player, @NotNull Object packet) throws ReflectiveOperationException {
        if (player == null || !player.isOnline()) {
            return;
        }
        if (NetMinecraft.isLegacy()) {
            var handle = CraftBukkit.ENTITY.invokeMethod(player, "CraftPlayer", "getHandle");
            var connection = NetMinecraft.LEGACY.getFieldValue("EntityPlayer", "playerConnection", handle);
            NetMinecraft.LEGACY.getMethod("PlayerConnection", "sendPacket", NetMinecraft.LEGACY.getClass("Packet")).invoke(connection, packet);
        } else {
            var handle = CraftBukkit.ENTITY.invokeMethod(player, "CraftPlayer", "getHandle");
            var connection = NetMinecraft.SR_LEVEL.getFieldValue("EntityPlayer", "b", handle);
            NetMinecraft.SR_NETWORK.getMethod("PlayerConnection", "sendPacket", NetMinecraft.NW_PROTOCOL.getClass("Packet")).invoke(connection, packet);
        }
    }

    public static void sendPackets(@NotNull Object... packets) throws ReflectiveOperationException {
        for (var player : Bukkit.getOnlinePlayers()) {
            sendPackets(player, packets);
        }
    }

    public static void sendPackets(@NotNull Player player, @NotNull Object... packets) throws ReflectiveOperationException {
        if (player == null || !player.isOnline()) {
            return;
        }
        for (var packet : packets) {
            sendPacket(player, packet);
        }
    }

    public static void sendActionBar(@NotNull Player player, @NotNull String text) throws ReflectiveOperationException {
        var component = NetMinecraft.LEGACY.invokeMethod(null, "IChatBaseComponent$ChatSerializer", "a", "{\"text\": \"" + text + "\"}");
        var classes = new Class<?>[] { NetMinecraft.LEGACY.getClass("IChatBaseComponent"), byte.class };
        var value = (Object) null;
        if (Utils.isCBXXXorLater("1.12")) {
            classes[1] = (value = NetMinecraft.LEGACY.getEnumValueOf("ChatMessageType", "GAME_INFO")).getClass();
        } else {
            value = (byte) 2;
        }
        sendPacket(player, NetMinecraft.LEGACY.getConstructor("PacketPlayOutChat", classes).newInstance(component, value));
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
            var NEVER = NetMinecraft.LEGACY.getEnumValueOf("FluidCollisionOption", "NEVER");
            arguments = new Object[] { vec3d1, vec3d2, NEVER, false, false };
        } else {
            arguments = new Object[] { vec3d1, vec3d2, false };
        }
        var world = player.getWorld();
        var nmsWorld = CraftBukkit.CB.invokeMethod(world, "CraftWorld", "getHandle");
        var rayTrace = NetMinecraft.LEGACY.invokeMethod(nmsWorld, "World", "rayTrace", arguments);
        if (rayTrace != null) {
            var direction = NetMinecraft.LEGACY.getFieldValue("MovingObjectPosition", "direction", rayTrace);
            var position = NetMinecraft.LEGACY.invokeMethod(rayTrace, "MovingObjectPosition", "a");
            int bx = (int) NetMinecraft.LEGACY.invokeMethod(position, "BaseBlockPosition", "getX");
            int by = (int) NetMinecraft.LEGACY.invokeMethod(position, "BaseBlockPosition", "getY");
            int bz = (int) NetMinecraft.LEGACY.invokeMethod(position, "BaseBlockPosition", "getZ");
            return new RayResult(world.getBlockAt(bx, by, bz), BlockFace.valueOf(((Enum<?>) direction).name()));
        }
        return null;
    }

    @NotNull
    public static List<Entity> selectEntities(@NotNull CommandSender sender, @NotNull Location location, @NotNull String selector) throws ReflectiveOperationException {
        var reader = Mojang.BRIGADIER.newInstance("StringReader", selector);
        var vector = newVec3D(location.getBlockX() + 0.5D, location.getBlockY(), location.getBlockZ() + 0.5D);
        if (NetMinecraft.isLegacy()) {
            var entity = NetMinecraft.LEGACY.newInstance(true, "ArgumentEntity", ARGUMENT_ENTITY_CLASS, ARGUMENT_ENTITY);
            var wrapper = NetMinecraft.LEGACY.invokeMethod(getICommandListener(sender), "CommandListenerWrapper", "a", vector);
            var parse = NetMinecraft.LEGACY.invokeMethod(entity, "ArgumentEntity", "parse", Utils.isCBXXXorLater("1.13.2") ? new Object[] { reader, true } : new Object[] { reader });
            var nmsList = (List<?>) NetMinecraft.LEGACY.invokeMethod(parse, "EntitySelector", Utils.isCBXXXorLater("1.14") ? "getEntities" : "b", wrapper);
            var resultList = new ArrayList<Entity>(nmsList.size());
            for (var nmsEntity : nmsList) {
                resultList.add((Entity) NetMinecraft.LEGACY.invokeMethod(nmsEntity, "Entity", "getBukkitEntity"));
            }
            return resultList;
        } else {
            var entity = NetMinecraft.CM_ARGUMENTS.invokeMethod(null, "ArgumentEntity", "multipleEntities");
            var wrapper = NetMinecraft.COMMANDS.invokeMethod(getICommandListener(sender), "CommandListenerWrapper", "a", vector);
            var parse = NetMinecraft.CM_ARGUMENTS.invokeMethod(entity, "ArgumentEntity", "parse", reader);
            var nmsList = (List<?>) NetMinecraft.CM_AR_SELECTOR.invokeMethod(parse, "EntitySelector", "getEntities", wrapper);
            var resultList = new ArrayList<Entity>(nmsList.size());
            for (var nmsEntity : nmsList) {
                resultList.add((Entity) NetMinecraft.WR_ENTITY.invokeMethod(nmsEntity, "Entity", "getBukkitEntity"));
            }
            return resultList;
        }
    }

    @NotNull
    public static Object getICommandListener(@NotNull CommandSender sender) throws ReflectiveOperationException {
        if (Utils.isCBXXXorLater("1.13.2")) {
            return CraftBukkit.COMMAND.invokeMethod(false, (Object) null, "VanillaCommandWrapper", "getListener", COMMAND_LISTENER_CLASS, sender);
        }
        if (sender instanceof Player) {
            var entity = CraftBukkit.ENTITY.invokeMethod(sender, "CraftPlayer", "getHandle");
            return NetMinecraft.LEGACY.invokeMethod(entity, "Entity", "getCommandListener");
        } else if (sender instanceof BlockCommandSender) {
            return CraftBukkit.COMMAND.invokeMethod(sender, "CraftBlockCommandSender", "getWrapper");
        } else if (sender instanceof CommandMinecart) {
            var cart = CraftBukkit.ENTITY.invokeMethod(sender, "CraftMinecartCommand", "getHandle");
            var command = NetMinecraft.LEGACY.invokeMethod(cart, "EntityMinecartCommandBlock", "getCommandBlock");
            return NetMinecraft.LEGACY.invokeMethod(command, "CommandBlockListenerAbstract", "getWrapper");
        } else if (sender instanceof RemoteConsoleCommandSender) {
            var server = NetMinecraft.LEGACY.invokeMethod(null, "MinecraftServer", "getServer");
            var remote = NetMinecraft.LEGACY.getFieldValue("DedicatedServer", "remoteControlCommandListener", server);
            return NetMinecraft.LEGACY.invokeMethod(remote, "RemoteControlCommandListener", "f");
        } else if (sender instanceof ConsoleCommandSender) {
            var server = CraftBukkit.CB.invokeMethod(sender.getServer(), "CraftServer", "getServer");
            return NetMinecraft.LEGACY.invokeMethod(server, "MinecraftServer", "getServerCommandListener");
        } else if (sender instanceof ProxiedCommandSender) {
            return CraftBukkit.COMMAND.invokeMethod(sender, "ProxiedNativeCommandSender", "getHandle");
        }
        throw new IllegalArgumentException("Cannot make " + sender + " a vanilla command listener");
    }

    @NotNull
    public static Object createMagmaCube(@NotNull BlockCoords blockCoords) throws ReflectiveOperationException {
        double x = blockCoords.getX() + 0.5D, y = blockCoords.getY(), z = blockCoords.getZ() + 0.5D;
        var magmaCube = newEntityMagmaCube(blockCoords.getWorld());
        if (NetMinecraft.isLegacy()) {
            if (Utils.isCBXXXorLater("1.11")) {
                NetMinecraft.LEGACY.invokeMethod(true, magmaCube, "EntityMagmaCube", "setSize", 2, true);
            } else {
                NetMinecraft.LEGACY.invokeMethod(true, magmaCube, "EntityMagmaCube", "setSize", 2);
            }
            NetMinecraft.LEGACY.invokeMethod(magmaCube, "EntityMagmaCube", "setFlag", 6, true);
            NetMinecraft.LEGACY.invokeMethod(magmaCube, "EntityMagmaCube", "setInvisible", true);
            NetMinecraft.LEGACY.invokeMethod(magmaCube, "EntityMagmaCube", "setLocation", x, y, z, 0.0F, 0.0F);
        } else {
            NetMinecraft.WR_EN_MONSTER.invokeMethod(true, magmaCube, "EntityMagmaCube", "setSize", 2, true);
            NetMinecraft.WR_EN_MONSTER.invokeMethod(magmaCube, "EntityMagmaCube", "setFlag", 6, true);
            NetMinecraft.WR_EN_MONSTER.invokeMethod(magmaCube, "EntityMagmaCube", "setInvisible", true);
            NetMinecraft.WR_EN_MONSTER.invokeMethod(magmaCube, "EntityMagmaCube", "setLocation", x, y, z, 0.0F, 0.0F);
        }
        return magmaCube;
    }

    @NotNull
    public static Object newEntityMagmaCube(@NotNull World world) throws ReflectiveOperationException {
        var handle = CraftBukkit.CB.invokeMethod(world, "CraftWorld", "getHandle");
        if (NetMinecraft.isLegacy()) {
            var nmsWorld = NetMinecraft.LEGACY.getClass("World");
            var entityTypes = NetMinecraft.LEGACY.getClass("EntityTypes");
            if (Utils.isCBXXXorLater("1.14")) {
                var entityType = NetMinecraft.LEGACY.getFieldValue("EntityTypes", "MAGMA_CUBE", null);
                return NetMinecraft.LEGACY.newInstance(false, "EntityMagmaCube", new Class<?>[] { entityTypes, nmsWorld }, entityType, handle);
            }
            return NetMinecraft.LEGACY.newInstance(false, "EntityMagmaCube", new Class<?>[] { nmsWorld }, handle);
        } else {
            var nmsWorld = NetMinecraft.WR_LEVEL.getClass("World");
            var entityTypes = NetMinecraft.WR_ENTITY.getClass("EntityTypes");
            var entityType = NetMinecraft.WR_ENTITY.getFieldValue("EntityTypes", "X", null);
            return NetMinecraft.WR_EN_MONSTER.newInstance(false, "EntityMagmaCube", new Class<?>[] { entityTypes, nmsWorld }, entityType, handle);
        }
    }

    @NotNull
    public static Object createSpawnEntity(@NotNull Object nmsEntity) throws ReflectiveOperationException {
        if (NetMinecraft.isLegacy()) {
            var spawnEntity = NetMinecraft.LEGACY.newInstance(false, "PacketPlayOutSpawnEntityLiving", new Class<?>[] { NetMinecraft.LEGACY.getClass("EntityLiving") }, nmsEntity);
            NetMinecraft.LEGACY.setFieldValue(true, "PacketPlayOutSpawnEntityLiving", "j", spawnEntity, (byte) 0);
            NetMinecraft.LEGACY.setFieldValue(true, "PacketPlayOutSpawnEntityLiving", "k", spawnEntity, (byte) 0);
            NetMinecraft.LEGACY.setFieldValue(true, "PacketPlayOutSpawnEntityLiving", "l", spawnEntity, (byte) 0);
            return spawnEntity;
        } else {
            var spawnEntity = NetMinecraft.NW_PR_GAME.newInstance(false, "PacketPlayOutSpawnEntityLiving", new Class<?>[] { NetMinecraft.WR_ENTITY.getClass("EntityLiving") }, nmsEntity);
            NetMinecraft.NW_PR_GAME.setFieldValue(true, "PacketPlayOutSpawnEntityLiving", "j", spawnEntity, (byte) 0);
            NetMinecraft.NW_PR_GAME.setFieldValue(true, "PacketPlayOutSpawnEntityLiving", "k", spawnEntity, (byte) 0);
            NetMinecraft.NW_PR_GAME.setFieldValue(true, "PacketPlayOutSpawnEntityLiving", "l", spawnEntity, (byte) 0);
            return spawnEntity;
        }
    }

    @NotNull
    public static Object createMetadata(final int id, @NotNull Object nmsEntity) throws ReflectiveOperationException {
        if (NetMinecraft.isLegacy()) {
            var watcher = NetMinecraft.LEGACY.invokeMethod(nmsEntity, "EntityMagmaCube", "getDataWatcher");
            return NetMinecraft.LEGACY.newInstance("PacketPlayOutEntityMetadata", id, watcher, true);
        } else {
            var watcher = NetMinecraft.WR_EN_MONSTER.invokeMethod(nmsEntity, "EntityMagmaCube", "getDataWatcher");
            return NetMinecraft.NW_PR_GAME.newInstance("PacketPlayOutEntityMetadata", id, watcher, true);
        }
    }

    @NotNull
    public static Object[] createDestroy(final int[] ids) throws ReflectiveOperationException {
        if (NetMinecraft.isLegacy()) {
            return new Object[] { NetMinecraft.LEGACY.newInstance("PacketPlayOutEntityDestroy", ids) };
        } else {
            var packets = new Object[ids.length];
            for (int i = 0, l = ids.length; i < l; i++) {
                packets[i] = NetMinecraft.NW_PR_GAME.newInstance("PacketPlayOutEntityDestroy", ids[i]);
            }
            return packets;
        }
    }

    public static int getPing(@NotNull Player player) throws ReflectiveOperationException {
        var handle = CraftBukkit.ENTITY.invokeMethod(player, "CraftPlayer", "getHandle");
        if (NetMinecraft.isLegacy()) {
            return NetMinecraft.LEGACY.getField("EntityPlayer", "ping").getInt(handle);
        } else {
            return NetMinecraft.SR_LEVEL.getField("EntityPlayer", "e").getInt(handle);
        }
    }

    public static int getEntityId(@NotNull Object nmsEntity) throws ReflectiveOperationException {
        return NetMinecraft.isLegacy() ? (int) NetMinecraft.LEGACY.invokeMethod(nmsEntity, "EntityMagmaCube", "getId") : (int) NetMinecraft.WR_EN_MONSTER.invokeMethod(nmsEntity, "EntityMagmaCube", "getId");
    }

    @NotNull
    public static UUID getUniqueId(@NotNull Object nmsEntity) throws ReflectiveOperationException {
        return NetMinecraft.isLegacy() ? (UUID) NetMinecraft.LEGACY.getFieldValue(true, "Entity", "uniqueID", nmsEntity) : (UUID) NetMinecraft.WR_ENTITY.getFieldValue(true, "Entity", "aj", nmsEntity);
    }

    @NotNull
    public static Object getAxisAlignedBB(@NotNull Block block) throws ReflectiveOperationException {
        var world = CraftBukkit.CB.invokeMethod(block.getWorld(), "CraftWorld", "getHandle");
        var position = NetMinecraft.LEGACY.newInstance("BlockPosition", block.getX(), block.getY(), block.getZ());
        var blockData = NetMinecraft.LEGACY.invokeMethod(world, "WorldServer", "getType", position);
        if (Utils.isCBXXXorLater("1.13")) {
            var name = Utils.getPackageVersion().equals("v1_13_R2") ? "i" : "g";
            var voxelShape = NetMinecraft.LEGACY.getMethod("IBlockData", name, NetMinecraft.LEGACY.getClass("IBlockAccess"), position.getClass());
            return NetMinecraft.LEGACY.invokeMethod(voxelShape.invoke(blockData, world, position), "VoxelShape", "a");
        } else {
            var name = Utils.isCBXXXorLater("1.11") ? "b" : "a";
            var axisAlignedBB = NetMinecraft.LEGACY.getMethod("Block", name, NetMinecraft.LEGACY.getClass("IBlockData"), NetMinecraft.LEGACY.getClass("IBlockAccess"), position.getClass());
            return axisAlignedBB.invoke(NetMinecraft.LEGACY.invokeMethod(blockData, "IBlockData", "getBlock"), blockData, world, position);
        }
    }

    @NotNull
    public static Map<String, Material> getItemRegistry() throws ReflectiveOperationException {
        var materials = new HashMap<String, Material>();
        var registry = NetMinecraft.LEGACY.getFieldValue("Item", "REGISTRY", null);
        var registryMap = (Map<?, ?>) NetMinecraft.LEGACY.getFieldValue(true, "RegistrySimple", "c", registry);
        var bukkitMaterial = CraftBukkit.UTIL.getMethod("CraftMagicNumbers", "getMaterial", NetMinecraft.LEGACY.getClass("Item"));
        for (var entry : registryMap.entrySet()) {
            materials.put(getKey(entry.getKey()), (Material) bukkitMaterial.invoke(null, entry.getValue()));
        }
        return materials;
    }

    @NotNull
    private static String getKey(@NotNull Object minecraftKey) throws ReflectiveOperationException {
        var name = Utils.isCBXXXorLater("1.12") ? "getKey" : "a";
        return (String) NetMinecraft.LEGACY.invokeMethod(minecraftKey, "MinecraftKey", name);
    }

    @NotNull
    public static Object newVec3D(double x, double y, double z) throws ReflectiveOperationException {
        return NetMinecraft.isLegacy() ? NetMinecraft.LEGACY.newInstance("Vec3D", x, y, z) : NetMinecraft.WR_PHYS.newInstance("Vec3D", x, y, z);
    }
}