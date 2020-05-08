package com.github.yuttyann.scriptblockplus.file.json;

import com.github.yuttyann.scriptblockplus.script.option.time.TimerTemp;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * ScriptBlockPlus PlayerTemp クラス
 * @author yuttyann44581
 */
public class PlayerTempInfo {

    @SerializedName("timer")
    @Expose
    private final Set<TimerTemp> timer = new HashSet<>();

    @SerializedName("uuid")
    @Expose
    private final UUID uuid;

    public PlayerTempInfo(UUID uuid) {
        this.uuid = uuid;
    }

    @NotNull
    public Set<TimerTemp> getTimerTemp() {
        return timer;
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }
}