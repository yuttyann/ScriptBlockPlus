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
import com.github.yuttyann.scriptblockplus.file.json.derived.PlayerCountJson;
import com.github.yuttyann.scriptblockplus.file.json.derived.PlayerTimerJson;
import com.github.yuttyann.scriptblockplus.item.action.TickRunnable;
import com.github.yuttyann.scriptblockplus.script.ScriptKey;
import com.github.yuttyann.scriptblockplus.utils.collection.ReuseIterator;

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

    private CuboidRegionIterator iterator;

    public CuboidRegionRemove(@NotNull Region region) {
        this.region = region;
        this.scriptKeys = new LinkedHashSet<>();
    }

    @NotNull
    public Set<ScriptKey> getScriptKeys() {
        return scriptKeys;
    }

    @Nullable
    public CuboidRegionIterator result() {
        return iterator;
    } 

    @NotNull
    public CuboidRegionRemove remove() {
        scriptKeys.clear();
        var blocks = new HashSet<BlockCoords>();
        var iterator = new CuboidRegionIterator(region);
        try {
            for (var scriptKey : ScriptKey.iterable()) {
                var scriptJson = BlockScriptJson.get(scriptKey);
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
                            TickRunnable.GLOW_ENTITY.broadcastDestroy(blockCoords);
                        }
                    }
                }
                if (removed) {
                    scriptKeys.add(scriptKey);
                    scriptJson.saveJson();
                }
            }
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        } finally {
            var reuseIterator = new ReuseIterator<>(blocks, BlockCoords[]::new);
            for (var scriptKey : scriptKeys) {
                PlayerTimerJson.removeAll(scriptKey, reuseIterator);
                PlayerCountJson.removeAll(scriptKey, reuseIterator);
            }
            this.iterator = iterator;
        }
        return this;
    }
    
    private boolean lightRemove(@NotNull BlockCoords blockCoords, @NotNull BlockScriptJson scriptJson) {
        return scriptJson.has(blockCoords) ? scriptJson.remove(blockCoords) : false;
    }
}