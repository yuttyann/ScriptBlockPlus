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
package com.github.yuttyann.scriptblockplus.utils.server.minecraft;

import static com.github.yuttyann.scriptblockplus.utils.reflect.Reflection.*;
import static com.github.yuttyann.scriptblockplus.utils.server.CraftBukkit.*;
import static com.github.yuttyann.scriptblockplus.utils.server.NetMinecraft.*;
import static com.github.yuttyann.scriptblockplus.utils.server.minecraft.SpigotAccessor_v1_20_5.*;
import static com.github.yuttyann.scriptblockplus.utils.version.McVersion.*;
import static io.leangen.geantyref.TypeFactory.*;
import static java.lang.Integer.*;
import static java.lang.reflect.Modifier.*;
import static org.apache.commons.lang3.ArrayUtils.*;
import static org.apache.commons.lang3.math.NumberUtils.*;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.springframework.lang.NonNull;

import com.github.yuttyann.scriptblockplus.utils.kyori.KyoriGson;
import com.github.yuttyann.scriptblockplus.utils.kyori.KyoriLegacy;
import com.github.yuttyann.scriptblockplus.utils.reflect.matcher.ReflectionMatcher.ConstructorStore;
import com.github.yuttyann.scriptblockplus.utils.reflect.matcher.ReflectionMatcher.FieldStore;
import com.github.yuttyann.scriptblockplus.utils.reflect.matcher.ReflectionMatcher.MethodStore;
import com.mojang.brigadier.StringReader;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

/**
 * FBS-Bukkit SpigotAccessor_v1_17
 * @author yuttyann44581
 */
final class SpigotAccessor_v1_17 implements NativeAccessor {

    private final FieldStore f;
    private final MethodStore m;
    private final ConstructorStore c;

    private final Object magmaCubeType;
 
