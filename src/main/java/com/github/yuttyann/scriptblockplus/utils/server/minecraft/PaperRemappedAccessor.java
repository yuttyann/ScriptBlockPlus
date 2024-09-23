
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

import com.github.yuttyann.scriptblockplus.utils.reflect.matcher.ReflectionMatcher.ConstructorStore;
import com.github.yuttyann.scriptblockplus.utils.reflect.matcher.ReflectionMatcher.FieldStore;
import com.github.yuttyann.scriptblockplus.utils.reflect.matcher.ReflectionMatcher.MethodStore;
import com.mojang.brigadier.StringReader;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

/**
 * FBS-Bukkit PaperRemappedAccessor
 * @author yuttyann44581
 */
public class PaperRemappedAccessor implements NativeAccessor {

    private final FieldStore f;
    private final MethodStore m;
    private final ConstructorStore c;

    private final Object blockPosZero;
    private final Object magmaCubeType;
 
    PaperRemappedAccessor() throws ReflectiveOperationException {
        // sendPacket
        field(SERVER_LEVEL.getClass("ServerPlayer"))
            .modifiers(PUBLIC, -STATIC, -FINAL)
            .name("connection")
            .fieldType(SERVER_NETWORK.getClass("ServerGamePacketListenerImpl"))
            .findFirst("ServerPlayer.connection");
        method(SERVER_NETWORK.getClass("ServerCommonPacketListenerImpl"))
            .modifiers(PUBLIC, -STATIC, -FINAL)
            .name("send")
            .parameterTypes(NETWORK_PROTOCOL.getClass("Packet"))
            .returnType(void.class)
            .findFirst("ServerGamePacketListenerImpl.sendPacket");

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
            .returnType(SERVER_LEVEL.getClass("ServerPlayer"))
            .emptyParameterTypes()
            .findFirst("CraftPlayer.getHandle");

        // getServerLevel
        method(CRAFTBUKKIT.getClass("CraftWorld"))
            .modifiers(PUBLIC, -STATIC, -FINAL)
            .name("getHandle")
            .returnType(SERVER_LEVEL.getClass("ServerLevel"))
            .emptyParameterTypes()
            .findFirst("CraftWorld.getHandle");

        // getComponentFromJson & getComponentToJson & setItemTag & getItemTag
        method(CRAFTBUKKIT.getClass("CraftRegistry"))
            .modifiers(PUBLIC, STATIC)
            .name("getMinecraftRegistry")
            .returnType(CORE.getClass("RegistryAccess"))
            .emptyParameterTypes()
            .findFirst("CraftRegistry.getMinecraftRegistry");

        // getComponentFromJson
        method(NETWORK_CHAT.getClass("Component$Serializer"))
            .modifiers(PUBLIC, STATIC)
            .name("fromJson")
            .returnType(NETWORK_CHAT.getClass("MutableComponent"))
            .parameterTypes(String.class, CORE.getClass("HolderLookup$Provider"))
            .findFirst("Serializer.fromJson");

        // getComponentToJson
        method(NETWORK_CHAT.getClass("Component$Serializer"))
            .modifiers(PUBLIC, STATIC)
            .name("toJson")
            .returnType(String.class)
            .parameterTypes(NETWORK_CHAT.getClass("Component"), CORE.getClass("HolderLookup$Provider"))
            .findFirst("Serializer.toJson");

        // getContainerId
        field(WORLD_INVENTORY.getClass("AbstractContainerMenu"))
            .modifiers(PUBLIC, -STATIC, FINAL)
            .name("containerId")
            .fieldType(int.class)
            .findFirst("ContainerMenu.containerId");

        // getNextContainerId
        method(SERVER_LEVEL.getClass("ServerPlayer"))
            .modifiers(PUBLIC, -STATIC, -FINAL)
            .name("nextContainerCounter")
            .returnType(int.class)
            .emptyParameterTypes()
            .findFirst("ServerPlayer.nextContainerCounter");

        // setActiveContainer & getActiveContainer
        field(WORLD_ENTITY_PLAYER.getClass("Player"))
            .modifiers(PUBLIC, -STATIC, -FINAL)
            .name("containerMenu")
            .fieldType(WORLD_INVENTORY.getClass("AbstractContainerMenu"))
            .findFirst("Player.activeContainer");

        // getDefaultContainer
        field(WORLD_ENTITY_PLAYER.getClass("Player"))
            .modifiers(PUBLIC, -STATIC, FINAL)
            .name("inventoryMenu")
            .fieldType(WORLD_INVENTORY.getClass("InventoryMenu"))
            .findFirst("Player.defaultContainer");

        // initMenu
        method(SERVER_LEVEL.getClass("ServerPlayer"))
            .modifiers(PUBLIC, -STATIC, -FINAL)
            .returnType(void.class)
            .parameterTypes(WORLD_INVENTORY.getClass("AbstractContainerMenu"))
            .findFirst("ServerPlayer.initMenu");

        // handleInventoryCloseEvent
        method(EVENT.getClass("CraftEventFactory"))
            .modifiers(PUBLIC, STATIC)
            .name("handleInventoryCloseEvent")
            .returnType(void.class)
            .parameterTypes(WORLD_ENTITY_PLAYER.getClass("Player"))
            .findFirst("CraftEventFactory.handleInventoryCloseEvent");

        // getMenuType - AbstractContainerMenu
        method(WORLD_INVENTORY.getClass("AbstractContainerMenu"))
            .modifiers(PUBLIC, -STATIC, -FINAL)
            .name("getType")
            .returnType(WORLD_INVENTORY.getClass("MenuType"))
            .emptyParameterTypes()
            .findFirst("ContainerMenu.getType");

        // setMenuTitle & newAnvilMenu
        method(WORLD_INVENTORY.getClass("AbstractContainerMenu"))
            .modifiers(PUBLIC, -STATIC, FINAL)
            .name("setTitle")
            .returnType(void.class)
            .parameterTypes(NETWORK_CHAT.getClass("Component"))
            .findFirst("ContainerMenu.setTitle");

        // getBukkitView
        method(WORLD_INVENTORY.getClass("AbstractContainerMenu"))
            .modifiers(PUBLIC)
            .name("getBukkitView")
            .returnType(InventoryView.class)
            .emptyParameterTypes()
            .findFirst("Container.getBukkitView");

        // getEntities
        construct(COMMANDS_ARGUMENTS.getClass("EntityArgument"))
            .parameterTypes(boolean.class, boolean.class)
            .findFirst("EntityArgument");
        method(COMMANDS_ARGUMENTS.getClass("EntityArgument"))
            .modifiers(PUBLIC, -STATIC, -FINAL)
            .name("parse")
            .returnType(COMMANDS_ARGUMENTS_SELECTOR.getClass("EntitySelector"))
            .parameterTypes(StringReader.class)
            .findFirst("EntityArgument.parse");
        method(COMMAND.getClass("VanillaCommandWrapper"))
            .modifiers(STATIC)
            .name("getListener")
            .returnType(COMMANDS.getClass("CommandSourceStack"))
            .parameterTypes(CommandSender.class)
            .findFirst("VanillaCommandWrapper.getListener");
        method(COMMANDS.getClass("CommandSourceStack"))
            .modifiers(PUBLIC, -STATIC, -FINAL)
            .name("withPosition")
            .returnType(COMMANDS.getClass("CommandSourceStack"))
            .parameterTypes(WORLD_PHYS.getClass("Vec3"))
            .findFirst("CommandSourceStack.withPosition");
        method(COMMANDS_ARGUMENTS_SELECTOR.getClass("EntitySelector"))
            .modifiers(PUBLIC, -STATIC, -FINAL)
            .name("findEntities")
            .returnType(List.class)
            .parameterTypes(COMMANDS.getClass("CommandSourceStack"))
            .findFirst("EntitySelector.findEntities");
        method(WORLD_ENTITY.getClass("Entity"))
            .modifiers(PUBLIC, -STATIC, -FINAL)
            .name("getBukkitEntity")
            .returnType(ENTITY.getClass("CraftEntity"))
            .emptyParameterTypes()
            .findFirst("Entity.getBukkitEntity");

        // newVec3
        construct(WORLD_PHYS.getClass("Vec3"))
            .parameterTypes(double.class, double.class, double.class)
            .findFirst("Vec3");

        // newAnvilMenu & newClientboundAddEntityPacket
        construct(CORE.getClass("BlockPos"))
            .parameterTypes(int.class, int.class, int.class)
            .findFirst("BlockPos");

        // newAnvilMenu
        field(WORLD_ENTITY_PLAYER.getClass("Player"))
            .modifiers(-STATIC, FINAL)
            .fieldType(WORLD_ENTITY_PLAYER.getClass("Inventory"))
            .findFirst("Player.inventory");
        method(WORLD_INVENTORY.getClass("ContainerLevelAccess"))
            .modifiers(STATIC)
            .returnType(WORLD_INVENTORY.getClass("ContainerLevelAccess"))
            .parameterTypes(WORLD_LEVEL.getClass("Level"), CORE.getClass("BlockPos"))
            .findFirst("ContainerLevelAccess.create");
        construct(WORLD_INVENTORY.getClass("AnvilMenu"))
            .parameterTypes(int.class, WORLD_ENTITY_PLAYER.getClass("Inventory"), WORLD_INVENTORY.getClass("ContainerLevelAccess"))
            .findFirst("AnvilMenu");
        field(WORLD_INVENTORY.getClass("AbstractContainerMenu"))
            .modifiers(PUBLIC, -STATIC, -FINAL)
            .name("checkReachable")
            .fieldType(boolean.class)
            .findFirst("ContainerMenu.checkReachable");

        // newMagmaCube
        construct(WORLD_ENTITY_MONSTER.getClass("MagmaCube"))
            .parameterTypes(WORLD_ENTITY.getClass("EntityType"), WORLD_LEVEL.getClass("Level"))
            .findFirst("MagmaCube");

        // newCraftMagmaCube
        construct(ENTITY.getClass("CraftMagmaCube"))
            .parameterTypes(CRAFTBUKKIT.getClass("CraftServer"), WORLD_ENTITY_MONSTER.getClass("MagmaCube"))
            .findFirst("CraftMagmaCube");

        // newClientboundSetEntityDataPacket
        method(WORLD_ENTITY.getClass("Entity"))
            .modifiers(PUBLIC, -STATIC, -FINAL)
            .name("getEntityData")
            .returnType(NETWORK_SYNCHER.getClass("SynchedEntityData"))
            .emptyParameterTypes()
            .findFirst("Entity.getEntityData");
        method(NETWORK_SYNCHER.getClass("SynchedEntityData")).deep()
            .modifiers(PUBLIC, -STATIC, -FINAL)
            .name("getNonDefaultValues")
            .returnType(List.class, parameterizedClass(NETWORK_SYNCHER.getClass("SynchedEntityData$DataValue"), unboundWildcard()))
            .emptyParameterTypes()
            .findFirst("SynchedEntityData.getNonDefaultValues");
        construct(NETWORK_PROTOCOL_GAME.getClass("ClientboundSetEntityDataPacket"))
            .parameterTypes(int.class, List.class)
            .findFirst("ClientboundSetEntityDataPacket");

        // newClientboundAddEntityPacket
        construct(NETWORK_PROTOCOL_GAME.getClass("ClientboundAddEntityPacket"))
            .parameterTypes(WORLD_ENTITY.getClass("Entity"), int.class, CORE.getClass("BlockPos"))
            .findFirst("ClientboundAddEntityPacket");
        field(constructs().first("ClientboundAddEntityPacket").getDeclaringClass())
            .modifiers(-STATIC, FINAL)
            .fieldType(double.class)
            .findAbsolute("ClientboundAddEntityPacket.x", "ClientboundAddEntityPacket.y", "ClientboundAddEntityPacket.z");
        field(constructs().first("ClientboundAddEntityPacket").getDeclaringClass())
            .modifiers(-STATIC, FINAL)
            .fieldType(byte.class)
            .findAbsolute("ClientboundAddEntityPacket.xRot", "ClientboundAddEntityPacket.yRot", "ClientboundAddEntityPacket.yHeadRot");

        // newClientboundRemoveEntitiesPacket
        construct(NETWORK_PROTOCOL_GAME.getClass("ClientboundRemoveEntitiesPacket"))
            .parameterTypes(int[].class)
            .findFirst("ClientboundRemoveEntitiesPacket");

        // newClientboundOpenScreenPacket
        construct(NETWORK_PROTOCOL_GAME.getClass("ClientboundOpenScreenPacket"))
            .parameterTypes(int.class, WORLD_INVENTORY.getClass("MenuType"), NETWORK_CHAT.getClass("Component"))
            .findFirst("ClientboundOpenScreenPacket");

        // newClientboundContainerClosePacket
        construct(NETWORK_PROTOCOL_GAME.getClass("ClientboundContainerClosePacket"))
            .parameterTypes(int.class)
            .findFirst("ClientboundContainerClosePacket");

        this.f = fields();
        this.m = methods();
        this.c = constructs();
        this.blockPosZero = c.newInstance("BlockPos", INT_ARRAY_ZERO_3);
        this.magmaCubeType = field(WORLD_ENTITY.getClass("EntityType")).modifiers(STATIC).name("MAGMA_CUBE").fieldType(WORLD_ENTITY.getClass("EntityType")).findFirst().get(null);
    }

