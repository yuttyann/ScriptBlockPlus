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
package com.github.yuttyann.scriptblockplus.script.option.discord;

import com.github.yuttyann.scriptblockplus.hook.plugin.DiscordSRV;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.script.option.OptionTag;

/**
 * ScriptBlockPlus DiscordChannel オプションクラス
 * @author yuttyann44581
 */
@OptionTag(name = "discordchannel", syntax = "@dchannel:", description = "<channelid>")
public class DiscordChannel extends BaseOption {

    @Override
    protected Result isValid() throws Exception {
        if (!DiscordSRV.INSTANCE.isEnabled()) {
            throw new UnsupportedOperationException("Invalid function");
        }
        var channelId = DiscordSRV.INSTANCE.getVoiceChannelId(getUniqueId());
        return toResult(channelId == null ? false : channelId.equals(getOptionValue()));
    }
}