    SpigotAccessor_v1_17() throws ReflectiveOperationException {
        // sendPacket
        field(SERVER_LEVEL.getClass("EntityPlayer"))
            .modifiers(PUBLIC, -STATIC, -FINAL)
            .fieldType(SERVER_NETWORK.getClass("PlayerConnection"))
            .findFirst("EntityPlayer.connection");
        method(V_1_20_2.isSupported() ? SERVER_NETWORK.getClass("ServerCommonPacketListenerImpl") : SERVER_NETWORK.getClass("PlayerConnection"))
            .modifiers(PUBLIC, -STATIC, -FINAL)
            .parameterTypes(NETWORK_PROTOCOL.getClass("Packet"))
            .returnType(void.class)
            .findFirst("PlayerConnection.sendPacket");

        // getEntity
        method(ENTITY.getClass("CraftEntity"))
            .modifiers(PUBLIC, -STATIC, -FINAL)
            .name("getHandle")
            .returnType(WORLD_ENTITY.getClass("Entity"))
            .emptyParameterTypes()
            .findFirst("CraftEntity.getHandle");

        // getServerPlayer
        method(ENTITY.getClass("CraftPlayer"))
            .modifiers(PUBLIC, -STATIC, -FINAL)
            .name("getHandle")
            .returnType(SERVER_LEVEL.getClass("EntityPlayer"))
            .emptyParameterTypes()
            .findFirst("CraftPlayer.getHandle");

        // getServerLevel
        method(CRAFTBUKKIT.getClass("CraftWorld"))
            .modifiers(PUBLIC, -STATIC, -FINAL)
            .name("getHandle")
            .returnType(SERVER_LEVEL.getClass("WorldServer"))
            .emptyParameterTypes()
            .findFirst("CraftWorld.getHandle");

        // getComponentFromJson
        method(NETWORK_CHAT.getClass("IChatBaseComponent$ChatSerializer"))
            .modifiers(PUBLIC, STATIC)
            .name("a")
            .returnType(NETWORK_CHAT.getClass("IChatMutableComponent"))
            .parameterTypes(String.class)
            .findFirst("ChatSerializer.fromJson");

        // getComponentToJson
        method(NETWORK_CHAT.getClass("IChatBaseComponent$ChatSerializer"))
            .modifiers(PUBLIC, STATIC)
            .name("a")
            .returnType(String.class)
            .parameterTypes(NETWORK_CHAT.getClass("IChatBaseComponent"))
            .findFirst("ChatSerializer.toJson");

        // getContainerId
        field(WORLD_INVENTORY.getClass("Container"))
            .modifiers(PUBLIC, -STATIC, FINAL)
            .fieldType(int.class)
            .findFirst("Container.containerId");

        // getNextContainerId
        method(SERVER_LEVEL.getClass("EntityPlayer"))
            .modifiers(PUBLIC, -STATIC, -FINAL)
            .name("nextContainerCounter")
            .returnType(int.class)
            .emptyParameterTypes()
            .findFirst("EntityPlayer.nextContainerCounter");

        // setActiveContainer & getActiveContainer
        field(WORLD_ENTITY_PLAYER.getClass("EntityHuman"))
            .modifiers(PUBLIC, -STATIC, -FINAL)
            .fieldType(WORLD_INVENTORY.getClass("Container"))
            .findFirst("EntityHuman.activeContainer");

        // getDefaultContainer
        field(WORLD_ENTITY_PLAYER.getClass("EntityHuman"))
            .modifiers(PUBLIC, -STATIC, FINAL)
            .fieldType(WORLD_INVENTORY.getClass("ContainerPlayer"))
            .findFirst("EntityHuman.defaultContainer");

        // initMenu
        method(SERVER_LEVEL.getClass("EntityPlayer"))
            .modifiers(PUBLIC, -STATIC, -FINAL)
            .returnType(void.class)
            .parameterTypes(WORLD_INVENTORY.getClass("Container"))
            .findFirst("EntityPlayer.initMenu");

        // handleInventoryCloseEvent
        method(EVENT.getClass("CraftEventFactory"))
            .modifiers(PUBLIC, STATIC)
            .name("handleInventoryCloseEvent")
            .returnType(void.class)
            .parameterTypes(WORLD_ENTITY_PLAYER.getClass("EntityHuman"))
            .findFirst("CraftEventFactory.handleInventoryCloseEvent");

        // getMenuType - Container
        method(WORLD_INVENTORY.getClass("Container"))
            .modifiers(PUBLIC, -STATIC, -FINAL)
            .returnType(WORLD_INVENTORY.getClass("Containers"))
            .emptyParameterTypes()
            .findFirst("Container.getType");

        // setMenuTitle & newAnvilMenu
        method(WORLD_INVENTORY.getClass("Container"))
            .modifiers(PUBLIC, -STATIC, FINAL)
            .name("setTitle")
            .returnType(void.class)
            .parameterTypes(NETWORK_CHAT.getClass("IChatBaseComponent"))
            .findFirst("Container.setTitle");

        // getBukkitView
        method(WORLD_INVENTORY.getClass("Container"))
            .modifiers(PUBLIC)
            .name("getBukkitView")
            .returnType(InventoryView.class)
            .emptyParameterTypes()
            .findFirst("Container.getBukkitView");

        // getEntities
        construct(COMMANDS_ARGUMENTS.getClass("ArgumentEntity"))
            .parameterTypes(boolean.class, boolean.class)
            .findFirst("ArgumentEntity");
        method(COMMANDS_ARGUMENTS.getClass("ArgumentEntity"))
            .modifiers(PUBLIC, -STATIC, -FINAL)
            .returnType(COMMANDS_ARGUMENTS_SELECTOR.getClass("EntitySelector"))
            .parameterTypes(StringReader.class)
            .findFirst("ArgumentEntity.parse");
        method(COMMAND.getClass("VanillaCommandWrapper"))
            .modifiers(STATIC)
            .name("getListener")
            .parameterTypes(CommandSender.class)
            .findFirst("VanillaCommandWrapper.getListener");
        method(COMMANDS.getClass("CommandListenerWrapper"))
            .modifiers(PUBLIC, -STATIC, -FINAL)
            .returnType(COMMANDS.getClass("CommandListenerWrapper"))
            .parameterTypes(WORLD_PHYS.getClass("Vec3D"))
            .findFirst("CommandListenerWrapper.withPosition");
        method(COMMANDS_ARGUMENTS_SELECTOR.getClass("EntitySelector"))
            .modifiers(PUBLIC, -STATIC, -FINAL)
            .returnType(List.class)
            .parameterTypes(COMMANDS.getClass("CommandListenerWrapper"))
            .findFirst("EntitySelector.findEntities");
        method(WORLD_ENTITY.getClass("Entity"))
            .modifiers(PUBLIC, -STATIC, -FINAL)
            .name("getBukkitEntity")
            .returnType(ENTITY.getClass("CraftEntity"))
            .emptyParameterTypes()
            .findFirst("Entity.getBukkitEntity");

        // newVec3
        construct(WORLD_PHYS.getClass("Vec3D"))
            .parameterTypes(double.class, double.class, double.class)
            .findFirst("Vec3D");
    
        // newAnvilMenu
        construct(CORE.getClass("BlockPosition"))
            .parameterTypes(int.class, int.class, int.class)
            .findFirst("BlockPosition");
        field(WORLD_ENTITY_PLAYER.getClass("EntityHuman"))
            .modifiers(-STATIC, FINAL)
            .fieldType(WORLD_ENTITY_PLAYER.getClass("PlayerInventory"))
            .findFirst("EntityHuman.playerInventory");
        method(WORLD_INVENTORY.getClass("ContainerAccess"))
            .modifiers(STATIC)
            .returnType(WORLD_INVENTORY.getClass("ContainerAccess"))
            .parameterTypes(WORLD_LEVEL.getClass("World"), CORE.getClass("BlockPosition"))
            .findFirst("ContainerAccess.create");
        construct(WORLD_INVENTORY.getClass("ContainerAnvil"))
            .parameterTypes(int.class, WORLD_ENTITY_PLAYER.getClass("PlayerInventory"), WORLD_INVENTORY.getClass("ContainerAccess"))
            .findFirst("ContainerAnvil");
        field(WORLD_INVENTORY.getClass("Container"))
            .modifiers(PUBLIC)
            .name("checkReachable")
            .fieldType(boolean.class)
            .findFirst("Container.checkReachable");

        // newMagmaCube
        construct(WORLD_ENTITY_MONSTER.getClass("EntityMagmaCube"))
            .parameterTypes(WORLD_ENTITY.getClass("EntityTypes"), WORLD_LEVEL.getClass("World"))
            .findFirst("EntityMagmaCube");

        // newCraftMagmaCube
        construct(ENTITY.getClass("CraftMagmaCube"))
            .parameterTypes(CRAFTBUKKIT.getClass("CraftServer"), WORLD_ENTITY_MONSTER.getClass("EntityMagmaCube"))
            .findFirst("CraftMagmaCube");

        // newClientboundSetEntityDataPacket
        method(WORLD_ENTITY.getClass("Entity"))
            .modifiers(PUBLIC, -STATIC, -FINAL)
            .returnType(NETWORK_SYNCHER.getClass("DataWatcher"))
            .emptyParameterTypes()
            .findFirst("Entity.getEntityData");
        if (V_1_19_3.isSupported()) {
            method(NETWORK_SYNCHER.getClass("DataWatcher")).deep()
                .modifiers(PUBLIC, -STATIC, -FINAL)
                .returnType(List.class, parameterizedClass(NETWORK_SYNCHER.getClass("DataWatcher$b"), unboundWildcard()))
                .emptyParameterTypes()
                .findAbsolute(null, "DataWatcher.getNonDefaultValues");
        }
        construct(NETWORK_PROTOCOL_GAME.getClass("PacketPlayOutEntityMetadata"))
            .parameterTypes(
                V_1_19_3.isSupported() ?
                new Class<?>[] { int.class, List.class } :
                new Class<?>[] { int.class, NETWORK_SYNCHER.getClass("DataWatcher"), boolean.class }
            )
            .findFirst("PacketPlayOutEntityMetadata");

        // newClientboundAddEntityPacket
        if (V_1_19.isSupported()) {
            construct(NETWORK_PROTOCOL_GAME.getClass("PacketPlayOutSpawnEntity"))
                .parameterTypes(WORLD_ENTITY.getClass("Entity"))
                .findFirst("PacketPlayOutSpawnEntity");
        } else {
            construct(NETWORK_PROTOCOL_GAME.getClass("PacketPlayOutSpawnEntityLiving"))
                .parameterTypes(WORLD_ENTITY.getClass("EntityLiving"))
                .findFirst("PacketPlayOutSpawnEntity");
        }
        field(constructs().first("PacketPlayOutSpawnEntity").getDeclaringClass())
            .modifiers(-STATIC, FINAL)
            .fieldType(double.class)
            .findAbsolute("PacketPlayOutSpawnEntity.x", "PacketPlayOutSpawnEntity.y", "PacketPlayOutSpawnEntity.z");
        field(constructs().first("PacketPlayOutSpawnEntity").getDeclaringClass())
            .modifiers(-STATIC, FINAL)
            .fieldType(byte.class)
            .findAbsolute("PacketPlayOutSpawnEntity.xRot", "PacketPlayOutSpawnEntity.yRot", "PacketPlayOutSpawnEntity.yHeadRot");

        // newClientboundRemoveEntitiesPacket
        construct(NETWORK_PROTOCOL_GAME.getClass("PacketPlayOutEntityDestroy"))
            .parameterTypes(V_1_17.isGameVersion() ? int.class : int[].class)
            .findFirst("PacketPlayOutEntityDestroy");

        // newClientboundOpenScreenPacket
        construct(NETWORK_PROTOCOL_GAME.getClass("PacketPlayOutOpenWindow"))
            .parameterTypes(int.class, WORLD_INVENTORY.getClass("Containers"), NETWORK_CHAT.getClass("IChatBaseComponent"))
            .findFirst("PacketPlayOutOpenWindow");

        // newClientboundContainerClosePacket
        construct(NETWORK_PROTOCOL_GAME.getClass("PacketPlayOutCloseWindow"))
            .parameterTypes(int.class)
            .findFirst("PacketPlayOutCloseWindow");

        this.f = fields();
        this.m = methods();
        this.c = constructs();
        this.magmaCubeType = getMagmaCubeType();
    }

