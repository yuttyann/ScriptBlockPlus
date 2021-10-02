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

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.file.json.derived.BlockScriptJson;
import com.github.yuttyann.scriptblockplus.script.ScriptKey;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * ScriptBlockPlus CuboidRegionRemove クラス
 * @author yuttyann44581
 */
public final class CuboidRegionRemove {

    private final Region region;
    private final Set<ScriptKey> scriptKeys;

    private int volume;
    private CuboidRegionIterator iterator;

    public CuboidRegionRemove(@NotNull Region region) {
        this.region = region;
        this.scriptKeys = new LinkedHashSet<>();
    }

    public int getVolume() {
        return volume;
    }

    @NotNull
    public Set<ScriptKey> getScriptKeys() {
        return scriptKeys;
    }

    @Nullable
    public CuboidRegionIterator iterator() {
        return iterator;
    }

    @NotNull
    public CuboidRegionRemove remove() {
        scriptKeys.clear();
        var blocks = new HashSet<BlockCoords>();
        var iterator = new CuboidRegionIterator(region);
        for (var scriptKey : ScriptKey.iterable()) {
            var scriptJson = BlockScriptJson.newJson(scriptKey);
            if (scriptJson.isEmpty()) {
                continue;
            }
            iterator.reset();
            var removed = false;
            while (iterator.hasNext()) {
                var blockCoords = iterator.next();
                if (lightRemove(blockCoords, scriptJson)) {
                    removed = true;
                    if (!blocks.contains(blockCoords)) {
                        blocks.add(BlockCoords.copy(blockCoords));
                    }
                }
            }
            if (removed) {
                scriptKeys.add(scriptKey);
                scriptJson.saveJson();
            }
        }
        var blockCoords = blocks.toArray(BlockCoords[]::new);
        scriptKeys.forEach(s -> BlockScriptJson.newJson(s).init(blockCoords));
        this.iterator = iterator;
        this.volume = blocks.size();
        return this;
    }
    
    private boolean lightRemove(@NotNull BlockCoords blockCoords, @NotNull BlockScriptJson scriptJson) {
        return scriptJson.has(blockCoords) ? scriptJson.remove(blockCoords) : false;
    }
}