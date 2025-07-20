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

import static com.github.yuttyann.scriptblockplus.utils.version.McVersion.*;

import java.util.List;

import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.springframework.lang.NonNull;

import com.github.yuttyann.scriptblockplus.utils.server.CraftBukkit;
import com.github.yuttyann.scriptblockplus.utils.server.NetMinecraft;

/**
 * Corelate-Bukkit NativeAccessor
 * @author yuttyann44581
 */
public interface NativeAccessor {

    @NotNull
    static NativeAccessor get() throws ReflectiveOperationException {
        if (CraftBukkit.isPaperRemapped()) {
            if (V_1_21_6.isSupported()) {
                return new PaperRemappedAccessor_v1_21_6();
            } else {
                return new PaperRemappedAccessorLegacy();
            }
        } else if (NetMinecraft.isLegacy()) {
            if (V_1_14.isSupported()) {
                return new SpigotAccessor_v1_14();
            } else {
                return new SpigotAccessor_v1_9();
            }
        } else if (V_1_21_6.isSupported()) {
            return new SpigotAccessor_v1_21_6();
        } else if (V_1_20_5.isSupported()) {
            return new SpigotAccessor_v1_20_5();
        } else {
            return new SpigotAccessor_v1_17();
        }
    }

    // net.minecraft.server - methods

    void sendAllPacket(@NotNull Object packet) throws ReflectiveOperationException;

    void sendAllPackets(@NotNull Object... packets) throws ReflectiveOperationException;

    void sendPacket(@NotNull Object serverPlayer, @NotNull Object packet) throws ReflectiveOperationException;

    void sendPackets(@NotNull Object serverPlayer, @NotNull Object... packets) throws ReflectiveOperationException;

    @NotNull
    Object getEntity(@NotNull Entity entity) throws ReflectiveOperationException;

    @NotNull
    Object getServerPlayer(@NotNull Player player) throws ReflectiveOperationException;

    @NotNull
    Object getServerLevel(@NotNull World world) throws ReflectiveOperationException;

    @NotNull
    Object getComponentFromJson(@NotNull String json) throws ReflectiveOperationException;

    @NotNull
    String getComponentToJson(@NotNull Object component) throws ReflectiveOperationException;

    @NotNull
    Integer getContainerId(@NotNull Object containerMenu) throws ReflectiveOperationException;

    @NotNull
    Integer getNextContainerId(@NotNull Object serverPlayer) throws ReflectiveOperationException;

    void setActiveContainer(@NotNull Object serverPlayer, @NotNull Object containerMenu) throws ReflectiveOperationException;

    @NotNull
    Object getActiveContainer(@NotNull Object serverPlayer) throws ReflectiveOperationException;

    @NotNull
    Object getDefaultContainer(@NotNull Object serverPlayer) throws ReflectiveOperationException;

    void initMenu(@NotNull Object serverPlayer, @NotNull Object containerMenu) throws ReflectiveOperationException;

    void handleInventoryCloseEvent(@NotNull Object serverPlayer) throws ReflectiveOperationException;

    @NotNull
    InventoryView getBukkitView(@NotNull Object containerMenu) throws ReflectiveOperationException;

    @NotNull
    List<Entity> getEntities(@NotNull CommandSender sender, @NotNull Vector position, @NotNull String selector) throws ReflectiveOperationException;


    // net.minecraft.server - newInstance

    @NotNull
    Object newVec3(double x, double y, double z) throws ReflectiveOperationException;

    @NotNull
    Object newAnvilMenu(@NotNull Object serverPlayer, @NotNull Object serverLevel, @NotNull Object title) throws ReflectiveOperationException;

    @NotNull
    Object newMagmaCube(@NotNull Object serverLevel) throws ReflectiveOperationException;

    @NotNull
    Object newCraftMagmaCube(@NotNull Object magmaCube) throws ReflectiveOperationException;


    // net.minecraft-packets

    @NotNull
    Object newClientboundSetEntityDataPacket(@NotNull Object entity, @NotNull Integer entityId) throws ReflectiveOperationException;

    @NotNull
    Object newClientboundAddEntityPacket(@NotNull Object entity, double x, double y, double z) throws ReflectiveOperationException;

    @NotNull
    Object newClientboundRemoveEntitiesPacket(int... entityIds) throws ReflectiveOperationException;

    @NonNull
    Object newClientboundOpenScreenPacket(@NotNull Object containerMenu, @NotNull Object title) throws ReflectiveOperationException;

    @NonNull
    Object newClientboundOpenScreenPacket(@NotNull Integer containerId, @NotNull Object menuType, @NotNull Object title) throws ReflectiveOperationException;

    @NonNull
    Object newClientboundContainerClosePacket(@NotNull Integer containerId) throws ReflectiveOperationException;
}