    @Override
    public void sendAllPacket(@NotNull Object packet) throws ReflectiveOperationException {
        var temp = new Object[] { packet };
        var connection = f.first("EntityPlayer.connection");
        var sendPacket = m.first("PlayerConnection.sendPacket");
        for (var player : Bukkit.getOnlinePlayers()) {
            sendPacket.invoke(connection.get(getServerPlayer(player)), temp);
        }
    }

    @Override
    public void sendAllPackets(@NotNull Object... packets) throws ReflectiveOperationException {
        var temp = new Object[] { null };
        var connection = f.first("EntityPlayer.connection");
        var sendPacket = m.first("PlayerConnection.sendPacket");
        for (var player : Bukkit.getOnlinePlayers()) {
            var playerConnection = connection.get(getServerPlayer(player));
            for (var packet : packets) {
                temp[0] = packet; sendPacket.invoke(playerConnection, temp);
            }
        }
    }

    @Override
    public void sendPacket(@NotNull Object serverPlayer, @NotNull Object packet) throws ReflectiveOperationException {
        m.invoke("PlayerConnection.sendPacket", f.getObject("EntityPlayer.connection", serverPlayer), packet);
    }

    @Override
    public void sendPackets(@NotNull Object serverPlayer, @NotNull Object... packets) throws ReflectiveOperationException {
        var temp = new Object[] { null };
        var connection = f.getObject("EntityPlayer.connection", serverPlayer);
        var sendPacket = m.first("PlayerConnection.sendPacket");
        for (var packet : packets) {
            temp[0] = packet; sendPacket.invoke(connection, temp);
        }
    }

