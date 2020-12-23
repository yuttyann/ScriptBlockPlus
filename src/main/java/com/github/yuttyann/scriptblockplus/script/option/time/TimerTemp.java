package com.github.yuttyann.scriptblockplus.script.option.time;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.script.ScriptType;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

/**
 * ScriptBlockPlus TimerTemp クラス
 * @author yuttyann44581
 */
public class TimerTemp {

    private long[] params;

    private final UUID uuid;
    private final String fullCoords;
    private final ScriptType scriptType;

    public TimerTemp(@NotNull Location location, @NotNull ScriptType scriptType) {
        this(null, location, scriptType);
    }

    public TimerTemp(@Nullable UUID uuid, @NotNull Location location, @NotNull ScriptType scriptType) {
        this.uuid = uuid;
        this.fullCoords = BlockCoords.getFullCoords(location);
        this.scriptType = scriptType;
    }
    
    TimerTemp set(long[] params) {
        this.params = params;
        return this;
    }

    public int getSecond() {
        if (params != null && params[2] > System.currentTimeMillis()) {
            return Math.toIntExact((params[2] - System.currentTimeMillis()) / 1000L);
        }
        return 0;
    }

    @Nullable
    public UUID getUniqueId() {
        return uuid;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof TimerTemp)) {
            return false;
        }
        TimerTemp temp = (TimerTemp) obj;
        return Objects.equals(uuid, temp.uuid) && fullCoords.equals(temp.fullCoords) && scriptType.equals(temp.scriptType);
    }

    @Override
    public int hashCode() {
        int hash = 1;
        int prime = 31;
        boolean isOldCooldown = uuid == null;
        hash = prime * hash + Boolean.hashCode(isOldCooldown);
        if (!isOldCooldown) {
            hash = prime * hash + uuid.hashCode();
        }
        hash = prime * hash + fullCoords.hashCode();
        hash = prime * hash + scriptType.hashCode();
        return hash;
    }
}