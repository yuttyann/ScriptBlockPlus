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

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.file.config.SBConfig;
import com.github.yuttyann.scriptblockplus.file.json.derived.PlayerCountJson;
import com.github.yuttyann.scriptblockplus.file.json.element.PlayerCount;
import com.github.yuttyann.scriptblockplus.hook.plugin.Placeholder;
import com.github.yuttyann.scriptblockplus.manager.EndProcessManager;
import com.github.yuttyann.scriptblockplus.manager.OptionManager;
import com.github.yuttyann.scriptblockplus.script.ScriptKey;
import com.github.yuttyann.scriptblockplus.script.ScriptRead;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.script.option.OptionTag;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * ScriptBlockPlus Execute オプションクラス
 * @author yuttyann44581
 */
@OptionTag(name = "execute", syntax = "@execute:")
public class Execute extends BaseOption {

    @Override
    protected boolean isValid() throws Exception {
        var array = StringUtils.split(getOptionValue(), '/');
        var scriptKey = ScriptKey.valueOf(array[0]);
        return new TempScriptRead(getPlayer(), BlockCoords.fromString(array[1]), scriptKey).read(0);
    }

    private class TempScriptRead extends ScriptRead {

        private TempScriptRead(@NotNull Player player, @NotNull BlockCoords blockCoords, @NotNull ScriptKey scriptKey) {
            super(player, blockCoords, scriptKey);
        }

        @Override
        protected boolean perform(final int index) {
            for (this.index = index; this.index < scripts.size(); this.index++) {
                var script = scripts.get(this.index);
                var option = OptionManager.newInstance(script);
                this.value = Placeholder.INSTANCE.replace(getPlayer(), option.getValue(script));
                if (!option.callOption(this) && isFailedIgnore(option)) {
                    return false;
                }
            }
            EndProcessManager.forEach(e -> e.success(this));
            PlayerCountJson.get(sbPlayer).action(PlayerCount::add, scriptKey, blockCoords);
            SBConfig.CONSOLE_SUCCESS_SCRIPT_EXECUTE.replace(scriptKey, blockCoords).console();
            return true;
        }
    }
}