    @Override
    @NotNull
    public Object getEntity(@NotNull Entity entity) throws ReflectiveOperationException {
        return m.invoke("CraftEntity.getHandle", entity);
    }

    @Override
    @NotNull
    public Object getServerPlayer(@NotNull Player player) throws ReflectiveOperationException {
        return m.invoke("CraftPlayer.getHandle", player);
    }

    @Override
    @NotNull
    public Object getServerLevel(@NotNull World world) throws ReflectiveOperationException {
        return m.invoke("CraftWorld.getHandle", world);
    }

    @Override
    @NotNull
    public Object getComponentFromJson(@NotNull String json) throws ReflectiveOperationException {
        if (KyoriGson.isJsonInValid(json)) {
            json = KyoriGson.toJson(KyoriLegacy.fromString(json));
        }
        return m.invokeStatic("ChatSerializer.fromJson", json);
    }

    @Override
    @NotNull
    public String getComponentToJson(@NotNull Object component) throws ReflectiveOperationException {
        return (String) m.invokeStatic("ChatSerializer.toJson", component);
    }

    @Override
    @NotNull
    public Integer getContainerId(@NotNull Object containerMenu) throws ReflectiveOperationException {
        return valueOf(f.getInt("Container.containerId", containerMenu));
    }

    @Override
    @NotNull
    public Integer getNextContainerId(@NotNull Object serverPlayer) throws ReflectiveOperationException {
        return (Integer) m.invoke("EntityPlayer.nextContainerCounter", serverPlayer);
    }

