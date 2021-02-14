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
public final class Delay extends BaseOption implements Runnable {

    private static final Set<Integer> DELAYS = new HashSet<>();

    private boolean saveDelay;

    @Override
    public boolean isFailedIgnore() {
        return true;
    }

    @Override
    protected boolean isValid() throws Exception {
        var array = StringUtils.split(getOptionValue(), '/');
        this.saveDelay = array.length <= 1 || Boolean.parseBoolean(array[1]);

        if (saveDelay && DELAYS.contains(delayHash())) {
            SBConfig.ACTIVE_DELAY.send(getSBPlayer());
        } else {
            if (saveDelay) {
                DELAYS.add(delayHash());
            }
            ((ScriptRead) getTempMap()).setInitialize(false);
            ScriptBlock.getScheduler().run(this, Long.parseLong(array[0]));
        }
        return false;
    }

    @Override
    public void run() {
        if (saveDelay) {
            DELAYS.remove(delayHash());
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

    private int delayHash() {
        int hash = 1;
        int prime = 31;
        hash = prime * hash + getUniqueId().hashCode();
        hash = prime * hash + getScriptKey().hashCode();
        hash = prime * hash + getBlockCoords().hashCode();
        return hash;
    }
}