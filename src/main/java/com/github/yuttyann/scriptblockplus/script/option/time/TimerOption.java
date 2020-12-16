package com.github.yuttyann.scriptblockplus.script.option.time;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.file.config.SBConfig;
import com.github.yuttyann.scriptblockplus.file.json.Json;
import com.github.yuttyann.scriptblockplus.file.json.PlayerTempJson;
import com.github.yuttyann.scriptblockplus.file.json.element.PlayerTemp;
import com.github.yuttyann.scriptblockplus.script.ScriptType;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.utils.StreamUtils;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

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
        String fullCoords = BlockCoords.getFullCoords(location);
        int oldCooldown = Objects.hash(true, fullCoords, scriptType);
        for (String id : Json.getIdList(PlayerTempJson.class)) {
            UUID uuid = UUID.fromString(id);
            Json<PlayerTemp> json = new PlayerTempJson(uuid);
            Set<TimerTemp> set = json.load().getTimerTemp();
            if (set.size() > 0) {
                int cooldown = Objects.hash(false, uuid, fullCoords, scriptType);
                set.removeIf(t -> t.hashCode() == cooldown);
                set.removeIf(t -> t.hashCode() == oldCooldown);
                json.saveFile();
            }
        }
    }
}