    @Override
    public void initMenu(@NotNull Object serverPlayer, @NotNull Object containerMenu) throws ReflectiveOperationException {
        m.invoke("EntityPlayer.initMenu", serverPlayer, containerMenu);
    }

    @Override
    public void setActiveContainer(@NotNull Object serverPlayer, @NotNull Object containerMenu) throws ReflectiveOperationException {
        f.setObject("EntityHuman.activeContainer", serverPlayer, containerMenu);
    }

    @Override
    @NotNull
    public Object getActiveContainer(@NotNull Object serverPlayer) throws ReflectiveOperationException {
        return f.getObject("EntityHuman.activeContainer", serverPlayer);
    }

    @Override
    @NotNull
    public Object getDefaultContainer(@NotNull Object serverPlayer) throws ReflectiveOperationException {
        return f.getObject("EntityHuman.defaultContainer", serverPlayer);
    }

    @Override
    public void handleInventoryCloseEvent(@NotNull Object serverPlayer) throws ReflectiveOperationException {
        m.invokeStatic("CraftEventFactory.handleInventoryCloseEvent", serverPlayer);
    }

    @Override
    @NotNull
    public InventoryView getBukkitView(@NotNull Object containerMenu) throws ReflectiveOperationException {
        return (InventoryView) m.invoke("Container.getBukkitView", containerMenu);
    }

    @Override
    @NotNull
    public List<Entity> getEntities(@NotNull CommandSender sender, @NotNull Vector position, @NotNull String selector) throws ReflectiveOperationException {
        var entitySelector = m.invoke("ArgumentEntity.parse", c.newInstance("ArgumentEntity", BOOL_ARRAY_FALSE_2), new StringReader(selector));
        var commandWrapper = m.invoke("CommandListenerWrapper.withPosition", m.invokeStatic("VanillaCommandWrapper.getListener", sender), newVec3(position.getX(), position.getY(), position.getZ()));
        var findEntities = (List<?>) m.invoke("EntitySelector.findEntities", entitySelector, commandWrapper);
        var newEntities = new ObjectArrayList<Entity>(findEntities.size());
        var toBukkit = m.first("Entity.getBukkitEntity");
        for (int i = 0, l = findEntities.size(); i < l; i++) {
            newEntities.add((Entity) toBukkit.invoke(findEntities.get(i), EMPTY_OBJECT_ARRAY));
        }
        return newEntities;
    }

    @Override
    @NotNull
    public Object newVec3(double x, double y, double z) throws ReflectiveOperationException {
        return c.newInstance("Vec3D", x, y, z);
    }

