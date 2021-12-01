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

import org.apache.commons.lang.ArrayUtils;
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
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.github.yuttyann.scriptblockplus.enums.server.CraftBukkit;
import com.github.yuttyann.scriptblockplus.enums.server.Mojang;
import com.github.yuttyann.scriptblockplus.enums.server.NetMinecraft;
import com.github.yuttyann.scriptblockplus.enums.server.reflect.ReflectMatcher;
import com.github.yuttyann.scriptblockplus.hook.nms.GlowEntity;
import com.github.yuttyann.scriptblockplus.raytrace.RayResult;

/**
 * ScriptBlockPlus NMSHelper クラス
 * @author yuttyann44581
 */
public final class NMSHelper {

    public static void build() throws ReflectiveOperationException {
        var builder = new ReflectMatcher.Builder();
        if (Utils.isCBXXXorLater("1.14")) {
            builder
            .key("setTitle")
            .method(orLegacy(NetMinecraft.WR_INVENTORY).getClass("Container"))
            .parameter(orLegacy(NetMinecraft.NW_CHAT).getClass("IChatBaseComponent"))
            .match()

            .key("ContainerAccess")
            .method(orLegacy(NetMinecraft.WR_INVENTORY).getClass("ContainerAccess"))
            .type(orLegacy(NetMinecraft.WR_INVENTORY).getClass("ContainerAccess"))
            .parameter(orLegacy(NetMinecraft.WR_LEVEL).getClass("World"), orLegacy(NetMinecraft.CORE).getClass("BlockPosition"))
            .match();
        }
        if (Utils.isCBXXXorLater("1.13.2")) {
            builder
            .key("parse")
            .method(orLegacy(NetMinecraft.CM_ARGUMENTS).getClass("ArgumentEntity"))
            .type(orLegacy(NetMinecraft.CM_AR_SELECTOR).getClass("EntitySelector"))
            .parameter(Mojang.BRIGADIER.getClass("StringReader"))
            .match()

            .key("getEntities")
            .method(orLegacy(NetMinecraft.CM_AR_SELECTOR).getClass("EntitySelector"))
            .type(List.class)
            .parameter(orLegacy(NetMinecraft.COMMANDS).getClass("CommandListenerWrapper"))
            .match()

            .key("ArgumentEntity")
            .constructor(orLegacy(NetMinecraft.CM_ARGUMENTS).getClass("ArgumentEntity"))
            .parameter(boolean.class, boolean.class)
            .match();
        }
        builder
        .key("playerInventory")
        .field(orLegacy(NetMinecraft.WR_EN_PLAYER).getClass("EntityHuman"))
        .type(orLegacy(NetMinecraft.WR_EN_PLAYER).getClass("PlayerInventory"))
        .match()

        .key("playerConnection")
        .field(orLegacy(NetMinecraft.SR_LEVEL).getClass("EntityPlayer"))
        .type(orLegacy(NetMinecraft.SR_NETWORK).getClass("PlayerConnection"))
        .match()

        .key("defaultContainer")
        .name(Utils.isCBXXXorLater("1.14") ? null : "defaultContainer")
        .field(orLegacy(NetMinecraft.WR_EN_PLAYER).getClass("EntityHuman"))
        .type(orLegacy(NetMinecraft.WR_INVENTORY).getClass(Utils.isCBXXXorLater("1.14") ? "ContainerPlayer" : "Container"))
        .match()

        .key("activeContainer")
        .name(Utils.isCBXXXorLater("1.14") ? null : "activeContainer")
        .field(orLegacy(NetMinecraft.WR_EN_PLAYER).getClass("EntityHuman"))
        .type(orLegacy(NetMinecraft.WR_INVENTORY).getClass("Container"))
        .match()

        .key("initMenu")
        .name(NetMinecraft.isLegacy() ? "addSlotListener" : null)
        .method(orLegacy(NetMinecraft.SR_LEVEL).getClass(NetMinecraft.isLegacy() ? "Container" : "EntityPlayer"))
        .parameter(NetMinecraft.isLegacy() ? NetMinecraft.LEGACY.getClass("ICrafting") : NetMinecraft.WR_INVENTORY.getClass("Container"))
        .match()

        .key("sendPacket")
        .method(orLegacy(NetMinecraft.SR_NETWORK).getClass("PlayerConnection"))
        .parameter(orLegacy(NetMinecraft.NW_PROTOCOL).getClass("Packet"))
        .match(false, Modifier.PUBLIC)

        .key("setInvisible")
        .name("setInvisible")
        .method(NetMinecraft.isLegacy() ? NetMinecraft.LEGACY.getClass("Entity") : CraftBukkit.ENTITY.getClass("CraftLivingEntity"))
        .parameter(boolean.class)
        .match()

        .key("setLocation")
        .method(orLegacy(NetMinecraft.WR_ENTITY).getClass("Entity"))
        .parameter(double.class, double.class, double.class, float.class, float.class)
        .match()

        .key("getDataWatcher")
        .method(orLegacy(NetMinecraft.WR_ENTITY).getClass("Entity"))
        .type(orLegacy(NetMinecraft.NW_SYNCHER).getClass("DataWatcher"))
        .parameter(ArrayUtils.EMPTY_CLASS_ARRAY)
        .match()

        .key("getBukkitEntity")
        .name("getBukkitEntity")
        .method(orLegacy(NetMinecraft.WR_ENTITY).getClass("Entity"))
        .type(CraftBukkit.ENTITY.getClass("CraftEntity"))
        .parameter(ArrayUtils.EMPTY_CLASS_ARRAY)
        .match()

        .key("handleInventoryCloseEvent")
        .name("handleInventoryCloseEvent")
        .method(CraftBukkit.EVENT.getClass("CraftEventFactory"))
        .parameter(orLegacy(NetMinecraft.WR_EN_PLAYER).getClass("EntityHuman"))
        .match()

        .key("CraftMagmaCube")
        .constructor(CraftBukkit.ENTITY.getClass("CraftMagmaCube"))
        .parameter(CraftBukkit.CB.getClass("CraftServer"), orLegacy(NetMinecraft.WR_EN_MONSTER).getClass("EntityMagmaCube"))
        .match()

        .key("EntityMagmaCube")
        .constructor(orLegacy(NetMinecraft.WR_EN_MONSTER).getClass("EntityMagmaCube"))
        .parameter(
            Utils.isCBXXXorLater("1.14")
            ? new Class<?>[] { orLegacy(NetMinecraft.WR_ENTITY).getClass("EntityTypes"), orLegacy(NetMinecraft.WR_LEVEL).getClass("World") }
            : new Class<?>[] { NetMinecraft.LEGACY.getClass("World") }
        )
        .match()

        .key("ContainerAnvil")
        .constructor(orLegacy(NetMinecraft.WR_INVENTORY).getClass("ContainerAnvil"))
        .parameter( 
            Utils.isCBXXXorLater("1.14")
            ? new Class<?>[] { int.class, orLegacy(NetMinecraft.WR_EN_PLAYER).getClass("PlayerInventory"), orLegacy(NetMinecraft.WR_INVENTORY).getClass("ContainerAccess") }
            : new Class<?>[] { NetMinecraft.LEGACY.getClass("PlayerInventory"), NetMinecraft.LEGACY.getClass("World"), NetMinecraft.LEGACY.getClass("BlockPosition"), NetMinecraft.LEGACY.getClass("EntityHuman") }
        )
        .match()

        .key("PacketPlayOutOpenWindow")
        .constructor(orLegacy(NetMinecraft.NW_PR_GAME).getClass("PacketPlayOutOpenWindow"))
        .parameter(
            int.class,
            Utils.isCBXXXorLater("1.14") ? orLegacy(NetMinecraft.WR_INVENTORY).getClass("Containers") : String.class,
            orLegacy(NetMinecraft.NW_CHAT).getClass("IChatBaseComponent")
        )
        .match()

        .key("PacketPlayOutEntityMetadata")
        .constructor(orLegacy(NetMinecraft.NW_PR_GAME).getClass("PacketPlayOutEntityMetadata"))
        .parameter(int.class, orLegacy(NetMinecraft.NW_SYNCHER).getClass("DataWatcher"), boolean.class)
        .match()

        .key("PacketPlayOutSpawnEntityLiving")
        .constructor(orLegacy(NetMinecraft.NW_PR_GAME).getClass("PacketPlayOutSpawnEntityLiving"))
        .parameter(orLegacy(NetMinecraft.WR_ENTITY).getClass("EntityLiving"))
        .match();
    }


