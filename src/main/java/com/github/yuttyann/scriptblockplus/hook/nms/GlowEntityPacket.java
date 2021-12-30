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
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.enums.TeamColor;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.utils.NMSHelper;
import com.github.yuttyann.scriptblockplus.utils.collection.IntHashMap;
import com.github.yuttyann.scriptblockplus.utils.collection.IntMap;

import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * ScriptBlockPlus GlowEntityPacket クラス
 * @author yuttyann44581
 */
public final class GlowEntityPacket {

    private static final Function<UUID, IntMap<GlowEntity>> CREATE_MAP = m -> IntHashMap.create();

    private final Map<UUID, IntMap<GlowEntity>> GLOW_ENTITIES = new HashMap<>();

    /**
     * 発光エンティティのマップを取得します。
     * @return {@link Map}&lt;{@link UUID}, {@link IntMap}&lt;{@link GlowEntity}&gt;&gt; - 発光エンティティのマップ
     */
    @NotNull
    public Map<UUID, IntMap<GlowEntity>> getEntities() {
        return GLOW_ENTITIES;
    }

    /**
     * 発光エンティティが存在するのかどうか。
     * @param sbPlayer - 送信者
     * @param block - ブロック
     * @return {@code boolean} - 存在する場合は{@code true}
     */
    public boolean has(@NotNull SBPlayer sbPlayer, @NotNull Block block) {
        return getGlowEntity(sbPlayer, block) != null;
    }

