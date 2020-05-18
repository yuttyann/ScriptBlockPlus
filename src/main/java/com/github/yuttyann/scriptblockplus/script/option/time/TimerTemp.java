package com.github.yuttyann.scriptblockplus.script.option.time;

import com.github.yuttyann.scriptblockplus.script.ScriptType;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

/**
 * ScriptBlockPlus TimerTemp クラス
 * @author yuttyann44581
 */
public class TimerTemp {

    private final long[] params;

    private final UUID uuid;
    private final String fullCoords;
    private final ScriptType scriptType;

    public TimerTemp(final long[] params, @NotNull String fullCoords, @NotNull ScriptType scriptType) {
        this.params = params;
        this.uuid = null;
        this.fullCoords = fullCoords;
        this.scriptType = scriptType;
    }

    public TimerTemp(final long[] params, @NotNull UUID uuid, @NotNull String fullCoords, @NotNull ScriptType scriptType) {
        this.params = params;
        this.uuid = uuid;
        this.fullCoords = fullCoords;
        this.scriptType = scriptType;
    }

    public int getSecond() {
        if (params[2] > System.currentTimeMillis()) {
            return Math.toIntExact((params[2] - System.currentTimeMillis()) / 1000L);
        }
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof TimerTemp)) {
            return false;
        }
        TimerTemp temp = (TimerTemp) o;
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