    /** 1.17 以降も稼働しているコード */

    public static void sendPacket(@NotNull Object packet) throws ReflectiveOperationException {
        for (var player : Bukkit.getOnlinePlayers()) {
            sendPacket(player, packet);
        }
    }

    public static void sendPacket(@NotNull Player player, @NotNull Object packet) throws ReflectiveOperationException {
        if (player != null && player.isOnline()) {
            ReflectMatcher.method("sendPacket").invoke(ReflectMatcher.field("playerConnection").get(getNMSPlayer(player)), packet);
        }
    }

    public static void sendPackets(@NotNull Object... packets) throws ReflectiveOperationException {
        for (var player : Bukkit.getOnlinePlayers()) {
            sendPackets(player, packets);
        }
    }

    public static void sendPackets(@NotNull Player player, @NotNull Object... packets) throws ReflectiveOperationException {
        if (player != null && player.isOnline()) {
            for (var packet : packets) {
                sendPacket(player, packet);
            }
        }
    }

    @NotNull
    public static Object newVec3D(double x, double y, double z) throws ReflectiveOperationException {
        return orLegacy(NetMinecraft.WR_PHYS).newInstance("Vec3D", x, y, z);
    }

    @NotNull
    public static Object getNMSWorld(@NotNull World world) throws ReflectiveOperationException {
        return CraftBukkit.CB.invokeMethod(world, "CraftWorld", "getHandle");
    }

