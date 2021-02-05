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

import java.util.UUID;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.enums.TeamColor;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * ScriptBlockPlus GlowEntity クラス
 * @author yuttyann44581
 */
public final class GlowEntity {
        
    private final int id;
    private final UUID uuid;
    private final Vector vector;
    private final SBPlayer sbPlayer;
    private final TeamColor teamColor;

    private boolean[] flags = ArrayUtils.EMPTY_BOOLEAN_ARRAY;

    private boolean dead;

    GlowEntity(int id, @NotNull UUID uuid, @NotNull Vector vector, @NotNull SBPlayer sbPlayer, @NotNull TeamColor teamColor, final int flagSize) {
        this.id = id;
        this.uuid = uuid;
        this.vector = vector;
        this.sbPlayer = sbPlayer;
        this.teamColor = teamColor;
        if (flagSize > 0) {
            this.flags = new boolean[flagSize];
        }
    }

    public int getId() {
        return id;
    }

    public int getX() {
        return vector.getBlockX();
    }

    public int getY() {
        return vector.getBlockY();
    }

    public int getZ() {
        return vector.getBlockZ();
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

    public boolean[] getFlags() {
        return flags;
    }

    void setDead(boolean dead) {
        this.dead = dead;
    }

    public boolean isDead() {
        return dead;
    }

    public boolean equals(@NotNull Block block) {
        return getX() == block.getX() && getY() == block.getY() && getZ() == block.getZ();
    }

    public boolean equals(@NotNull BlockCoords blockCoords) {
        return getX() == blockCoords.getX() && getY() == blockCoords.getY() && getZ() == blockCoords.getZ();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof GlowEntity)) {
            return false;
        }
        var glow = (GlowEntity) obj;
        return glow.id == id && glow.uuid.equals(uuid) && glow.vector.equals(vector) && glow.sbPlayer.equals(sbPlayer);
    }
    
    @Override
    public int hashCode() {
        int hash = 1;
        int prime = 16;
        hash = prime * hash + id;
        hash = prime * hash + uuid.hashCode();
        hash = prime * hash + vector.hashCode();
        hash = prime * hash + sbPlayer.getUniqueId().hashCode();
        return hash;
    }
}