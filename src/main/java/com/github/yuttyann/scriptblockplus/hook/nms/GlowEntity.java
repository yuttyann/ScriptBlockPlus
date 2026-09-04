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
import static com.github.yuttyann.scriptblockplus.utils.version.McVersion.V_26_2;
import static java.lang.reflect.Modifier.*;

import java.util.UUID;

import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.MagmaCube;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.enums.TeamColor;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.utils.ArrayUtils;
import com.github.yuttyann.scriptblockplus.utils.server.CraftBukkit;
import com.github.yuttyann.scriptblockplus.utils.server.NetMinecraft;
import com.github.yuttyann.scriptblockplus.utils.unmodifiable.UnmodifiableBlockCoords;

/**
 * ScriptBlockPlus GlowEntity クラス
 * @author yuttyann44581
 */
public final class GlowEntity {

    private static final Object SCALE_ATTRIBUTE;

    static {
        method(WORLD_ENTITY.getClass("Entity"))
                .modifiers(PUBLIC, -STATIC, -FINAL)
                .returnType(void.class)
                .parameterTypes(double.class, double.class, double.class, float.class, float.class)
                .findFirst("Entity.moveTo");
        if (V_26_2.isSupported()) {
            method(CraftBukkit.ENTITY.getClass("CraftAbstractCubeMob"))
                .modifiers(PUBLIC, -STATIC)
                .name("setSize")
                .returnType(void.class)
                .parameterTypes(int.class)
                .findFirst("GlowEntity.setCubeSize");
            method(WORLD_ENTITY.getClass("AgeableMob"))
                .modifiers(PUBLIC, -STATIC)
                .name("setBaby")
                .returnType(void.class)
                .parameterTypes(boolean.class)
                .findFirst("GlowEntity.setBaby");
            method(WORLD_ENTITY.getClass("LivingEntity"))
                .modifiers(PUBLIC, -STATIC)
                .name("getAttribute")
                .returnType(WORLD_ENTITY_AI_ATTRIBUTES.getClass("AttributeInstance"))
                .parameterTypes(CORE.getClass("Holder"))
                .findFirst("GlowEntity.getAttribute");
            method(WORLD_ENTITY_AI_ATTRIBUTES.getClass("AttributeInstance"))
                .modifiers(PUBLIC, -STATIC)
                .name("setBaseValue")
                .returnType(void.class)
                .parameterTypes(double.class)
                .findFirst("GlowEntity.setAttributeBaseValue");
            try {
                SCALE_ATTRIBUTE = field(WORLD_ENTITY_AI_ATTRIBUTES.getClass("Attributes"))
                    .modifiers(PUBLIC, STATIC, FINAL).name("SCALE").fieldType(CORE.getClass("Holder")).findFirst().get(null);
            } catch (ReflectiveOperationException e) {
                throw new ExceptionInInitializerError(e);
            }
        } else {
            SCALE_ATTRIBUTE = null;
        }
    }

    private final int id;

    private final UUID uuid;
    private final Object nmsEntity;
    private final Object scaleAttribute;
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
     * @param scaleAttribute - 初期表示時に同期するSCALE属性（未使用ならnull）
     * @param sbPlayer - 送信者
     * @param teamColor - 発光色
     * @param blockCoords - 座標
     * @param flagSize - フラグの初期容量
     */
    private GlowEntity(final int id, @NotNull UUID uuid, @NotNull Object nmsEntity, @Nullable Object scaleAttribute, @NotNull SBPlayer sbPlayer, @NotNull TeamColor teamColor, @NotNull BlockCoords blockCoords, final int flagSize) {
        this.id = id;
        this.uuid = uuid;
        this.nmsEntity = nmsEntity;
        this.scaleAttribute = scaleAttribute;
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
        var nmsCube = newCubeEntity(getServerLevel(blockCoords.getWorld()));
        var cube = (LivingEntity) newCraftCubeEntity(nmsCube);
        Object scaleAttribute = null;
        if (V_26_2.isSupported()) {
            // 発光表示の大きさを約1ブロックに揃えます。
            methods().invoke("GlowEntity.setCubeSize", cube, SulfurCubeGlowAppearance.SIZE);
            methods().invoke("GlowEntity.setBaby", nmsCube, true);
            scaleAttribute = methods().invoke("GlowEntity.getAttribute", nmsCube, SCALE_ATTRIBUTE);
            methods().invoke("GlowEntity.setAttributeBaseValue", scaleAttribute, SulfurCubeGlowAppearance.SCALE);
        } else {
            ((MagmaCube) cube).setSize(2);
        }
        cube.setGlowing(true);
        if (NetMinecraft.isLegacy()) {
            NetMinecraft.LEGACY_PATH.invokeMethod(nmsCube, "Entity", "setInvisible", true);
        } else {
            cube.setInvisible(true);
        }
        methods().invoke("Entity.moveTo", nmsCube, blockCoords.getX() + 0.5D, blockCoords.getY(), blockCoords.getZ() + 0.5D, 0.0F, 0.0F);
        var glowEntity = new GlowEntity(cube.getEntityId(), cube.getUniqueId(), nmsCube, scaleAttribute, sbPlayer, teamColor, blockCoords, flagSize);
        teamColor.getTeam().addEntry(glowEntity.uuid.toString());
        return glowEntity;
    }

    @Nullable
    Object getScaleAttribute() {
        return scaleAttribute;
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
