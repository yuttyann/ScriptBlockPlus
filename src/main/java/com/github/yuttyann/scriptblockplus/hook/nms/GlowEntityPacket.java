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
package com.github.yuttyann.scriptblockplus.hook.nms;

import java.util.Collection;
import java.util.UUID;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.enums.TeamColor;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.utils.NMSHelper;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import com.google.common.collect.ArrayListMultimap;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.github.yuttyann.scriptblockplus.enums.reflection.PackageType.*;

/**
 * ScriptBlockPlus GlowEntityPacket クラス
 * @author yuttyann44581
 */
public final class GlowEntityPacket {

    private final ArrayListMultimap<UUID, GlowEntity> GLOW_ENTITIES = ArrayListMultimap.create();

    /**
     * 発光エンティティのマップを取得します。
     * @return {@link ArrayListMultimap}&lt;{@link UUID}, {@link GlowEntity}&gt; - 発光エンティティのマップ
     */
    @NotNull
    public ArrayListMultimap<UUID, GlowEntity> getEntities() {
        return GLOW_ENTITIES;
    }

    /**
     * 発光エンティティが存在するのかどうか。
     * @param sbPlayer - 送信者
     * @param block - ブロック
     * @return {@link boolean} - 存在する場合は{@code true}
     */
    public boolean has(@NotNull SBPlayer sbPlayer, @NotNull Block block) {
        return getGlowEntity(sbPlayer, block) != null;
    }

    /**
     * 発光エンティティが存在するのかどうか。
     * @param sbPlayer - 送信者
     * @param blockCoords - ブロックの座標
     * @return {@link boolean} - 存在する場合は{@code true}
     */
    public boolean has(@NotNull SBPlayer sbPlayer, @NotNull BlockCoords blockCoords) {
        return getGlowEntity(sbPlayer, blockCoords) != null;
    }

