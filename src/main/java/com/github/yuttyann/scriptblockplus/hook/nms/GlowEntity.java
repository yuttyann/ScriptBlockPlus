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

import java.util.UUID;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.enums.TeamColor;
import com.github.yuttyann.scriptblockplus.enums.reflection.PackageType;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * ScriptBlockPlus GlowEntity クラス
 * @author yuttyann44581
 */
public final class GlowEntity {

    public static final GlowEntityPacket DEFAULT = new GlowEntityPacket();

    private final int id;
    private final int x, y, z;
    
    private final UUID uuid;
    private final SBPlayer sbPlayer;
    private final TeamColor teamColor;

    private boolean dead;
    private boolean[] flag = ArrayUtils.EMPTY_BOOLEAN_ARRAY;

    private GlowEntity(final int id, @NotNull UUID uuid, @NotNull SBPlayer sbPlayer, @NotNull TeamColor teamColor, @NotNull BlockCoords blockCoords, final int flagSize) {
        this.id = id;
        this.x = blockCoords.getX();
        this.y = blockCoords.getY();
        this.z = blockCoords.getZ();
        this.uuid = uuid;
        this.sbPlayer = sbPlayer;
        this.teamColor = teamColor;
        if (flagSize > 0) {
            this.flag = new boolean[flagSize];
        }
    }

    @NotNull
    static GlowEntity create(@NotNull Object nmsEntity, @NotNull SBPlayer sbPlayer, @NotNull TeamColor teamColor, @NotNull BlockCoords blockCoords, final int flagSize) throws ReflectiveOperationException {
        int id = (int) PackageType.NMS.invokeMethod(nmsEntity, "EntityMagmaCube", "getId");
        var uuid = (UUID) PackageType.NMS.getFieldValue(true, "Entity", "uniqueID", nmsEntity);
        var glowEntity = new GlowEntity(id, uuid, sbPlayer, teamColor, blockCoords, flagSize);
        teamColor.getTeam().addEntry(glowEntity.uuid.toString());
        return glowEntity;
    }

    public int getId() {
        return id;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    @NotNull
    public UUID getUniqueId() {
        return uuid;
    }
    
    @NotNull
    public SBPlayer getSBPlayer() {
        return sbPlayer;
    }

    @NotNull
    public TeamColor getTeamColor() {
        return teamColor;
    }

    public boolean removeEntry() {
        return teamColor.getTeam().removeEntry(uuid.toString());
    }

    public boolean[] getFlag() {
        return flag;
    }

    void setDead(boolean dead) {
        this.dead = dead;
    }

    public boolean isDead() {
        return dead;
    }

    public boolean compare(@NotNull Block block) {
        return getX() == block.getX() && getY() == block.getY() && getZ() == block.getZ();
    }

    public boolean compare(@NotNull BlockCoords blockCoords) {
        return blockCoords.compare(getX(), getY(), getZ());
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