    /**
     * 発光エンティティが存在するのかどうか。
     * @param sbPlayer - 送信者
     * @param blockCoords - ブロックの座標
     * @return {@code boolean} - 存在する場合は{@code true}
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
        return getGlowEntity(sbPlayer, BlockCoords.of(block));
    }

    /**
     * 発光エンティティを取得します。
     * @param sbPlayer - 送信者
     * @param blockCoords - ブロックの座標
     * @return {@link GlowEntity} - 発光エンティティ
     */
    @Nullable
    public GlowEntity getGlowEntity(@NotNull SBPlayer sbPlayer, @NotNull BlockCoords blockCoords) {
        var glowEntities = GLOW_ENTITIES.get(sbPlayer.getUniqueId());
        return glowEntities == null ? null : glowEntities.get(blockCoords.hashCode());
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
    public GlowEntity spawn(@NotNull SBPlayer sbPlayer, @NotNull Block block, @NotNull TeamColor teamColor) throws ReflectiveOperationException {
        return spawn(sbPlayer, BlockCoords.of(block), teamColor, 0);
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
    public GlowEntity spawn(@NotNull SBPlayer sbPlayer, @NotNull Block block, @NotNull TeamColor teamColor, final int flagSize) throws ReflectiveOperationException {
        return spawn(sbPlayer, BlockCoords.of(block), teamColor, flagSize);
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
    public GlowEntity spawn(@NotNull SBPlayer sbPlayer, @NotNull BlockCoords blockCoords, @NotNull TeamColor teamColor) throws ReflectiveOperationException {
        return spawn(sbPlayer, blockCoords, teamColor, 0);
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
    public GlowEntity spawn(@NotNull SBPlayer sbPlayer, @NotNull BlockCoords blockCoords, @NotNull TeamColor teamColor, final int flagSize) throws ReflectiveOperationException {
        if (has(sbPlayer, blockCoords)) {
            return null;
        }
        var glowEntity = GlowEntity.create(sbPlayer, teamColor, blockCoords, flagSize);
        NMSHelper.sendPackets(sbPlayer.toPlayer(), NMSHelper.createSpawnEntity(glowEntity), NMSHelper.createMetadata(glowEntity));
        GLOW_ENTITIES.computeIfAbsent(sbPlayer.getUniqueId(), CREATE_MAP).put(blockCoords.hashCode(), glowEntity);
        return glowEntity;
    }

    /**
     * 発光エンティティをデスポーンさせます。
     * @param sbPlayer - 送信者
     * @param block - ブロック
     * @return {@code boolean} - デスポーンに成功した場合は{@code true}
     * @throws ReflectiveOperationException - リフレクション関係で例外が発生した場合にスローされます。
     */
    public boolean destroy(@NotNull SBPlayer sbPlayer, @NotNull Block block) throws ReflectiveOperationException {
        return destroy(sbPlayer, BlockCoords.of(block));
    }

    /**
     * 発光エンティティをデスポーンさせます。
     * @param sbPlayer - 送信者
     * @param blockCoords - ブロックの座標
     * @return {@code boolean} - デスポーンに成功した場合は{@code true}
     * @throws ReflectiveOperationException - リフレクション関係で例外が発生した場合にスローされます。
     */
    public boolean destroy(@NotNull SBPlayer sbPlayer, @NotNull BlockCoords blockCoords) throws ReflectiveOperationException {
        var glowEntity = getGlowEntity(sbPlayer, blockCoords);
        return glowEntity == null ? false : destroy(glowEntity);
    }

    /**
     * 発光エンティティをデスポーンさせます。
     * @param glowEntity - 発光エンティティ
     * @return {@code boolean} - デスポーンに成功した場合は{@code true}
     * @throws ReflectiveOperationException - リフレクション関係で例外が発生した場合にスローされます。
     */
    public boolean destroy(@NotNull GlowEntity glowEntity) throws ReflectiveOperationException {
        if (glowEntity.isDead()) {
            return false;
        }
        var sbPlayer = glowEntity.getSBPlayer();
        NMSHelper.sendPackets(sbPlayer.toPlayer(), NMSHelper.createDestroy(new int[] { glowEntity.getId() }));
        removeMap(sbPlayer.getUniqueId(), glowEntity.getBlockCoords());
        glowEntity.setDead(true);
        return true;
    }

    /**
     * 発光エンティティをデスポーンさせます。
     * @param blockCoords - ブロックの座標
     * @return {@code boolean} - デスポーンに成功した場合は{@code true}
     * @throws ReflectiveOperationException - リフレクション関係で例外が発生した場合にスローされます。
     */
    public boolean broadcastDestroy(@NotNull BlockCoords blockCoords) throws ReflectiveOperationException {
        if (GLOW_ENTITIES.isEmpty()) {
            return false;
        }
        var id = new int[1];
        var hash = blockCoords.hashCode();
        var removed = false;
        for (var intMap : GLOW_ENTITIES.values()) {
            var glowEntity = intMap.remove(hash);
            if (glowEntity != null) {
                id[0] = glowEntity.getId();
                NMSHelper.sendPackets(NMSHelper.createDestroy(id));
                glowEntity.setDead(true);
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
        if (glowEntities != null) {
            try {
                var entities = glowEntities.values();
                entities.forEach(g -> g.setDead(true));
                NMSHelper.sendPackets(sbPlayer.toPlayer(), NMSHelper.createDestroy(createIds(entities)));
            } finally {
                GLOW_ENTITIES.remove(sbPlayer.getUniqueId());
            }
        }
    }

    /**
     * 全ての発光エンティティをデスポーンさせます。
     * @throws ReflectiveOperationException - リフレクション関係で例外が発生した場合にスローされます。
     */
    public void removeAll() throws ReflectiveOperationException {
        if (!GLOW_ENTITIES.isEmpty()) {
            try {
                for (var entry : GLOW_ENTITIES.entrySet()) {
                    var sbPlayer = ScriptBlock.getSBPlayer(entry.getKey());
                    var entities = entry.getValue().values();
                    entities.forEach(g -> g.setDead(true));
                    if (sbPlayer.isOnline()) {
                        NMSHelper.sendPackets(sbPlayer.toPlayer(), NMSHelper.createDestroy(createIds(entities)));
                    }
                }
            } finally {
                GLOW_ENTITIES.clear();
            }
        }
    }

    private void removeMap(@NotNull UUID uuid, @NotNull BlockCoords blockCoords) {
        var glowEntities = GLOW_ENTITIES.get(uuid);
        if (glowEntities != null) {
            glowEntities.remove(blockCoords.hashCode());
        }
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