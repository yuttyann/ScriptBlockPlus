package com.github.yuttyann.scriptblockplus.script.option.time;

import com.github.yuttyann.scriptblockplus.file.config.SBConfig;
import com.github.yuttyann.scriptblockplus.file.json.PlayerTemp;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.script.ScriptType;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

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
                PlayerTemp temp = getSBPlayer().getPlayerTemp();
                temp.getInfo().getTimerTemp().remove(timer.get());
                temp.save();
            }
        }
        return false;
    }

    public static void removeAll(@NotNull String fullCoords, @NotNull ScriptType scriptType) {
        int oldCooldown = Objects.hash(true, fullCoords, scriptType);
        for (OfflinePlayer player : Utils.getAllPlayers()) {
            PlayerTemp temp = SBPlayer.fromPlayer(player).getPlayerTemp();
            Set<TimerTemp> set = temp.getInfo().getTimerTemp();
            if (set.size() > 0) {
                int cooldown = Objects.hash(false, player.getUniqueId(), fullCoords, scriptType);
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