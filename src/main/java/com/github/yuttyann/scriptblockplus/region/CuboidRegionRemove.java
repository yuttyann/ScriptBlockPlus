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
package com.github.yuttyann.scriptblockplus.region;

import com.github.yuttyann.scriptblockplus.file.json.derived.BlockScriptJson;
import com.github.yuttyann.scriptblockplus.file.json.derived.PlayerCountJson;
import com.github.yuttyann.scriptblockplus.script.ScriptKey;
import com.github.yuttyann.scriptblockplus.script.option.time.TimerOption;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * ScriptBlockPlus CuboidRegionRemove クラス
 * @author yuttyann44581
 */
public class CuboidRegionRemove {

    private final Set<ScriptKey> scriptKeys;
    private final CuboidRegionBlocks regionBlocks;

    public CuboidRegionRemove(@NotNull Region region) {
        this.scriptKeys = new LinkedHashSet<>();
        this.regionBlocks = new CuboidRegionBlocks(region);
    }

    @NotNull
    public Set<ScriptKey> getScriptKeys() {
        return scriptKeys;
    }

    @NotNull
    public CuboidRegionBlocks getRegionBlocks() {
        return regionBlocks;
    }

    public void init() {
        scriptKeys.clear();
    }

    public CuboidRegionRemove remove() {
        init();
        var blocks = regionBlocks.getBlocks();
        var locations = new HashSet<Location>(regionBlocks.getCount());
        for (var scriptKey : ScriptKey.values()) {
            var scriptJson = new BlockScriptJson(scriptKey);
            if (!scriptJson.exists()) {
                continue;
            }
            for (var block : blocks) {
                if (lightRemove(locations, block.getLocation(), scriptJson)) {
                    scriptKeys.add(scriptKey);
                }
            }
            scriptJson.saveFile();
        }
        for (var scriptKey : scriptKeys) {
            TimerOption.removeAll(locations, scriptKey);
            PlayerCountJson.clear(locations, scriptKey);
        }
        return this;
    }
    
    private boolean lightRemove(@NotNull Set<Location> locations, @NotNull Location location, @NotNull BlockScriptJson scriptJson) {
        if (!BlockScriptJson.has(location, scriptJson)) {
            return false;
        }
        scriptJson.load().remove(location);
        locations.add(location);
        return true;
    }
}