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

import org.bukkit.Bukkit;

import java.util.HashSet;
import java.util.Set;

/**
 * ScriptBlockPlus Delay オプションクラス
 * @author yuttyann44581
 */
@OptionTag(name = "delay", syntax = "@delay:", description = "<tick>[/stay]")
public final class Delay extends BaseOption implements Runnable {

    private static final Set<Integer> DELAYS = new HashSet<>();

    private boolean stay;

    @Override
    public boolean isFailedIgnore() {
        return true;
    }

    @Override
    protected boolean isValid() throws Exception {
        if (isInverted()) {
            throw new IllegalArgumentException("This option cannot be inverted");
        }
        var slash = split(getOptionValue(), '/', false);
        this.stay = slash.size() < 2 || Boolean.parseBoolean(slash.get(1));

        if (stay && DELAYS.contains(delayHash())) {
            SBConfig.ACTIVE_DELAY.send(getSBPlayer());
        } else {
            if (stay) {
                DELAYS.add(delayHash());
            }
            if (((ScriptRead) getTempMap()).isAsynchronous()) {
                ScriptBlock.getScheduler().asyncRun(this, Long.parseLong(slash.get(0)));
            } else {
                ScriptBlock.getScheduler().run(this, Long.parseLong(slash.get(0)));
            }
        }
        return false;
    }

    @Override
    public void run() {
        if (stay) {
            DELAYS.remove(delayHash());
        }
        var scriptRead = (ScriptRead) getTempMap();
        if (getSBPlayer().isOnline()) {
            Bukkit.getPluginManager().callEvent(new DelayRunEvent(scriptRead));
            try {
                scriptRead.read(getScriptIndex() + 1);
            } finally {
                Bukkit.getPluginManager().callEvent(new DelayEndEvent(scriptRead));
            }
        } else {
            EndProcessManager.forEachFinally(e -> e.failed(scriptRead), () -> scriptRead.clear());
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