package com.github.yuttyann.scriptblockplus.script.option.other;

import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.script.option.Option;
import com.github.yuttyann.scriptblockplus.script.option.OptionTag;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

/**
 * ScriptBlockPlus PlaySound オプションクラス
 * @author yuttyann44581
 */
@OptionTag(name = "sound", syntax = "@sound:")
public class PlaySound extends BaseOption {

    @Override
    @NotNull
    public Option newInstance() {
        return new PlaySound();
    }

    @Override
    protected boolean isValid() throws Exception {
        var array = StringUtils.split(getOptionValue(), "/");
        var sound = StringUtils.split(array[0], "-");
        var type = Sound.valueOf(sound[0].toUpperCase());
        int volume = Integer.parseInt(sound[1]);
        int pitch = Integer.parseInt(sound[2]);
        long delay = sound.length > 3 ? Long.parseLong(sound[3]) : 0;
        boolean sendAllPlayer = array.length > 1 && Boolean.parseBoolean(array[1]);

        if (delay > 0) {
            new Task(type, volume, pitch, sendAllPlayer).runTaskLater(ScriptBlock.getInstance(), delay);
        } else {
            playSound(type, volume, pitch, sendAllPlayer);
        }
        return true;
    }

    private void playSound(@NotNull Sound soundType, int volume, int pitch, boolean sendAllPlayer) {
        var world = getLocation().getWorld();
        if (sendAllPlayer && world != null) {
            world.playSound(getLocation(), soundType, volume, pitch);
        } else if (getSBPlayer().isOnline()) {
            getPlayer().playSound(getLocation(), soundType, volume, pitch);
        }
    }

    private class Task extends BukkitRunnable {

        private final Sound soundType;
        private final int volume;
        private final int pitch;
        private final boolean sendAllPlayer;

        Task(@NotNull Sound soundType, int volume, int pitch, boolean sendAllPlayer) {
            this.soundType = soundType;
            this.volume = volume;
            this.pitch = pitch;
            this.sendAllPlayer = sendAllPlayer;
        }

        @Override
        public void run() {
            playSound(soundType, volume, pitch, sendAllPlayer);
        }
    }
}