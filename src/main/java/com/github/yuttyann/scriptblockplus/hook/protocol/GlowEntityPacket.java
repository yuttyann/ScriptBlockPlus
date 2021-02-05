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
package com.github.yuttyann.scriptblockplus.hook.protocol;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.UUID;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.Registry;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.WrappedDataWatcherObject;
import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.enums.TeamColor;
import com.github.yuttyann.scriptblockplus.enums.reflection.PackageType;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.utils.NMSHelper;
import com.google.common.collect.ArrayListMultimap;

import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * ScriptBlockPlus GlowEntityPacket クラス
 * @author yuttyann44581
 */
public final class GlowEntityPacket {

    private static final int TYPE_ID = PackageType.HAS_NMS ? NMSHelper.getMagmaCubeId() : -1;
    private static final int SIZE_ID = PackageType.HAS_NMS ? NMSHelper.getSlimeSizeId() : -1;

    private final ArrayListMultimap<UUID, GlowEntity> GLOW_ENTITIES = ArrayListMultimap.create();

    @NotNull
    public ArrayListMultimap<UUID, GlowEntity> getEntities() {
        return GLOW_ENTITIES;
    }

    public boolean has(@NotNull SBPlayer sbPlayer, @NotNull Block block) {
        return getGlowEntity(sbPlayer, block) != null;
    }

    public boolean has(@NotNull SBPlayer sbPlayer, @NotNull BlockCoords blockCoords) {
        return getGlowEntity(sbPlayer, blockCoords) != null;
    }

    @Nullable
    public GlowEntity getGlowEntity(@NotNull SBPlayer sbPlayer, @NotNull Block block) {
        if (GLOW_ENTITIES.isEmpty()) {
            return null;
        }
        var glowEntities = GLOW_ENTITIES.get(sbPlayer.getUniqueId());
        if (glowEntities.isEmpty()) {
            return null;
        }
        for (int i = 0, l = glowEntities.size(); i < l; i++) {
            var glowEntity = glowEntities.get(i);
            if (glowEntity.compare(block)) {
                return glowEntity;
            }
        }
        return null;
    }

    @Nullable
    public GlowEntity getGlowEntity(@NotNull SBPlayer sbPlayer, @NotNull BlockCoords blockCoords) {
        if (GLOW_ENTITIES.isEmpty()) {
            return null;
        }
        var glowEntities = GLOW_ENTITIES.get(sbPlayer.getUniqueId());
        if (glowEntities.isEmpty()) {
            return null;
        }
        for (int i = 0, l = glowEntities.size(); i < l; i++) {
            var glowEntity = glowEntities.get(i);
            if (glowEntity.compare(blockCoords)) {
                return glowEntity;
            }
        }
        return null;
    }

    @Nullable
    public GlowEntity spawnGlowEntity(@NotNull SBPlayer sbPlayer, @NotNull Block block, @NotNull TeamColor teamColor) throws InvocationTargetException {
        return spawnGlowEntity(sbPlayer, BlockCoords.of(block), teamColor, 0);
    }
    
    @Nullable
    public GlowEntity spawnGlowEntity(@NotNull SBPlayer sbPlayer, @NotNull Block block, @NotNull TeamColor teamColor, final int flagSize) throws InvocationTargetException {
        return spawnGlowEntity(sbPlayer, BlockCoords.of(block), teamColor, flagSize);
    }

    @Nullable
    public GlowEntity spawnGlowEntity(@NotNull SBPlayer sbPlayer, @NotNull BlockCoords blockCoords, @NotNull TeamColor teamColor) throws InvocationTargetException {
        return spawnGlowEntity(sbPlayer, blockCoords, teamColor, 0);
    }

    @Nullable
    public GlowEntity spawnGlowEntity(@NotNull SBPlayer sbPlayer, @NotNull BlockCoords blockCoords, @NotNull TeamColor teamColor, final int flagSize) throws InvocationTargetException {
        if (has(sbPlayer, blockCoords)) {
            return null;
        }
        var glowEntity = GlowEntity.create(sbPlayer, teamColor, blockCoords, flagSize);
        GLOW_ENTITIES.put(sbPlayer.getUniqueId(), glowEntity);
        sendPacket(sbPlayer, createEntity(glowEntity.getId(), glowEntity.getUniqueId(), blockCoords));
        sendPacket(sbPlayer, createMetadata(glowEntity.getId()));
        return glowEntity;
    }

    public boolean destroyGlowEntity(@NotNull SBPlayer sbPlayer, @NotNull Block block) throws InvocationTargetException {
        var glowEntity = getGlowEntity(sbPlayer, block);
        if (glowEntity != null) {
            return destroyGlowEntity(glowEntity);
        }
        return false;
    }

