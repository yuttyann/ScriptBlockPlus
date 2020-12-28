package com.github.yuttyann.scriptblockplus.hook.plugin;

import java.util.Objects;
import java.util.UUID;

import com.github.yuttyann.scriptblockplus.player.SBPlayer;

import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    public int getX() {
        return vector.getBlockX();
    }

    public int getY() {
        return vector.getBlockX();
    }

    public int getZ() {
        return vector.getBlockX();
    }

    public boolean equals(int x, int y, int z) {
        return vector.getBlockX() == x && vector.getBlockY() == y && vector.getBlockZ() == z;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof GlowEntity)) {
            return false;
        }
        GlowEntity glowEntity = (GlowEntity) obj;
        return glowEntity.id == id && glowEntity.uuid.equals(uuid) && glowEntity.vector.equals(vector) && glowEntity.sbPlayer.equals(sbPlayer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, uuid, sbPlayer.getUniqueId(), vector);
    }
}