    @Override
    public void sendAllPacket(@NotNull Object packet) throws ReflectiveOperationException {
        var temp = new Object[] { packet };
        var connection = f.first("ServerPlayer.connection");
        var sendPacket = m.first("ServerGamePacketListenerImpl.sendPacket");
        for (var player : Bukkit.getOnlinePlayers()) {
            sendPacket.invoke(connection.get(getServerPlayer(player)), temp);
        }
    }

    @Override
    public void sendAllPackets(@NotNull Object... packets) throws ReflectiveOperationException {
        var temp = new Object[] { null };
        var connection = f.first("ServerPlayer.connection");
        var sendPacket = m.first("ServerGamePacketListenerImpl.sendPacket");
        for (var player : Bukkit.getOnlinePlayers()) {
            var serverGamePacketListener = connection.get(getServerPlayer(player));
            for (var packet : packets) {
                temp[0] = packet; sendPacket.invoke(serverGamePacketListener, temp);
            }
        }
    }

    @Override
    public void sendPacket(@NotNull Object serverPlayer, @NotNull Object packet) throws ReflectiveOperationException {
        m.invoke("ServerGamePacketListenerImpl.sendPacket", f.getObject("ServerPlayer.connection", serverPlayer), packet);
    }

    @Override
    public void sendPackets(@NotNull Object serverPlayer, @NotNull Object... packets) throws ReflectiveOperationException {
        var temp = new Object[] { null };
        var connection = f.getObject("ServerPlayer.connection", serverPlayer);
        var sendPacket = m.first("ServerGamePacketListenerImpl.sendPacket");
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
        return m.invokeStatic("Serializer.fromJson", json, m.invokeStatic("CraftRegistry.getMinecraftRegistry"));
    }