    /**
     * 発光エンティティを取得します。
     * @param sbPlayer - 送信者
     * @param block - ブロック
     * @return {@link GlowEntity} - 発光エンティティ
     */
    @Nullable
    public GlowEntity getGlowEntity(@NotNull SBPlayer sbPlayer, @NotNull Block block) {
        if (GLOW_ENTITIES.isEmpty() || !GLOW_ENTITIES.containsKey(sbPlayer.getUniqueId())) {
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

    /**
     * 発光エンティティを取得します。
     * @param sbPlayer - 送信者
     * @param blockCoords - ブロックの座標
     * @return {@link GlowEntity} - 発光エンティティ
     */
    @Nullable
    public GlowEntity getGlowEntity(@NotNull SBPlayer sbPlayer, @NotNull BlockCoords blockCoords) {
        if (GLOW_ENTITIES.isEmpty() || !GLOW_ENTITIES.containsKey(sbPlayer.getUniqueId())) {
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

    /**
     * 発光エンティティをスポーンさせます。
     * @param sbPlayer - 送信者
     * @param block - ブロック
     * @param teamColor - 発光色
     * @return {@link GlowEntity} - スポーンした発光エンティティ
     * @throws ReflectiveOperationException - リフレクション関係で例外が発生した場合にスローされます。
     */
    @Nullable
    public GlowEntity spawnGlowEntity(@NotNull SBPlayer sbPlayer, @NotNull Block block, @NotNull TeamColor teamColor) throws ReflectiveOperationException {
        return spawnGlowEntity(sbPlayer, BlockCoords.of(block), teamColor, 0);
    }

    /**
     * 発光エンティティをスポーンさせます。
     * @param sbPlayer - 送信者
     * @param block - ブロック
     * @param teamColor - 発光色
     * @param flagSize - フラグの初期容量
     * @return {@link GlowEntity} - スポーンした発光エンティティ
     * @throws ReflectiveOperationException - リフレクション関係で例外が発生した場合にスローされます。
     */
    @Nullable
    public GlowEntity spawnGlowEntity(@NotNull SBPlayer sbPlayer, @NotNull Block block, @NotNull TeamColor teamColor, final int flagSize) throws ReflectiveOperationException {
        return spawnGlowEntity(sbPlayer, BlockCoords.of(block), teamColor, flagSize);
    }

    /**
     * 発光エンティティをスポーンさせます。
     * @param sbPlayer - 送信者
     * @param blockCoords - ブロックの座標
     * @param teamColor - 発光色
     * @return {@link GlowEntity} - スポーンした発光エンティティ
     * @throws ReflectiveOperationException - リフレクション関係で例外が発生した場合にスローされます。
     */
    @Nullable
    public GlowEntity spawnGlowEntity(@NotNull SBPlayer sbPlayer, @NotNull BlockCoords blockCoords, @NotNull TeamColor teamColor) throws ReflectiveOperationException {
        return spawnGlowEntity(sbPlayer, blockCoords, teamColor, 0);
    }

    /**
     * 発光エンティティをスポーンさせます。
     * @param sbPlayer - 送信者
     * @param blockCoords - ブロックの座標
     * @param teamColor - 発光色
     * @param flagSize - フラグの初期容量
     * @return {@link GlowEntity} - スポーンした発光エンティティ
     * @throws ReflectiveOperationException - リフレクション関係で例外が発生した場合にスローされます。
     */
    @Nullable
    public GlowEntity spawnGlowEntity(@NotNull SBPlayer sbPlayer, @NotNull BlockCoords blockCoords, @NotNull TeamColor teamColor, final int flagSize) throws ReflectiveOperationException {
        if (has(sbPlayer, blockCoords)) {
            return null;
        }
        var nmsEntity = createMagmaCube(blockCoords);
        var glowEntity = GlowEntity.create(nmsEntity, sbPlayer, teamColor, blockCoords, flagSize);
        var spawnEntity = createSpawnEntity(nmsEntity);
        var entityRotation = createRotation(nmsEntity);
        var entityMetadata = createMetadata(glowEntity.getId(), nmsEntity);
        NMSHelper.sendPackets(sbPlayer.getPlayer(), spawnEntity, entityRotation, entityMetadata);
        GLOW_ENTITIES.put(sbPlayer.getUniqueId(), glowEntity);
        return glowEntity;
    }

    /**
     * 発光エンティティをデスポーンさせます。
     * @param sbPlayer - 送信者
     * @param block - ブロック
     * @return {@link boolean} - デスポーンに成功した場合は{@code true}
     * @throws ReflectiveOperationException - リフレクション関係で例外が発生した場合にスローされます。
     */
    public boolean destroyGlowEntity(@NotNull SBPlayer sbPlayer, @NotNull Block block) throws ReflectiveOperationException {
        var glowEntity = getGlowEntity(sbPlayer, block);
        if (glowEntity != null) {
            return destroyGlowEntity(glowEntity);
        }
        return false;
    }

    /**
     * 発光エンティティをデスポーンさせます。
     * @param sbPlayer - 送信者
     * @param blockCoords - ブロックの座標
     * @return {@link boolean} - デスポーンに成功した場合は{@code true}
     * @throws ReflectiveOperationException - リフレクション関係で例外が発生した場合にスローされます。
     */
    public boolean destroyGlowEntity(@NotNull SBPlayer sbPlayer, @NotNull BlockCoords blockCoords) throws ReflectiveOperationException {
        var glowEntity = getGlowEntity(sbPlayer, blockCoords);
        if (glowEntity != null) {
            return destroyGlowEntity(glowEntity);
        }
        return false;
    }

    /**
     * 発光エンティティをデスポーンさせます。
     * @param glowEntity - 発光エンティティ
     * @return {@link boolean} - デスポーンに成功した場合は{@code true}
     * @throws ReflectiveOperationException - リフレクション関係で例外が発生した場合にスローされます。
     */
    public boolean destroyGlowEntity(@NotNull GlowEntity glowEntity) throws ReflectiveOperationException {
        if (glowEntity.isDead()) {
            return false;
        }
        var sbPlayer = glowEntity.getSBPlayer();
        NMSHelper.sendPacket(sbPlayer.getPlayer(), createDestroy(new int[] { glowEntity.getId() }));
        glowEntity.setDead(true);
        GLOW_ENTITIES.remove(sbPlayer.getUniqueId(), glowEntity);
        return true;
    }

    /**
     * 発光エンティティをデスポーンさせます。
     * @param blockCoords - ブロックの座標
     * @return {@link boolean} - デスポーンに成功した場合は{@code true}
     * @throws ReflectiveOperationException - リフレクション関係で例外が発生した場合にスローされます。
     */
    public boolean broadcastDestroyGlowEntity(@NotNull BlockCoords blockCoords) throws ReflectiveOperationException {
        if (GLOW_ENTITIES.isEmpty()) {
            return false;
        }
        var removed = false;
        var id = new int[1];
        var iterator = GLOW_ENTITIES.values().iterator();
        while (iterator.hasNext()) {
            var glowEntity = iterator.next();
            if (glowEntity.compare(blockCoords)) {
                id[0] = glowEntity.getId();
                NMSHelper.sendPacket(createDestroy(id));
                glowEntity.setDead(true);
                iterator.remove();
                removed = true;
            }
        }
        return removed;
    }

    /**
     * 全ての発光エンティティをデスポーンさせます。
     * @param sbPlayer - 送信者
     * @throws ReflectiveOperationException - リフレクション関係で例外が発生した場合にスローされます。
     */
    public void destroyAll(@NotNull SBPlayer sbPlayer) throws ReflectiveOperationException {
        var glowEntities = GLOW_ENTITIES.get(sbPlayer.getUniqueId());
        if (!glowEntities.isEmpty()) {
            try {
                NMSHelper.sendPacket(sbPlayer.getPlayer(), createDestroy(createIds(glowEntities)));
            } finally {
                glowEntities.forEach(g -> g.setDead(true));
                GLOW_ENTITIES.removeAll(sbPlayer.getUniqueId());
            }
        }
    }

    /**
     * 全ての発光エンティティをデスポーンさせます。
     * @throws ReflectiveOperationException - リフレクション関係で例外が発生した場合にスローされます。
     */
    public void removeAll() throws ReflectiveOperationException {
        var glowEntities = GLOW_ENTITIES.values();
        if (!glowEntities.isEmpty()) {
            try {
                NMSHelper.sendPacket(createDestroy(createIds(glowEntities)));
            } finally {
                glowEntities.forEach(g -> g.setDead(true));
                GLOW_ENTITIES.clear();
            }
        }
    }

    @NotNull
    private Object createMagmaCube(@NotNull BlockCoords blockCoords) throws ReflectiveOperationException {
        double x = blockCoords.getX() + 0.5D, y = blockCoords.getY(), z = blockCoords.getZ() + 0.5D;
        var magmaCube = newEntityMagmaCube(blockCoords.getWorld());
        if (Utils.isCBXXXorLater("1.11")) {
            NMS.invokeMethod(true, magmaCube, "EntityMagmaCube", "setSize", 2, true);
        } else {
            NMS.invokeMethod(true, magmaCube, "EntityMagmaCube", "setSize", 2);
        }
        NMS.invokeMethod(magmaCube, "EntityMagmaCube", "setFlag", 6, true);
        NMS.invokeMethod(magmaCube, "EntityMagmaCube", "setInvisible", true);
        NMS.invokeMethod(magmaCube, "EntityMagmaCube", "setLocation", x, y, z, 0.0F, 0.0F);
        return magmaCube;
    }

    @NotNull
    private Object newEntityMagmaCube(@NotNull World world) throws ReflectiveOperationException {
        var handle = CB.invokeMethod(world, "CraftWorld", "getHandle");
        var nmsWorld = NMS.getClass("World");
        var entityTypes = NMS.getClass("EntityTypes");
        if (!Utils.isCBXXXorLater("1.13.2")) {
            return NMS.newInstance(false, "EntityMagmaCube", new Class<?>[] { nmsWorld }, handle);
        }
        var entityType = NMS.getFieldValue("EntityTypes", "MAGMA_CUBE", null);
        return NMS.newInstance(false, "EntityMagmaCube", new Class<?>[] { entityTypes, nmsWorld }, entityType, handle);
    }

    @NotNull
    private Object createSpawnEntity(@NotNull Object nmsEntity) throws ReflectiveOperationException {
        var parameterTypes = new Class<?>[] { NMS.getClass("EntityLiving") };
        return NMS.newInstance(false, "PacketPlayOutSpawnEntityLiving", parameterTypes, nmsEntity);
    }

    @NotNull
    private Object createMetadata(final int id, @NotNull Object nmsEntity) throws ReflectiveOperationException {
        var watcher = NMS.invokeMethod(nmsEntity, "EntityMagmaCube", "getDataWatcher");
        return NMS.newInstance("PacketPlayOutEntityMetadata", id, watcher, true);
    }

    @NotNull
    private Object createRotation(@NotNull Object nmsEntity) throws ReflectiveOperationException {
        var parameterTypes = new Class<?>[] { NMS.getClass("Entity"), byte.class };
        return NMS.newInstance(false, "PacketPlayOutEntityHeadRotation", parameterTypes, nmsEntity, (byte) 0);
    }

    @NotNull
    private Object createDestroy(final int[] ids) throws ReflectiveOperationException {
        return NMS.newInstance("PacketPlayOutEntityDestroy", ids);
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
}