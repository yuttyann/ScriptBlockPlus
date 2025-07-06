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

import static com.github.yuttyann.scriptblockplus.utils.reflect.Reflection.*;
import static com.github.yuttyann.scriptblockplus.utils.server.NetMinecraft.*;
import static com.github.yuttyann.scriptblockplus.utils.server.minecraft.Minecraft.*;
import static java.lang.reflect.Modifier.*;

import java.util.UUID;

import org.bukkit.block.Block;
import org.bukkit.entity.MagmaCube;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.enums.TeamColor;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.utils.ArrayUtils;
import com.github.yuttyann.scriptblockplus.utils.server.NetMinecraft;
import com.github.yuttyann.scriptblockplus.utils.unmodifiable.UnmodifiableBlockCoords;

/**
 * ScriptBlockPlus GlowEntity クラス
 * @author yuttyann44581
 */
public final class GlowEntity {

    static {
        method(WORLD_ENTITY.getClass("Entity"))
                .modifiers(PUBLIC, -STATIC, -FINAL)
                .returnType(void.class)
                .parameterTypes(double.class, double.class, double.class, float.class, float.class)
                .findFirst("Entity.moveTo");
    }

    private final int id;
    
    private final UUID uuid;
    private final Object nmsEntity;
    private final SBPlayer sbPlayer;
    private final TeamColor teamColor;
    private final BlockCoords blockCoords;

    private boolean dead;
    private boolean[] flag = ArrayUtils.EMPTY_BOOLEAN_ARRAY;

    /**
     * コンストラクタ
     * @param id - エンティティのID
     * @param uuid - エンティティの{@link UUID}
     * @param nmsEntity - {@code net.minecraft.world.entity.Entity}
     * @param sbPlayer - 送信者
     * @param teamColor - 発光色
     * @param blockCoords - 座標
     * @param flagSize - フラグの初期容量
     */
    private GlowEntity(final int id, @NotNull UUID uuid, @NotNull Object nmsEntity, @NotNull SBPlayer sbPlayer, @NotNull TeamColor teamColor, @NotNull BlockCoords blockCoords, final int flagSize) {
        this.id = id;
        this.uuid = uuid;
        this.nmsEntity = nmsEntity;
        this.sbPlayer = sbPlayer;
        this.teamColor = teamColor;
        this.blockCoords = new UnmodifiableBlockCoords(blockCoords);
        if (flagSize > 0) {
            this.flag = new boolean[flagSize];
        }
    }

    /**
     * {@link GlowEntity}を作成します。
     * @param sbPlayer - 送信者
     * @param teamColor - 発光色
     * @param blockCoords - 座標
     * @param flagSize - フラグの初期容量
     * @return {@link GlowEntity} - インスタンス
     * @throws ReflectiveOperationException - リフレクション関係で例外が発生した場合にスローされます。
     */
    @NotNull
    static GlowEntity create(@NotNull SBPlayer sbPlayer, @NotNull TeamColor teamColor, @NotNull BlockCoords blockCoords, final int flagSize) throws ReflectiveOperationException {
        var nmsMagmaCube = newMagmaCube(getServerLevel(blockCoords.getWorld()));
        var magmaCube = (MagmaCube) newCraftMagmaCube(nmsMagmaCube);
        magmaCube.setSize(2);
        magmaCube.setGlowing(true);
        if (NetMinecraft.isLegacy()) {
            NetMinecraft.LEGACY_PATH.invokeMethod(nmsMagmaCube, "Entity", "setInvisible", true);
        } else {
            magmaCube.setInvisible(true);
        }
        methods().invoke("Entity.moveTo", nmsMagmaCube, blockCoords.getX() + 0.5D, blockCoords.getY(), blockCoords.getZ() + 0.5D, 0.0F, 0.0F);
        var glowEntity = new GlowEntity(magmaCube.getEntityId(), magmaCube.getUniqueId(), nmsMagmaCube, sbPlayer, teamColor, blockCoords, flagSize);
        teamColor.getTeam().addEntry(glowEntity.uuid.toString());
        return glowEntity;
    }

    /**
     * エンティティIDを取得します。
     * @return {@code int} - エンティティID
     */
    public int getId() {
        return id;
    }

    /**
     * エンティティの{@link UUID}を取得します。
     * @return {@link UUID} - エンティティの{@link UUID}
     */
    @NotNull
    public UUID getUniqueId() {
        return uuid;
    }

    /**
     * {@code net.minecraft.world.entity.Entity}のエンティティを取得します。
     * @return {@code net.minecraft.world.entity.Entity} - エンティティ
     */
    public Object getNMSEntity() {
        return nmsEntity;
    }

    /**
     * 送信者を取得します。
     * @return {@link SBPlayer} - 送信者
     */
    @NotNull
    public SBPlayer getSBPlayer() {
        return sbPlayer;
    }

    /**
     * 発光色(チームの色)を取得します。
     * @return {@link TeamColor} - 発光色
     */
    @NotNull
    public TeamColor getTeamColor() {
        return teamColor;
    }

    /**
     * エンティティの座標を取得します。
     * @return {@code int} - X座標
     */
    public BlockCoords getBlockCoords() {
        return blockCoords;
    }

    /**
     * フラグを取得します。
     * <p>
     * 条件等を設定したい場合に利用してください。
     * @return {@code boolean} - フラグ
     */
    public boolean[] getFlag() {
        return flag;
    }

    /**
     * エンティティが消滅しているのかどうかを設定します。
     * @param dead - 消滅しているのかどうか。
     */
    void setDead(boolean dead) {
        this.dead = dead;
        if (dead) {
            teamColor.getTeam().removeEntry(uuid.toString());
        } else {
            teamColor.getTeam().addEntry(uuid.toString());
        }
    }

    /**
     * エンティティが消滅しているのかどうか。
     * @return {@code boolean} - 消滅している場合は{@code true}
     */
    public boolean isDead() {
        return dead;
    }

    /**
     * エンティティの座標を比較します。
     * @param block - ブロック
     * @return {@code boolean} - 一致する場合は{@code true}
     */
    public boolean compare(@NotNull Block block) {
        return blockCoords.getX() == block.getX() && blockCoords.getY() == block.getY() && blockCoords.getZ() == block.getZ();
    }

    /**
     * エンティティの座標を比較します。
     * @param blockCoords - ブロックの座標
     * @return {@code boolean} - 一致する場合は{@code true}
     */
    public boolean compare(@NotNull BlockCoords blockCoords) {
        return blockCoords.compare(blockCoords.getX(), blockCoords.getY(), blockCoords.getZ());
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == this) {
            return true;
        }
        return obj instanceof GlowEntity ? ((GlowEntity) obj).id == id : false;
    }

    @Override
    public int hashCode() {
        return id;
    }
}