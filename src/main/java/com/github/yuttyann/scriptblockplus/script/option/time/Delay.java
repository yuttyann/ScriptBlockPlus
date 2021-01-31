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
package com.github.yuttyann.scriptblockplus.script.option.time;

import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.event.DelayEndEvent;
import com.github.yuttyann.scriptblockplus.event.DelayRunEvent;
import com.github.yuttyann.scriptblockplus.file.config.SBConfig;
import com.github.yuttyann.scriptblockplus.manager.EndProcessManager;
import com.github.yuttyann.scriptblockplus.script.ScriptRead;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.script.option.OptionTag;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;

import org.bukkit.Bukkit;

import java.util.HashSet;
import java.util.Set;

/**
 * ScriptBlockPlus Delay オプションクラス
 * @author yuttyann44581
 */
@OptionTag(name = "delay", syntax = "@delay:")
public class Delay extends BaseOption implements Runnable {

    public static final Set<TimerTemp> DELAY_SET = new HashSet<>();

    private boolean saveDelay;

    @Override
    public boolean isFailedIgnore() {
        return true;
    }

    @Override
    protected boolean isValid() throws Exception {
        var array = StringUtils.split(getOptionValue(), '/');
        this.saveDelay = array.length <= 1 || Boolean.parseBoolean(array[1]);

        if (saveDelay && DELAY_SET.contains(new TimerTemp(getUniqueId(), getLocation(), getScriptKey()))) {
            SBConfig.ACTIVE_DELAY.send(getSBPlayer());
        } else {
            if (saveDelay) {
                DELAY_SET.add(new TimerTemp(getUniqueId(), getLocation(), getScriptKey()));
            }
            ((ScriptRead) getTempMap()).setInitialize(false);
            Bukkit.getScheduler().runTaskLater(ScriptBlock.getInstance(), this, Long.parseLong(array[0]));
        }
        return false;
    }

    @Override
    public void run() {
        if (saveDelay) {
            DELAY_SET.remove(new TimerTemp(getUniqueId(), getLocation(), getScriptKey()));
        }
        var sbRead = (ScriptRead) getTempMap();
        if (getSBPlayer().isOnline()) {
            Bukkit.getPluginManager().callEvent(new DelayRunEvent(sbRead));
            try {
                sbRead.setInitialize(true);
                sbRead.read(getScriptIndex() + 1);
            } finally {
                Bukkit.getPluginManager().callEvent(new DelayEndEvent(sbRead));
            }
        } else {
            EndProcessManager.forEachFinally(e -> e.failed(sbRead), () -> sbRead.clear());
        }
    }
}