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

import com.github.yuttyann.scriptblockplus.enums.Permission;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.script.option.Option;
import com.github.yuttyann.scriptblockplus.script.option.OptionTag;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * ScriptBlockPlus Title オプションクラス
 * @author yuttyann44581
 */
@OptionTag(name = "title", syntax = "@title:")
public class Title extends BaseOption {

    @Override
    @NotNull
    public Option newInstance() {
        return new Title();
    }

    @Override
    protected boolean isValid() throws Exception {
        var array = StringUtils.split(getOptionValue(), '/');
        var title = StringUtils.setColor(array[0] + "");
        var subtitle = StringUtils.setColor(array.length > 1 ? array[1] : "");
        int fadeIn = 10, stay = 40, fadeOut = 10;
        if (array.length == 3) {
            var times = StringUtils.split(array[2], '-');
            if (times.length == 3) {
                fadeIn = Integer.parseInt(times[0]);
                stay = Integer.parseInt(times[1]);
                fadeOut = Integer.parseInt(times[2]);
            }
        }
        send(getSBPlayer(), title, subtitle, fadeIn, stay, fadeOut);
        return true;
    }

    public static void send(@NotNull SBPlayer sbPlayer, @Nullable String title, @Nullable String subtitle, int fadeIn, int stay, int fadeOut) {
        if (Utils.isCBXXXorLater("1.12")) {
            sbPlayer.getPlayer().sendTitle(title, subtitle, fadeIn, stay, fadeOut);
        } else {
            var prefix = "minecraft:title " + sbPlayer.getName();
            Utils.tempPerm(sbPlayer, Permission.MINECRAFT_COMMAND_TITLE, () -> {
                Utils.dispatchCommand(sbPlayer, prefix + " times " + fadeIn + " " + stay + " " + fadeOut);
                Utils.dispatchCommand(sbPlayer, prefix + " subtitle {\"text\":\"" + subtitle + "\"}");
                Utils.dispatchCommand(sbPlayer, prefix + " title {\"text\":\"" + title + "\"}");
                return true;
            });
        }
    }
}