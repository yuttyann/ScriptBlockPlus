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

import com.github.yuttyann.scriptblockplus.utils.NMSHelper;
import com.github.yuttyann.scriptblockplus.utils.kyori.KyoriGson;
import com.github.yuttyann.scriptblockplus.utils.kyori.KyoriLegacy;
import com.github.yuttyann.scriptblockplus.utils.reflect.matcher.ReflectionMatcher.ConstructorStore;
import com.github.yuttyann.scriptblockplus.utils.reflect.matcher.ReflectionMatcher.FieldStore;
import com.github.yuttyann.scriptblockplus.utils.reflect.matcher.ReflectionMatcher.MethodStore;
import com.mojang.brigadier.StringReader;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

/**
 * ScriptBlockPlus SpigotAccessor_v1_9
 * @author yuttyann44581
 */
public class SpigotAccessor_v1_9 implements NativeAccessor {

    private final FieldStore f;
    private final MethodStore m;
    private final ConstructorStore c;
 
    SpigotAccessor_v1_9() throws ReflectiveOperationException {
        // sendPacket
        field(LEGACY_PATH.getClass("EntityPlayer"))
            .fieldType(LEGACY_PATH.getClass("PlayerConnection"))
            .findFirst("EntityPlayer.connection");
        method(LEGACY_PATH.getClass("PlayerConnection"))
            .modifiers(PUBLIC, -STATIC, -FINAL)
            .parameterTypes(LEGACY_PATH.getClass("Packet"))
            .returnType(void.class)
            .findFirst("PlayerConnection.sendPacket");

        // getEntity
        method(ENTITY.getClass("CraftEntity"))
            .name("getHandle")
            .emptyParameterTypes()
            .findFirst("CraftEntity.getHandle");

        // getServerPlayer
        method(ENTITY.getClass("CraftPlayer"))
            .name("getHandle")
            .emptyParameterTypes()
            .findFirst("CraftPlayer.getHandle");

        // getServerLevel
        method(CRAFTBUKKIT.getClass("CraftWorld"))
            .modifiers(PUBLIC, -STATIC, -FINAL)
            .name("getHandle")
            .emptyParameterTypes()
            .findFirst("CraftWorld.getHandle");

        // getComponentFromJson
        method(LEGACY_PATH.getClass("IChatBaseComponent$ChatSerializer"))
            .modifiers(PUBLIC, STATIC)
            .name("a")
            .parameterTypes(String.class)
            .findFirst("ChatSerializer.fromJson");

        // setActiveContainer & getActiveContainer
        field(LEGACY_PATH.getClass("EntityHuman"))
            .name("activeContainer")
            .fieldType(LEGACY_PATH.getClass("Container"))
            .findFirst("EntityHuman.activeContainer");

        // getDefaultContainer
        field(LEGACY_PATH.getClass("EntityHuman"))
            .name("defaultContainer")
            .fieldType(LEGACY_PATH.getClass("Container"))
            .findFirst("EntityHuman.defaultContainer");

        // initMenu
        method(LEGACY_PATH.getClass("Container"))
            .modifiers(PUBLIC, -STATIC, -FINAL)
            .name("addSlotListener")
            .returnType(void.class)
            .parameterTypes(LEGACY_PATH.getClass("ICrafting"))
            .findFirst("EntityPlayer.initMenu");

        // handleInventoryCloseEvent
        method(EVENT.getClass("CraftEventFactory"))
            .modifiers(PUBLIC, STATIC)
            .name("handleInventoryCloseEvent")
            .returnType(void.class)
            .parameterTypes(LEGACY_PATH.getClass("EntityHuman"))
            .findFirst("CraftEventFactory.handleInventoryCloseEvent");

        // getBukkitView
        method(LEGACY_PATH.getClass("Container"))
            .modifiers(PUBLIC)
            .name("getBukkitView")
            .returnType(InventoryView.class)
            .emptyParameterTypes()
            .findFirst("Container.getBukkitView");

        // getEntities
        if (V_1_13.isSupported()) {
            construct(LEGACY_PATH.getClass("ArgumentEntity"))
                .parameterTypes(boolean.class, boolean.class)
                .findFirst("ArgumentEntity");
            method(LEGACY_PATH.getClass("ArgumentEntity"))
                .modifiers(PUBLIC, -STATIC, -FINAL)
                .returnType(LEGACY_PATH.getClass("EntitySelector"))
                .parameterTypes(StringReader.class)
                .findFirst("ArgumentEntity.parse");
            method(LEGACY_PATH.getClass("CommandListenerWrapper"))
                .modifiers(PUBLIC, -STATIC, -FINAL)
                .returnType(LEGACY_PATH.getClass("CommandListenerWrapper"))
                .parameterTypes(LEGACY_PATH.getClass("Vec3D"))
                .findFirst("CommandListenerWrapper.withPosition");
            method(LEGACY_PATH.getClass("EntitySelector"))
                .modifiers(PUBLIC, -STATIC, -FINAL)
                .returnType(List.class)
                .parameterTypes(LEGACY_PATH.getClass("CommandListenerWrapper"))
                .findFirst("EntitySelector.findEntities");
            method(LEGACY_PATH.getClass("Entity"))
                .modifiers(PUBLIC, -STATIC, -FINAL)
                .name("getBukkitEntity")
                .returnType(ENTITY.getClass("CraftEntity"))
                .emptyParameterTypes()
                .findFirst("Entity.getBukkitEntity");
        }

        // newVec3
        construct(LEGACY_PATH.getClass("Vec3D"))
            .parameterTypes(double.class, double.class, double.class)
            .findFirst("Vec3D");
    
        // newAnvilMenu
        construct(LEGACY_PATH.getClass("BlockPosition"))
            .parameterTypes(int.class, int.class, int.class)
            .findFirst("BlockPosition");
        field(LEGACY_PATH.getClass("EntityHuman"))
            .fieldType(LEGACY_PATH.getClass("PlayerInventory"))
            .findFirst("EntityHuman.playerInventory");
        field(LEGACY_PATH.getClass("Container"))
            .modifiers(PUBLIC)
            .name("checkReachable")
            .fieldType(boolean.class)
            .findFirst("Container.checkReachable");
        construct(LEGACY_PATH.getClass("ContainerAnvil"))
            .parameterTypes(LEGACY_PATH.getClass("PlayerInventory"), LEGACY_PATH.getClass("World"), LEGACY_PATH.getClass("BlockPosition"), LEGACY_PATH.getClass("EntityHuman"))
            .findFirst("ContainerAnvil");

        // newMagmaCube
        construct(LEGACY_PATH.getClass("EntityMagmaCube"))
            .parameterTypes(LEGACY_PATH.getClass("World"))
            .findFirst("EntityMagmaCube");

        // newCraftMagmaCube
        construct(ENTITY.getClass("CraftMagmaCube"))
            .parameterTypes(CRAFTBUKKIT.getClass("CraftServer"), LEGACY_PATH.getClass("EntityMagmaCube"))
            .findFirst("CraftMagmaCube");

        // newClientboundSetEntityDataPacket
        method(LEGACY_PATH.getClass("Entity"))
            .returnType(LEGACY_PATH.getClass("DataWatcher"))
            .parameterTypes(EMPTY_CLASS_ARRAY)
            .findFirst("Entity.getEntityData");
        construct(LEGACY_PATH.getClass("PacketPlayOutEntityMetadata"))
            .parameterTypes(int.class, LEGACY_PATH.getClass("DataWatcher"), boolean.class)
            .findFirst("PacketPlayOutEntityMetadata");

        // newClientboundAddEntityPacket
        construct(LEGACY_PATH.getClass("PacketPlayOutSpawnEntityLiving"))
            .parameterTypes(LEGACY_PATH.getClass("EntityLiving"))
            .findFirst("PacketPlayOutSpawnEntity");
        field(constructs().first("PacketPlayOutSpawnEntity").getDeclaringClass())
            .modifiers(-STATIC)
            .fieldType(double.class)
            .findAbsolute("PacketPlayOutSpawnEntity.x", "PacketPlayOutSpawnEntity.y", "PacketPlayOutSpawnEntity.z");
        field(constructs().first("PacketPlayOutSpawnEntity").getDeclaringClass())
            .modifiers(-STATIC)
            .fieldType(byte.class)
            .findAbsolute("PacketPlayOutSpawnEntity.xRot", "PacketPlayOutSpawnEntity.yRot", "PacketPlayOutSpawnEntity.yHeadRot");

        // newClientboundRemoveEntitiesPacket
        construct(LEGACY_PATH.getClass("PacketPlayOutEntityDestroy"))
            .parameterTypes(int[].class)
            .findFirst("PacketPlayOutEntityDestroy");

        // newClientboundOpenScreenPacket
        construct(LEGACY_PATH.getClass("PacketPlayOutOpenWindow"))
            .parameterTypes(int.class, String.class, LEGACY_PATH.getClass("IChatBaseComponent"))
            .findFirst("PacketPlayOutOpenWindow");

        // newClientboundContainerClosePacket
        construct(LEGACY_PATH.getClass("PacketPlayOutCloseWindow"))
            .parameterTypes(int.class)
            .findFirst("PacketPlayOutCloseWindow");

        this.f = fields();
        this.m = methods();
        this.c = constructs();
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
        throw new UnsupportedOperationException(String.valueOf(containerMenu));
    }