    @NotNull
    public static Object getNMSEntity(@NotNull Entity entity) throws ReflectiveOperationException {
        return CraftBukkit.ENTITY.invokeMethod(entity, "CraftEntity", "getHandle");
    }

    @NotNull
    public static Object getNMSPlayer(@NotNull Player player) throws ReflectiveOperationException {
        return CraftBukkit.ENTITY.invokeMethod(player, "CraftPlayer", "getHandle");
    }
    @NotNull
    public static UUID getUniqueId(@NotNull Object craftEntity) throws ReflectiveOperationException {
        return (UUID) CraftBukkit.ENTITY.invokeMethod(craftEntity, "CraftEntity", "getUniqueId");
    }

    public static int getEntityId(@NotNull Object craftEntity) throws ReflectiveOperationException {
        return (int) CraftBukkit.ENTITY.invokeMethod(craftEntity, "CraftEntity", "getEntityId");
    }

    public static int getPing(@NotNull Player player) throws ReflectiveOperationException {
        return orLegacy(NetMinecraft.SR_LEVEL).getField("EntityPlayer", NetMinecraft.isLegacy() ? "ping" : "e").getInt(getNMSPlayer(player));
    }

    @NotNull
    public static List<Entity> selectEntities(@NotNull CommandSender sender, @NotNull Location location, @NotNull String selector) throws ReflectiveOperationException {
        var vector = newVec3D(location.getBlockX() + 0.5D, location.getBlockY(), location.getBlockZ() + 0.5D);
        var wrapper = orLegacy(NetMinecraft.COMMANDS).invokeMethod(getICommandListener(sender), "CommandListenerWrapper", "a", vector);
        var entity = ReflectMatcher.constructor("ArgumentEntity").newInstance(false, false);
        var parse = ReflectMatcher.method("parse").invoke(entity, Mojang.BRIGADIER.newInstance("StringReader", selector));
        var nmsList = (List<?>) ReflectMatcher.method("getEntities").invoke(parse, wrapper);
        var resultList = new ArrayList<Entity>(nmsList.size());
        var bukkitEntity = ReflectMatcher.method("getBukkitEntity");
        for (var nmsEntity : nmsList) {
            resultList.add((Entity) bukkitEntity.invoke(nmsEntity, ArrayUtils.EMPTY_OBJECT_ARRAY));
        }
        return resultList;
    }

