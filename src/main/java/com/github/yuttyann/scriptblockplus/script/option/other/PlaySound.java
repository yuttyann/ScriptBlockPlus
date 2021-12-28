/**
 * ScriptBlockPlus - Allow you to add script to any blocks.
 * Copyright (C) 2021 yuttyann44581
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <https://www.gnu.org/licenses/>.
 */
package com.github.yuttyann.scriptblockplus.script.option.other;

import java.util.Locale;
import java.util.function.Consumer;

import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.script.option.OptionTag;
import com.github.yuttyann.scriptblockplus.utils.StreamUtils;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * ScriptBlockPlus PlaySound オプションクラス
 * @author yuttyann44581
 */
@OptionTag(name = "sound", syntax = "@sound:", description = "<sound>-<volume>-<pitch>[-delay][/sendtoallplayers]")
public final class PlaySound extends BaseOption implements Runnable {

    private String name;
    private int volume, pitch;
    private boolean sendAllPlayer;

    @Override
    protected boolean isValid() throws Exception {
        var slash = split(getOptionValue(), '/', false);
        var hyphen = split(slash.get(0), '-', false);
        this.name = StringUtils.removeStart(hyphen.get(0), Utils.MINECRAFT).toLowerCase(Locale.ROOT);
        var delay = hyphen.size() > 3 ? Long.parseLong(hyphen.get(3)) : 0L;
        this.volume = Integer.parseInt(hyphen.get(1));
        this.pitch = Integer.parseInt(hyphen.get(2));
        this.sendAllPlayer = slash.size() > 1 && Boolean.parseBoolean(slash.get(1));

        if (delay > 0) {
            ScriptBlock.getScheduler().asyncRun(this, delay);
        } else {
            run();
        }
        return true;
    }

    @Override
    public void run() {
        var playSound = (Consumer<Player>) p -> {
            var sound = getSound(name);
            var location = getLocation();
            if (sound == null) {
                p.playSound(location, name, volume, pitch);
            } else {
                p.playSound(location, sound, volume, pitch);
            }
        };
        if (sendAllPlayer) {
            Bukkit.getOnlinePlayers().forEach(playSound);
        } else if (getSBPlayer().isOnline()) {
            playSound.accept(getSBPlayer().getPlayer());
        }
    }

    @Nullable
    private Sound getSound(@NotNull String name) {
        var upper = name.toUpperCase(Locale.ROOT);
        return StreamUtils.anyMatch(Sound.values(), s -> s.name().equals(upper)) ? Sound.valueOf(upper) : null;
    }
}