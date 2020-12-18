package com.github.yuttyann.scriptblockplus.script.option.time;

import com.github.yuttyann.scriptblockplus.file.Json;
import com.github.yuttyann.scriptblockplus.file.config.SBConfig;
import com.github.yuttyann.scriptblockplus.file.json.PlayerTempJson;
import com.github.yuttyann.scriptblockplus.file.json.element.PlayerTemp;
import com.github.yuttyann.scriptblockplus.script.ScriptType;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * ScriptBlockPlus TimerOption オプションクラス
 * @author yuttyann44581
 */
public abstract class TimerOption extends BaseOption {

    @NotNull
    protected abstract Optional<TimerTemp> getTimerTemp();

    @NotNull
    protected final <T> Optional<T> get(Set<T> set, @NotNull TimerTemp timerTemp) {
        int hash = timerTemp.hashCode();
        return set.stream().filter(t -> t.hashCode() == hash).findFirst();
    }

    @NotNull
    protected UUID getFileUniqueId() {
        return getUniqueId();
    }

    protected boolean inCooldown() {
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
                Json<PlayerTemp> json = new PlayerTempJson(getFileUniqueId());
                json.load().getTimerTemp().remove(timer.get());
                json.saveFile();
            }
        }
        return false;
    }

    public static void removeAll(@NotNull Location location, @NotNull ScriptType scriptType) {
        for (String name : Json.getNameList(PlayerTempJson.class)) {
            UUID uuid = UUID.fromString(name);
            Json<PlayerTemp> json = new PlayerTempJson(uuid);
            Set<TimerTemp> set = json.load().getTimerTemp();
            if (set.size() > 0) {
                set.remove(new TimerTemp(location, scriptType));
                set.remove(new TimerTemp(uuid, location, scriptType));
                json.saveFile();
            }
        }
    }
}