    @NotNull
    public static Object getICommandListener(@NotNull CommandSender sender) throws ReflectiveOperationException {
        if (Utils.isCBXXXorLater("1.13.2")) {
            return CraftBukkit.COMMAND.invokeMethod(false, (Object) null, "VanillaCommandWrapper", "getListener", new Class<?>[] { CommandSender.class }, sender);
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
    public static Object newEntityMagmaCube(@NotNull World world) throws ReflectiveOperationException {
        var nmsWorld = getNMSWorld(world);
        if (Utils.isCBXXXorLater("1.14")) {
            var magmaCube = orLegacy(NetMinecraft.WR_ENTITY).getFieldValue("EntityTypes", NetMinecraft.isLegacy() ? "MAGMA_CUBE" : "X", null);
            return ReflectMatcher.constructor("EntityMagmaCube").newInstance(magmaCube, nmsWorld);
        }
        return ReflectMatcher.constructor("EntityMagmaCube").newInstance(nmsWorld);
    }

    @NotNull
    public static Object createSpawnEntity(@NotNull GlowEntity glowEntity) throws ReflectiveOperationException {
        var nmsEntity = glowEntity.getNMSEntity();
        var spawnEntity = ReflectMatcher.constructor("PacketPlayOutSpawnEntityLiving").newInstance(nmsEntity);
        orLegacy(NetMinecraft.NW_PR_GAME).setFieldValue(true, "PacketPlayOutSpawnEntityLiving", "j", spawnEntity, (byte) 0);
        orLegacy(NetMinecraft.NW_PR_GAME).setFieldValue(true, "PacketPlayOutSpawnEntityLiving", "k", spawnEntity, (byte) 0);
        orLegacy(NetMinecraft.NW_PR_GAME).setFieldValue(true, "PacketPlayOutSpawnEntityLiving", "l", spawnEntity, (byte) 0);
        return spawnEntity;
    }

    @NotNull
    public static Object createMetadata(@NotNull GlowEntity glowEntity) throws ReflectiveOperationException {
        var watcher = ReflectMatcher.method("getDataWatcher").invoke(glowEntity.getNMSEntity(), ArrayUtils.EMPTY_OBJECT_ARRAY);
        return ReflectMatcher.constructor("PacketPlayOutEntityMetadata").newInstance(glowEntity.getId(), watcher, true);
    }

    @NotNull
    public static Object[] createDestroy(final int[] ids) throws ReflectiveOperationException {
        if (Utils.getServerVersion().equals("1.17")) {
            var packets = new Object[ids.length];
            for (int i = 0, l = ids.length; i < l; i++) {
                packets[i] = NetMinecraft.NW_PR_GAME.newInstance("PacketPlayOutEntityDestroy", ids[i]);
            }
            return packets;
        }
        return new Object[] { orLegacy(NetMinecraft.NW_PR_GAME).newInstance("PacketPlayOutEntityDestroy", ids) };
    }

    public static int getContainerId(@NotNull Player player, @NotNull Object container) throws ReflectiveOperationException {
        if (Utils.isCBXXXorLater("1.17")) {
            return (int) NetMinecraft.WR_INVENTORY.getFieldValue("Container", "j", container);
        } else if (Utils.isCBXXXorLater("1.14")) {
            return (int) NetMinecraft.LEGACY.getFieldValue("Container", "windowId", container);
        }
        return getNextContainerId(player);
    }

    public static int getNextContainerId(@NotNull Player player) throws ReflectiveOperationException {
    	return (int) orLegacy(NetMinecraft.SR_LEVEL).invokeMethod(getNMSPlayer(player), "EntityPlayer", "nextContainerCounter");
    }

    public static void sendPacketOpenWindow(@NotNull Player player, @NotNull String title, int containerId) throws ReflectiveOperationException {
        if (Utils.isCBXXXorLater("1.14")) {
            var anvil = orLegacy(NetMinecraft.WR_INVENTORY).getFieldValue("Containers",  NetMinecraft.isLegacy() ? "ANVIL" : "h", null);
            var message = orLegacy(NetMinecraft.NW_CHAT).newInstance("ChatComponentText", title);
            sendPacket(player, ReflectMatcher.constructor("PacketPlayOutOpenWindow").newInstance(containerId, anvil, message));
        } else {
            var name = NetMinecraft.LEGACY.invokeMethod(NetMinecraft.LEGACY.getFieldValue("Blocks", "ANVIL", null), "Block", "a");
            var message = NetMinecraft.LEGACY.newInstance("ChatComponentText", name + ".name");
            sendPacket(player, ReflectMatcher.constructor("PacketPlayOutOpenWindow").newInstance(containerId, "minecraft:anvil", message));
        }
    }

    public static void sendPacketCloseWindow(@NotNull Player player, int containerId) throws ReflectiveOperationException {
        sendPacket(player, orLegacy(NetMinecraft.NW_PR_GAME).newInstance("PacketPlayOutCloseWindow", containerId));
    }

    public static void setActiveContainerDefault(@NotNull Player player) throws ReflectiveOperationException {
        var nmsPlayer = getNMSPlayer(player);
        ReflectMatcher.field("activeContainer").set(nmsPlayer, ReflectMatcher.field("defaultContainer").get(nmsPlayer));
    }

    public static void setActiveContainer(@NotNull Player player, @NotNull Object container) throws ReflectiveOperationException {
        ReflectMatcher.field("activeContainer").set(getNMSPlayer(player), container);
    }

    public static void setActiveContainerId(@NotNull Object container, int containerId) throws ReflectiveOperationException {
        if (!Utils.isCBXXXorLater("1.14")) {
            NetMinecraft.LEGACY.setFieldValue("Container", "windowId", container, containerId);
        }
    }

    public static void addActiveContainerSlotListener(@NotNull Object container, @NotNull Player player) throws ReflectiveOperationException {
        var args = Utils.isCBXXXorLater("1.17") ? container : getNMSPlayer(player);
        ReflectMatcher.method("initMenu").invoke(Utils.isCBXXXorLater("1.17") ? getNMSPlayer(player) : container, args);
    }

    @NotNull
    public static Inventory toBukkitInventory(@NotNull Object container) throws ReflectiveOperationException {
        var view = (Utils.isCBXXXorLater("1.17") ? NetMinecraft.WR_INVENTORY : NetMinecraft.LEGACY).invokeMethod(container, "Container", "getBukkitView");
        return ((InventoryView) view).getTopInventory();
    }

    @NotNull
    public static Object newContainerAnvil(@NotNull Player player, @NotNull String title) throws ReflectiveOperationException {
        var nmsWorld = getNMSWorld(player.getWorld());
        var nmsPlayer = getNMSPlayer(player);
        var position = orLegacy(NetMinecraft.CORE).newInstance("BlockPosition", 0, 0, 0);
        var inventory = ReflectMatcher.field("playerInventory").get(nmsPlayer);
        var containerAnvil = ReflectMatcher.constructor("ContainerAnvil").newInstance(
            Utils.isCBXXXorLater("1.14")
            ? new Object[] { getNextContainerId(player), inventory, ReflectMatcher.method("ContainerAccess").invoke(null, nmsWorld, position) }
            : new Object[] { inventory, nmsWorld, position, nmsPlayer }
        );
        if (Utils.isCBXXXorLater("1.14")) {
            ReflectMatcher.method("setTitle").invoke(containerAnvil, orLegacy(NetMinecraft.NW_CHAT).newInstance("ChatComponentText", title));
        }
        orLegacy(NetMinecraft.WR_INVENTORY).setFieldValue("Container", "checkReachable", containerAnvil, false);
        return containerAnvil;
    }

    public static void handleInventoryCloseEvent(@NotNull Player player) throws ReflectiveOperationException {
        ReflectMatcher.method("handleInventoryCloseEvent").invoke(null, getNMSPlayer(player));
    }


    /** 1.16.5 以下のレガシーなコード */

    private static final double R = 0.017453292D;

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
        var nmsWorld = getNMSWorld(world);
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

    @NotNull
    private static String getKey(@NotNull Object minecraftKey) throws ReflectiveOperationException {
        var name = Utils.isCBXXXorLater("1.12") ? "getKey" : "a";
        return (String) NetMinecraft.LEGACY.invokeMethod(minecraftKey, "MinecraftKey", name);
    }

    @NotNull
    private static NetMinecraft orLegacy(NetMinecraft netMinecraft) {
        return NetMinecraft.isLegacy() ? NetMinecraft.LEGACY : netMinecraft;
    }
}