package com.github.yuttyann.scriptblockplus.script.option.other;

import java.util.Collections;
import java.util.Locale;

import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.script.option.Option;
import com.github.yuttyann.scriptblockplus.script.option.OptionTag;
import com.github.yuttyann.scriptblockplus.utils.StreamUtils;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * ScriptBlockPlus PlaySound オプションクラス
 * @author yuttyann44581
 */
@OptionTag(name = "sound", syntax = "@sound:")
public class PlaySound extends BaseOption implements Runnable {

    private String name;
    private int volume, pitch;
    private boolean sendAllPlayer;

    @Override
    @NotNull
    public Option newInstance() {
        return new PlaySound();
    }

    @Override
    protected boolean isValid() throws Exception {
        var array = StringUtils.split(getOptionValue(), '/');
        var param = StringUtils.split(array[0], '-');
        var delay = param.length > 3 ? Long.parseLong(param[3]) : 0;
        this.name = StringUtils.removeStart(param[0], Utils.MINECRAFT).toLowerCase(Locale.ROOT);
        this.volume = Integer.parseInt(param[1]);
        this.pitch = Integer.parseInt(param[2]);
        this.sendAllPlayer = array.length > 1 && Boolean.parseBoolean(array[1]);

        if (delay < 1) {
            playSound();
        } else {
            Bukkit.getScheduler().runTaskLater(ScriptBlock.getInstance(), this, delay);
        }
        return true;
    }

    @Override
    public void run() {
        StreamUtils.ifAction(getSBPlayer().isOnline(), () -> playSound());
    }

    private void playSound() {
        var sound = getSound(name);
        var location = getLocation();
        for (var player : sendAllPlayer ? Bukkit.getOnlinePlayers() : Collections.singleton(getPlayer())) {
            if (sound == null) {
                player.playSound(location, name, volume, pitch);
            } else {
                player.playSound(location, sound, volume, pitch);
            }
        }
    }

    @Nullable
    private Sound getSound(@NotNull String name) {
        var upper = name.toUpperCase(Locale.ROOT);
        if (StreamUtils.anyMatch(Sound.values(), s -> s.name().equals(upper))) {
            return Sound.valueOf(upper);
        }
        return null;
    }
}