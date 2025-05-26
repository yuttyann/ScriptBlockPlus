package com.github.yuttyann.scriptblockplus.utils.server.minecraft;

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
import static com.google.common.base.Preconditions.*;

import java.util.List;

import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.springframework.lang.NonNull;

/**
 * Corelate-Bukkit Minecraft
 * @author yuttyann44581
 */
public final class Minecraft {

    private static NativeAccessor accessor;

    private Minecraft() { }

    public static void setAccessor(@NonNull NativeAccessor accessor) {
        Minecraft.accessor = checkNotNull(accessor);
    }

    public static void sendAllPacket(@NotNull Object packet) throws ReflectiveOperationException {
        accessor.sendAllPacket(packet);
    }

    public static void sendAllPackets(@NotNull Object... packets) throws ReflectiveOperationException {
        accessor.sendAllPackets(packets);
    }

    public static void sendPacket(@NotNull Object serverPlayer, @NotNull Object packet) throws ReflectiveOperationException {
        accessor.sendPacket(serverPlayer, packet);
    }

    public static void sendPackets(@NotNull Object serverPlayer, @NotNull Object... packets) throws ReflectiveOperationException {
        accessor.sendPackets(serverPlayer, packets);
    }

    @NotNull
    public static Object getEntity(@NotNull Entity entity) throws ReflectiveOperationException {
        return accessor.getEntity(entity);
    }

    @NotNull
    public static Object getServerPlayer(@NotNull Player player) throws ReflectiveOperationException {
        return accessor.getServerPlayer(player);
    }

    @NotNull
    public static Object getServerLevel(@NotNull World world) throws ReflectiveOperationException {
        return accessor.getServerLevel(world);
    }

    @NotNull
    public static Object getComponentFromJson(@NotNull String json) throws ReflectiveOperationException {
        return accessor.getComponentFromJson(json);
    }

    @NotNull
    public static Integer getContainerId(@NotNull Object containerMenu) throws ReflectiveOperationException {
        return accessor.getContainerId(containerMenu);
    }

    @NotNull
    public static Integer getNextContainerId(@NotNull Object serverPlayer) throws ReflectiveOperationException {
        return accessor.getNextContainerId(serverPlayer);
    }

    public static void setActiveContainer(@NotNull Object serverPlayer, @NotNull Object containerMenu) throws ReflectiveOperationException {
        accessor.setActiveContainer(serverPlayer, containerMenu);
    }

    @NotNull
    public static Object getActiveContainer(@NotNull Object serverPlayer) throws ReflectiveOperationException {
        return accessor.getActiveContainer(serverPlayer);
    }

    @NotNull
    public static Object getDefaultContainer(@NotNull Object serverPlayer) throws ReflectiveOperationException {
        return accessor.getDefaultContainer(serverPlayer);
    }

    public static void initMenu(@NotNull Object serverPlayer, @NotNull Object containerMenu) throws ReflectiveOperationException {
        accessor.initMenu(serverPlayer, containerMenu);
    }

    public static void handleInventoryCloseEvent(@NotNull Object serverPlayer) throws ReflectiveOperationException {
        accessor.handleInventoryCloseEvent(serverPlayer);
    }

    @NotNull
    public static InventoryView getBukkitView(@NotNull Object containerMenu) throws ReflectiveOperationException {
        return accessor.getBukkitView(containerMenu);
    }

    @NotNull
    public static List<Entity> getEntities(@NotNull CommandSender sender, @NotNull Vector position, @NotNull String selector) throws ReflectiveOperationException {
        return accessor.getEntities(sender, position, selector);
    }

    @NotNull
    public static Object newVec3(double x, double y, double z) throws ReflectiveOperationException {
        return accessor.newVec3(x, y, z);
    }

    @NotNull
    public static Object newAnvilMenu(@NotNull Object serverPlayer, @NotNull Object serverLevel, @NotNull Object title) throws ReflectiveOperationException {
        return accessor.newAnvilMenu(serverPlayer, serverLevel, title);
    }

    @NotNull
    public static Object newMagmaCube(@NotNull Object serverLevel) throws ReflectiveOperationException {
        return accessor.newMagmaCube(serverLevel);
    }

    @NotNull
    public static Object newCraftMagmaCube(@NotNull Object magmaCube) throws ReflectiveOperationException {
        return accessor.newCraftMagmaCube(magmaCube);
    }

    @NotNull
    public static Object newClientboundSetEntityDataPacket(@NotNull Object entity, @NotNull Integer entityId) throws ReflectiveOperationException {
        return accessor.newClientboundSetEntityDataPacket(entity, entityId);
    }

    @NotNull
    public static Object newClientboundAddEntityPacket(@NotNull Object entity, double x, double y, double z) throws ReflectiveOperationException {
        return accessor.newClientboundAddEntityPacket(entity, x, y, z);
    }

    @NotNull
    public static Object newClientboundRemoveEntitiesPacket(int... entityIds) throws ReflectiveOperationException {
        return accessor.newClientboundRemoveEntitiesPacket(entityIds);
    }

    @NonNull
    public static Object newClientboundOpenScreenPacket(@NotNull Object containerMenu, @NotNull Object title) throws ReflectiveOperationException {
        return accessor.newClientboundOpenScreenPacket(containerMenu, title);
    }

    @NonNull
    public static Object newClientboundOpenScreenPacket(@NotNull Integer containerId, @NotNull Object menuType, @NotNull Object title) throws ReflectiveOperationException {
        return accessor.newClientboundOpenScreenPacket(containerId, menuType, title);
    }

    @NonNull
    public static Object newClientboundContainerClosePacket(@NotNull Integer containerId) throws ReflectiveOperationException {
        return accessor.newClientboundContainerClosePacket(containerId);
    }
}