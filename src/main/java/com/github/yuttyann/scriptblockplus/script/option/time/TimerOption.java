package com.github.yuttyann.scriptblockplus.script.option.time;

import com.github.yuttyann.scriptblockplus.file.Json;
import com.github.yuttyann.scriptblockplus.file.config.SBConfig;
import com.github.yuttyann.scriptblockplus.file.json.PlayerTempJson;
import com.github.yuttyann.scriptblockplus.script.ScriptKey;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.google.common.collect.Sets;

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
    protected final <T> Optional<T> get(@NotNull Set<T> set, @NotNull TimerTemp timerTemp) {
        int hash = timerTemp.hashCode();
        return set.stream().filter(t -> t.hashCode() == hash).findFirst();
    }

    @NotNull
    protected UUID getFileUniqueId() {
        return getUniqueId();
    }

    protected boolean inCooldown() {
        var timer = getTimerTemp();
        if (timer.isPresent()) {
            int time = timer.get().getSecond();
            if (time > 0) {
                short hour = (short) (time / 3600);
                byte minute = (byte) (time % 3600 / 60);
                byte second = (byte) (time % 3600 % 60);
                SBConfig.ACTIVE_COOLDOWN.replace(hour, minute, second).send(getSBPlayer());
                return true;
            } else {
                var tempJson = new PlayerTempJson(getFileUniqueId());
                tempJson.load().getTimerTemp().remove(timer.get());
                tempJson.saveFile();
            }
        }
        return false;
    }

    public static void removeAll(@NotNull Location location, @NotNull ScriptKey scriptKey) {
        removeAll(Sets.newHashSet(location), scriptKey);
    }

    public static void removeAll(@NotNull Set<Location> locations, @NotNull ScriptKey scriptKey) {
        for (var name : Json.getNames(PlayerTempJson.class)) {
            var uuid = UUID.fromString(name);
            var tempJson = new PlayerTempJson(uuid);
            if (!tempJson.exists()) {
                continue;
            }
            boolean modifiable = false;
            for (var location : locations) {
                if (!tempJson.has()) {
                    continue;
                }
                var timer = tempJson.load().getTimerTemp();
                if (timer.size() > 0) {
                    modifiable = true;
                    timer.remove(new TimerTemp(location, scriptKey));
                    timer.remove(new TimerTemp(uuid, location, scriptKey));
                }
            }
            if (modifiable) {
                tempJson.saveFile();
            }
        }
    }
}