    @Override
    @NotNull
    public String getComponentToJson(@NotNull Object component) throws ReflectiveOperationException {
        return (String) m.invokeStatic("Serializer.toJson", component, m.invokeStatic("CraftRegistry.getMinecraftRegistry"));
    }

    @Override
    @NotNull
    public Integer getContainerId(@NotNull Object containerMenu) throws ReflectiveOperationException {
        return valueOf(f.getInt("ContainerMenu.containerId", containerMenu));
    }

    @Override
    @NotNull
    public Integer getNextContainerId(@NotNull Object serverPlayer) throws ReflectiveOperationException {
        return (Integer) m.invoke("ServerPlayer.nextContainerCounter", serverPlayer);
    }

    @Override
    public void initMenu(@NotNull Object serverPlayer, @NotNull Object containerMenu) throws ReflectiveOperationException {
        m.invoke("ServerPlayer.initMenu", serverPlayer, containerMenu);
    }

    @Override
    public void setActiveContainer(@NotNull Object serverPlayer, @NotNull Object containerMenu) throws ReflectiveOperationException {
        f.setObject("Player.activeContainer", serverPlayer, containerMenu);
    }

    @Override
    @NotNull
    public Object getActiveContainer(@NotNull Object serverPlayer) throws ReflectiveOperationException {
        return f.getObject("Player.activeContainer", serverPlayer);
    }

