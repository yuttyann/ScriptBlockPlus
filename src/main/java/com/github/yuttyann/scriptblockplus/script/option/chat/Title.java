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
package com.github.yuttyann.scriptblockplus.script.option.chat;

import static com.github.yuttyann.scriptblockplus.utils.version.McVersion.*;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.github.yuttyann.scriptblockplus.enums.Permission;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.script.option.OptionTag;
import com.github.yuttyann.scriptblockplus.utils.Utils;

/**
 * ScriptBlockPlus Title オプションクラス
 * @author yuttyann44581
 */
@OptionTag(name = "title", syntax = "@title:", description = "<main>[/sub][/fadeIn-stay-fadeout]")
public final class Title extends BaseOption {

    @Override
    protected Result isValid() throws Exception {
        var slash = split(getOptionValue(), '/', false);
        var title = setColor(slash.get(0), true);
        var subtitle = setColor(slash.size() > 1 ? slash.get(1) : "", true);
        int fadeIn = 10, stay = 40, fadeOut = 10;
        if (slash.size() == 3) {
            var times = split(slash.get(2), '-', false);
            if (times.size() == 3) {
                fadeIn = Integer.parseInt(times.get(0));
                stay = Integer.parseInt(times.get(1));
                fadeOut = Integer.parseInt(times.get(2));
            }
        }
        send(getSBPlayer(), title, subtitle, fadeIn, stay, fadeOut);
        return Result.SUCCESS;
    }

    public static void send(@NotNull SBPlayer sbPlayer, @Nullable String title, @Nullable String subtitle, int fadeIn, int stay, int fadeOut) {
        var player = sbPlayer.toPlayer();
        if (V_1_12.isSupported()) {
            player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
        } else {
            var prefix = "minecraft:title " + sbPlayer.getName();
            Utils.tempPerm(sbPlayer, Permission.MINECRAFT_COMMAND_TITLE, () -> {
                Bukkit.dispatchCommand(player, prefix + " times " + fadeIn + " " + stay + " " + fadeOut);
                Bukkit.dispatchCommand(player, prefix + " subtitle {\"text\":\"" + subtitle + "\"}");
                Bukkit.dispatchCommand(player, prefix + " title {\"text\":\"" + title + "\"}");
                return true;
            });
        }
    }
}