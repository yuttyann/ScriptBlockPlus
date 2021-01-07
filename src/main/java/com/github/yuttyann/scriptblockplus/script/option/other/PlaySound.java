package com.github.yuttyann.scriptblockplus.script.option.other;

import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.script.option.Option;
import com.github.yuttyann.scriptblockplus.script.option.OptionTag;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.jetbrains.annotations.NotNull;

/**
 * ScriptBlockPlus PlaySound オプションクラス
 * @author yuttyann44581
 */
@OptionTag(name = "sound", syntax = "@sound:")
public class PlaySound extends BaseOption implements Runnable {

    private Sound sound;
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
        this.sound = Sound.valueOf(param[0].toUpperCase());
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
        playSound();
    }

    private void playSound() {
        var world = getLocation().getWorld();
        if (sendAllPlayer && world != null) {
            world.playSound(getLocation(), sound, volume, pitch);
        } else if (getSBPlayer().isOnline()) {
            getPlayer().playSound(getLocation(), sound, volume, pitch);
        }
    }
}