    @Override
    @NotNull
    public Object newAnvilMenu(@NotNull Object serverPlayer, @NotNull Object serverLevel, @NotNull Object title) throws ReflectiveOperationException {
        var containerAccess = m.invokeStatic("ContainerAccess.create", serverLevel, c.newInstance("BlockPosition", INT_ARRAY_ZERO_3));
        var containerAnvil = c.newInstance("ContainerAnvil", getNextContainerId(serverPlayer), f.getObject("EntityHuman.playerInventory", serverPlayer), containerAccess);
        m.invoke("Container.setTitle", containerAnvil, title);
        f.setBoolean("Container.checkReachable", containerAnvil, false);
        return containerAnvil;
    }

    @Override
    @NotNull
    public Object newMagmaCube(@NotNull Object serverLevel) throws ReflectiveOperationException {
        return c.newInstance("EntityMagmaCube", magmaCubeType, serverLevel);
    }

    @Override
    @NotNull
    public Object newCraftMagmaCube(@NotNull Object magmaCube) throws ReflectiveOperationException {
        return c.newInstance("CraftMagmaCube", Bukkit.getServer(), magmaCube);
    }

    @Override
    @NotNull
    public Object newClientboundSetEntityDataPacket(@NotNull Object entity, @NotNull Integer entityId) throws ReflectiveOperationException {
        var watcher = m.invoke("Entity.getEntityData", entity);
        if (V_1_19_3.isSupported()) {
            return c.newInstance("PacketPlayOutEntityMetadata", entityId, m.invoke("DataWatcher.getNonDefaultValues", watcher));
        } else {
            return c.newInstance("PacketPlayOutEntityMetadata", entityId, watcher, true);
        }
    }

    @Override
    @NotNull
    public Object newClientboundAddEntityPacket(@NotNull Object entity, double x, double y, double z) throws ReflectiveOperationException {
        var packetPlayOutSpawnEntity = c.newInstance("PacketPlayOutSpawnEntity", entity);
        f.setDouble("PacketPlayOutSpawnEntity.x", packetPlayOutSpawnEntity, x);
        f.setDouble("PacketPlayOutSpawnEntity.y", packetPlayOutSpawnEntity, y);
        f.setDouble("PacketPlayOutSpawnEntity.z", packetPlayOutSpawnEntity, z);
        f.setByte("PacketPlayOutSpawnEntity.xRot", packetPlayOutSpawnEntity, BYTE_ZERO);
        f.setByte("PacketPlayOutSpawnEntity.yRot", packetPlayOutSpawnEntity, BYTE_ZERO);
        f.setByte("PacketPlayOutSpawnEntity.yHeadRot", packetPlayOutSpawnEntity, BYTE_ZERO);
        return packetPlayOutSpawnEntity;
    }

    @Override
    @NotNull
    public Object newClientboundRemoveEntitiesPacket(int... entityIds) throws ReflectiveOperationException {
        var packetPlayOutEntityDestroy = c.first("PacketPlayOutEntityDestroy");
        if (V_1_17.isGameVersion()) {
            Object[] temp = { null }, packets = new Object[entityIds.length];
            for (int i = 0, l = entityIds.length; i < l; i++) {
                temp[0] = entityIds[i]; packets[i] = packetPlayOutEntityDestroy.newInstance(temp);
            }
            return packets;
        } else {
            return packetPlayOutEntityDestroy.newInstance(entityIds);
        }
    }

    @Override
    @NotNull
    public Object newClientboundOpenScreenPacket(@NotNull Object containerMenu, @NotNull Object title) throws ReflectiveOperationException {
        return c.newInstance("PacketPlayOutOpenWindow", getContainerId(containerMenu), m.invoke("Container.getType", containerMenu), title);
    }

    @Override
    @NonNull
    public Object newClientboundOpenScreenPacket(@NotNull Integer containerId, @NotNull Object menuType, @NotNull Object title) throws ReflectiveOperationException {
        return c.newInstance("PacketPlayOutOpenWindow", containerId, menuType, title);
    }

    @Override
    @NonNull
    public Object newClientboundContainerClosePacket(@NotNull Integer containerId) throws ReflectiveOperationException {
        return c.newInstance("PacketPlayOutCloseWindow", containerId);
    }
}