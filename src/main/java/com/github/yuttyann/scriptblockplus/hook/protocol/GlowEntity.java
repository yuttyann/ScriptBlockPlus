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

import com.github.yuttyann.scriptblockplus.player.SBPlayer;

import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static org.bukkit.util.NumberConversions.floor;

/**
 * ScriptBlockPlus GlowEntity クラス
 * @author yuttyann44581
 */
public final class GlowEntity {
        
    private final int id;
    private final UUID uuid;
    private final Team team;
    private final Vector vector;
    private final SBPlayer sbPlayer;

    GlowEntity(int id, @NotNull UUID uuid, @NotNull Team team, @NotNull Vector vector, @NotNull SBPlayer sbPlayer) {
        this.id = id;
        this.uuid = uuid;
        this.team = team;
        this.vector = vector;
        this.sbPlayer = sbPlayer;
    }

    public int getId() {
        return id;
    }

    public int getX() {
        return (int) vector.getX();
    }

    public int getY() {
        return (int) vector.getY();
    }

    public int getZ() {
        return (int) vector.getZ();
    }

    @NotNull
    public UUID getUniqueId() {
        return uuid;
    }

    @NotNull
    public Team getTeam() {
        return team;
    }
    
    @NotNull
    public SBPlayer getSender() {
        return sbPlayer;
    }

    public boolean equals(final double x, final double y, final double z) {
        return getX() == floor(x) && getY() == floor(y) && getZ() == floor(z);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof GlowEntity)) {
            return false;
        }
        var glow = (GlowEntity) obj;
        return glow.id == id && glow.uuid.equals(uuid) && glow.vector.equals(vector) && glow.sbPlayer.equals(sbPlayer);
    }
    
    @Override
    public int hashCode() {
        int hash = 1;
        int prime = 31;
        hash = prime * hash + id;
        hash = prime * hash + uuid.hashCode();
        hash = prime * hash + vector.hashCode();
        hash = prime * hash + sbPlayer.getUniqueId().hashCode();
        return hash;
    }
}