    @Override
    @NotNull
    public Object getDefaultContainer(@NotNull Object serverPlayer) throws ReflectiveOperationException {
        return f.getObject("Player.defaultContainer", serverPlayer);
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
        var entitySelector = m.invoke("EntityArgument.parse", c.newInstance("EntityArgument", BOOL_ARRAY_FALSE_2), new StringReader(selector));
        var commandWrapper = m.invoke("CommandSourceStack.withPosition", m.invokeStatic("VanillaCommandWrapper.getListener", sender), newVec3(position.getX(), position.getY(), position.getZ()));
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
        return c.newInstance("Vec3", x, y, z);
    }

    @Override
    @NotNull
    public Object newAnvilMenu(@NotNull Object serverPlayer, @NotNull Object serverLevel, @NotNull Object title) throws ReflectiveOperationException {
        var containerAccess = m.invokeStatic("ContainerLevelAccess.create", serverLevel, c.newInstance("BlockPos", INT_ARRAY_ZERO_3));
        var anvilMenu = c.newInstance("AnvilMenu", getNextContainerId(serverPlayer), f.getObject("Player.inventory", serverPlayer), containerAccess);
        m.invoke("ContainerMenu.setTitle", anvilMenu, title);
        f.setBoolean("ContainerMenu.checkReachable", anvilMenu, false);
        return anvilMenu;
    }

