package com.github.yuttyann.scriptblockplus.script.option.time;

import com.github.yuttyann.scriptblockplus.file.config.SBConfig;
import com.github.yuttyann.scriptblockplus.file.json.Json;
import com.github.yuttyann.scriptblockplus.file.json.PlayerTemp;
import com.github.yuttyann.scriptblockplus.script.ScriptType;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.utils.StreamUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * ScriptBlockPlus TimerOption オプションクラス
 * @author yuttyann44581
 */
public abstract class TimerOption extends BaseOption {

    public TimerOption(@NotNull String name, @NotNull String syntax) {
        super(name, syntax);
    }

    @NotNull
    protected abstract Optional<TimerTemp> getTimerTemp();

    @NotNull
    protected final <T> Optional<T> get(Set<T> set, int hash) {
        return Optional.ofNullable(StreamUtils.fOrElse(set, t -> t.hashCode() == hash, null));
    }

    @NotNull
    protected UUID getFileUniqueId() {
        return getUniqueId();
    }

    protected boolean inCooldown() throws Exception {
        Optional<TimerTemp> timer = getTimerTemp();
        if (timer.isPresent()) {
            int time = timer.get().getSecond();
            if (time > 0) {
                short hour = (short) (time / 3600);
                byte minute = (byte) (time % 3600 / 60);
                byte second = (byte) (time % 3600 % 60);
                SBConfig.ACTIVE_COOLDOWN.replace(hour, minute, second).send(getSBPlayer());
                return true;
            } else {
                PlayerTemp temp = new PlayerTemp(getFileUniqueId());
                temp.getInfo().getTimerTemp().remove(timer.get());
                temp.save();
            }
        }
        return false;
    }

    public static void removeAll(@NotNull String fullCoords, @NotNull ScriptType scriptType) {
        int oldCooldown = Objects.hash(true, fullCoords, scriptType);
        for (UUID uuid : Json.getUniqueIdList(PlayerTemp.class)) {
            PlayerTemp temp = new PlayerTemp(uuid);
            Set<TimerTemp> set = temp.getInfo().getTimerTemp();
            if (set.size() > 0) {
                int cooldown = Objects.hash(false, uuid, fullCoords, scriptType);
                set.removeIf(t -> t.hashCode() == cooldown);
                set.removeIf(t -> t.hashCode() == oldCooldown);
                try {
                    temp.save();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}