    public boolean destroyGlowEntity(@NotNull SBPlayer sbPlayer, @NotNull BlockCoords blockCoords) throws InvocationTargetException {
        var glowEntity = getGlowEntity(sbPlayer, blockCoords);
        if (glowEntity != null) {
            return destroyGlowEntity(glowEntity);
        }
        return false;
    }

    public boolean destroyGlowEntity(@NotNull GlowEntity glowEntity) throws InvocationTargetException {
        if (glowEntity.isDead()) {
            return false;
        }
        var uuid = glowEntity.getSBPlayer().getUniqueId();
        sendPacket(glowEntity.getSBPlayer(), createDestroy(new int[] { glowEntity.getId() }));
        glowEntity.setDead(true);
        glowEntity.removeEntry();
        GLOW_ENTITIES.remove(uuid, glowEntity);
        return true;
    }

    public boolean broadcastDestroyGlowEntity(@NotNull BlockCoords blockCoords) {
        if (GLOW_ENTITIES.isEmpty()) {
            return false;
        }
        var id = new int[1];
        var removed = false;
        var iterator = GLOW_ENTITIES.values().iterator();
        while (iterator.hasNext()) {
            var glowEntity = iterator.next();
            if (glowEntity.compare(blockCoords)) {
                id[0] = glowEntity.getId();
                sendPacket(createDestroy(id));
                glowEntity.setDead(true);
                glowEntity.removeEntry();
                iterator.remove();
                removed = true;
            }
        }
        return removed;
    }

    public void destroyAll(@NotNull SBPlayer sbPlayer) {
        var glowEntities = GLOW_ENTITIES.get(sbPlayer.getUniqueId());
        if (!glowEntities.isEmpty()) {
            try {
                sendPacket(sbPlayer, createDestroy(createIds(glowEntities)));
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } finally {
                glowEntities.forEach(g -> { g.setDead(true); g.removeEntry(); });
                GLOW_ENTITIES.removeAll(sbPlayer.getUniqueId());
            }
        }
    }

    public void removeAll() {
        var glowEntities = GLOW_ENTITIES.values();
        if (!glowEntities.isEmpty()) {
            try {
                sendPacket(createDestroy(createIds(glowEntities)));
            } finally {
                glowEntities.forEach(g -> { g.setDead(true); g.removeEntry(); });
                GLOW_ENTITIES.clear();
            }
        }
    }

    private void sendPacket(@NotNull PacketContainer packet) {
        ProtocolLibrary.getProtocolManager().broadcastServerPacket(packet);
    }

    private void sendPacket(@NotNull SBPlayer sbPlayer, @NotNull PacketContainer packet) throws InvocationTargetException {
        if (sbPlayer.isOnline()) {
            ProtocolLibrary.getProtocolManager().sendServerPacket(sbPlayer.getPlayer(), packet);
        }
    }

    @NotNull
    private PacketContainer createEntity(final int id, @NotNull UUID uuid, @NotNull BlockCoords blockCoords) {
        var packetType = PacketType.Play.Server.SPAWN_ENTITY_LIVING;
        var spawnEntity = ProtocolLibrary.getProtocolManager().createPacket(packetType);
        double x = blockCoords.getX() + 0.5D, y = blockCoords.getY(), z = blockCoords.getZ() + 0.5D;
        spawnEntity.getUUIDs().write(0, uuid);
        spawnEntity.getIntegers().write(0, id).write(1, TYPE_ID);
        spawnEntity.getDoubles().write(0, x).write(1, y).write(2, z);
        return spawnEntity;
    }

    @NotNull
    private PacketContainer createMetadata(final int id) {
        var packetType = PacketType.Play.Server.ENTITY_METADATA;
        var entityMetadata = ProtocolLibrary.getProtocolManager().createPacket(packetType);
        entityMetadata.getIntegers().write(0, id);
        var dataWatcher = new WrappedDataWatcher(entityMetadata.getWatchableCollectionModifier().read(0));
        dataWatcher.setObject(createObject(0, Byte.class), (byte) (0x20 + 0x40)); // Invisible & Glowing
        dataWatcher.setObject(createObject(SIZE_ID, Integer.class), 2);           // Size
        entityMetadata.getWatchableCollectionModifier().write(0, dataWatcher.getWatchableObjects());
        return entityMetadata;
    }

    @NotNull
    private int[] createIds(@NotNull Collection<GlowEntity> glowEntities) {
        var ids = new int[glowEntities.size()];
        var iterator = glowEntities.iterator();
        for (int i = 0; iterator.hasNext(); i++) {
            ids[i] = iterator.next().getId();
        }
        return ids;
    }

    @NotNull
    private PacketContainer createDestroy(final int[] ids) {
        var destroy = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_DESTROY);
        destroy.getIntegerArrays().write(0, ids);
        return destroy;
    }

    @NotNull
    private WrappedDataWatcherObject createObject(final int index, @NotNull Class<?> type) {
        return new WrappedDataWatcherObject(index, Registry.get(type));
    }
}