    @Override
    @NotNull
    public Object newMagmaCube(@NotNull Object serverLevel) throws ReflectiveOperationException {
        return c.newInstance("MagmaCube", magmaCubeType, serverLevel);
    }

    @Override
    @NotNull
    public Object newCraftMagmaCube(@NotNull Object magmaCube) throws ReflectiveOperationException {
        return c.newInstance("CraftMagmaCube", Bukkit.getServer(), magmaCube);
    }

    @Override
    @NotNull
    public Object newClientboundSetEntityDataPacket(@NotNull Object entity, @NotNull Integer entityId) throws ReflectiveOperationException {
        return c.newInstance("ClientboundSetEntityDataPacket", entityId, m.invoke("SynchedEntityData.getNonDefaultValues", m.invoke("Entity.getEntityData", entity)));
    }

    @Override
    @NotNull
    public Object newClientboundAddEntityPacket(@NotNull Object entity, double x, double y, double z) throws ReflectiveOperationException {
        var clientboundAddEntityPacket = c.newInstance("ClientboundAddEntityPacket", entity, 0, blockPosZero);
        f.setDouble("ClientboundAddEntityPacket.x", clientboundAddEntityPacket, x);
        f.setDouble("ClientboundAddEntityPacket.y", clientboundAddEntityPacket, y);
        f.setDouble("ClientboundAddEntityPacket.z", clientboundAddEntityPacket, z);
        f.setByte("ClientboundAddEntityPacket.xRot", clientboundAddEntityPacket, BYTE_ZERO);
        f.setByte("ClientboundAddEntityPacket.yRot", clientboundAddEntityPacket, BYTE_ZERO);
        f.setByte("ClientboundAddEntityPacket.yHeadRot", clientboundAddEntityPacket, BYTE_ZERO);
        return clientboundAddEntityPacket;
    }

    @Override
    @NotNull
    public Object newClientboundRemoveEntitiesPacket(int... entityIds) throws ReflectiveOperationException {
        return c.newInstance("ClientboundRemoveEntitiesPacket", entityIds);
    }

    @Override
    @NotNull
    public Object newClientboundOpenScreenPacket(@NotNull Object containerMenu, @NotNull Object title) throws ReflectiveOperationException {
        return c.newInstance("ClientboundOpenScreenPacket", getContainerId(containerMenu), m.invoke("ContainerMenu.getType", containerMenu), title);
    }

    @Override
    @NonNull
    public Object newClientboundOpenScreenPacket(@NotNull Integer containerId, @NotNull Object menuType, @NotNull Object title) throws ReflectiveOperationException {
        return c.newInstance("ClientboundOpenScreenPacket", containerId, menuType, title);
    }

    @Override
    @NonNull
    public Object newClientboundContainerClosePacket(@NotNull Integer containerId) throws ReflectiveOperationException {
        return c.newInstance("ClientboundContainerClosePacket", containerId);
    }
}