package com.github.yuttyann.scriptblockplus.file.json.element;

import com.github.yuttyann.scriptblockplus.script.option.time.TimerTemp;
import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

/**
 * ScriptBlockPlus PlayerTemp クラス
 * @author yuttyann44581
 */
public class PlayerTemp {

    @SerializedName("timer")
    private final Set<TimerTemp> timer = new HashSet<>();

    @NotNull
    public Set<TimerTemp> getTimerTemp() {
        return timer;
    }

    @Override
    public int hashCode() {
        return timer.hashCode();
    }
}