    @Override
    @NotNull
    public Integer getNextContainerId(@NotNull Object serverPlayer) throws ReflectiveOperationException {
        return (Integer) SERVER_LEVEL.invokeMethod(serverPlayer, "EntityPlayer", "nextContainerCounter");
    }

    @Override
    public void initMenu(@NotNull Object serverPlayer, @NotNull Object containerMenu) throws ReflectiveOperationException {
        m.invoke("EntityPlayer.initMenu", containerMenu, serverPlayer);
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
        var commandWrapper = m.invoke("CommandListenerWrapper.withPosition", NMSHelper.getICommandListener(sender), newVec3(position.getX(), position.getY(), position.getZ()));
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
        var blockPosition = c.newInstance("BlockPosition", INT_ARRAY_ZERO_3);
        var containerAnvil = c.newInstance("ContainerAnvil", f.getObject("EntityHuman.playerInventory", serverPlayer), serverLevel, blockPosition, serverPlayer);
        f.setBoolean("Container.checkReachable", containerAnvil, false);
        return containerAnvil;
    }

    @Override
    @NotNull
    public Object newMagmaCube(@NotNull Object serverLevel) throws ReflectiveOperationException {
        return c.newInstance("EntityMagmaCube", serverLevel);
    }

    @Override
    @NotNull
    public Object newCraftMagmaCube(@NotNull Object magmaCube) throws ReflectiveOperationException {
        return c.newInstance("CraftMagmaCube", Bukkit.getServer(), magmaCube);
    }

    @Override
    @NotNull
    public Object newClientboundSetEntityDataPacket(@NotNull Object entity, @NotNull Integer entityId) throws ReflectiveOperationException {
        return c.newInstance("PacketPlayOutEntityMetadata", entityId, m.invoke("Entity.getEntityData", entity), true);
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
        return c.newInstance("PacketPlayOutEntityDestroy", entityIds);
    }

    @Override
    @NotNull
    public Object newClientboundOpenScreenPacket(@NotNull Object containerMenu, @NotNull Object title) throws ReflectiveOperationException {
        throw new UnsupportedOperationException(String.valueOf(containerMenu) + ", " + String.valueOf(title));
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