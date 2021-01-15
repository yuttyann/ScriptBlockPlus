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
import java.util.UUID;
import java.util.function.Predicate;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.Registry;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.WrappedDataWatcherObject;
import com.github.yuttyann.scriptblockplus.enums.TeamColor;
import com.github.yuttyann.scriptblockplus.enums.reflection.PackageType;
import com.github.yuttyann.scriptblockplus.hook.plugin.ProtocolLib;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.utils.StreamUtils;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

/**
 * ScriptBlockPlus GlowEntityPacket クラス
 * @author yuttyann44581
 */
public class GlowEntityPacket {

    static {
        // エンティティのクリックを検知
        StreamUtils.ifAction(ProtocolLib.INSTANCE.has(), () -> new EntityActionListener().register());
    }

    private static final int TYPE_ID = PackageType.HAS_NMS ? PackageType.getMagmaCubeId() : -1;
    private static final int SIZE_ID = PackageType.HAS_NMS ? PackageType.getSlimeSizeId() : -1;

    private final Multimap<UUID, GlowEntity> GLOW_ENTITIES = HashMultimap.create();

    @NotNull
    public Multimap<UUID, GlowEntity> getEntities() {
        return GLOW_ENTITIES;
    }

    @NotNull
    public TeamColor getTeamColor(@NotNull Block block) {
        return block.getType() == Material.AIR ? TeamColor.BLUE : TeamColor.GREEN;
    }

    @NotNull
    public GlowEntity spawnGlowEntity(@NotNull SBPlayer sbPlayer, @NotNull Location location, @NotNull TeamColor teamColor) {
        // エンティティのID
        int id = EntityCount.next();
        var uuid = UUID.randomUUID();

        // チームを取得、エンティティの登録
        var team = teamColor.getTeam();
        team.addEntry(uuid.toString());

        // パケットを送信
        var vector = new Vector(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        try {
            var protocolManager = ProtocolLibrary.getProtocolManager();
            protocolManager.sendServerPacket(sbPlayer.getPlayer(), createEntity(id, uuid, vector));
            protocolManager.sendServerPacket(sbPlayer.getPlayer(), createMetadata(id));
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        // エンティティのデータをまとめたクラスを返す
        var glowEntity = new GlowEntity(id, uuid, team, vector, sbPlayer);
        GLOW_ENTITIES.get(sbPlayer.getUniqueId()).add(glowEntity);
        return glowEntity;
    }

    public void destroyGlowEntity(@NotNull SBPlayer sbPlayer, @NotNull Location location) {
        var filter = (Predicate<GlowEntity>) g -> g.equals(location.getX(), location.getY(), location.getZ());
        StreamUtils.filterFirst(GLOW_ENTITIES.get(sbPlayer.getUniqueId()), filter).ifPresent(this::destroyGlowEntity);
    }

    public void destroyGlowEntity(@NotNull GlowEntity glowEntity) {
        var uuid = glowEntity.getSender().getUniqueId();
        if (!GLOW_ENTITIES.get(uuid).contains(glowEntity)) {
            return;
        }
        try {
            var player = glowEntity.getSender().getPlayer();
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, createDestroy(glowEntity.getId()));
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } finally {
            GLOW_ENTITIES.remove(uuid, glowEntity);
            glowEntity.getTeam().removeEntry(glowEntity.getUniqueId().toString());
        }
    }

    public void broadcastDestroy(@NotNull GlowEntity glowEntity) {
        var uuid = glowEntity.getSender().getUniqueId();
        if (!GLOW_ENTITIES.get(uuid).contains(glowEntity)) {
            return;
        }
        try {
            ProtocolLibrary.getProtocolManager().broadcastServerPacket(createDestroy(glowEntity.getId()));
        } finally {
            GLOW_ENTITIES.remove(uuid, glowEntity);
            glowEntity.getTeam().removeEntry(glowEntity.getUniqueId().toString());
        }
    }

    public void destroyAll(@NotNull SBPlayer sbPlayer) {
        var glowEntities = GLOW_ENTITIES.get(sbPlayer.getUniqueId());
        if (!glowEntities.isEmpty()) {
            StreamUtils.forEach(glowEntities.toArray(GlowEntity[]::new), this::destroyGlowEntity);
            GLOW_ENTITIES.removeAll(sbPlayer.getUniqueId());
        }
    }

    public void removeAll() {
        var glowEntities = GLOW_ENTITIES.values();
        if (!glowEntities.isEmpty()) {
            StreamUtils.forEach(glowEntities.toArray(GlowEntity[]::new), this::broadcastDestroy);
            GLOW_ENTITIES.clear();
        }
    }

    public boolean has(@NotNull SBPlayer sbPlayer, @NotNull Location location) {
        if (GLOW_ENTITIES.isEmpty()) {
            return false;
        }
        var filter = (Predicate<GlowEntity>) g -> g.equals(location.getX(), location.getY(), location.getZ());
        return StreamUtils.anyMatch(GLOW_ENTITIES.get(sbPlayer.getUniqueId()), filter);
    }

    @NotNull
    private PacketContainer createEntity(final int id, @NotNull UUID uuid, @NotNull Vector vector) {
        var packetType = PacketType.Play.Server.SPAWN_ENTITY_LIVING;
        var spawnEntity = ProtocolLibrary.getProtocolManager().createPacket(packetType);
        double x = vector.getX() + 0.5D, y = vector.getY(), z = vector.getZ() + 0.5D;
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
    private PacketContainer createDestroy(final int id) {
        var destroy = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_DESTROY);
        destroy.getIntegerArrays().write(0, new int[] { id });
        return destroy;
    }

    @NotNull
    private WrappedDataWatcherObject createObject(final int index, @NotNull Class<?> clazz) {
        return new WrappedDataWatcherObject(index, Registry.get(clazz));
    }
}