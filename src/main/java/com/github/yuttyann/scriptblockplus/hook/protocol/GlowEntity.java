package com.github.yuttyann.scriptblockplus.hook.protocol;

import java.util.UUID;

import com.github.yuttyann.scriptblockplus.player.SBPlayer;

import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * ScriptBlockPlus GlowEntity クラス
 * @author yuttyann44581
 */
public class GlowEntity {
        
    private final int id;
    private final UUID uuid;
    private final Team team;
    private final Vector vector;
    private final SBPlayer sbPlayer;

    public GlowEntity(int id, @NotNull UUID uuid, @NotNull Team team, @NotNull Vector vector, @NotNull SBPlayer sbPlayer) {
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
        return vector.getBlockX();
    }

    public int getY() {
        return vector.getBlockX();
    }

    public int getZ() {
        return vector.getBlockX();
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

    public boolean equals(final int x, final int y, final int z) {
        return vector.getBlockX() == x && vector.getBlockY() == y && vector.getBlockZ() == z;
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