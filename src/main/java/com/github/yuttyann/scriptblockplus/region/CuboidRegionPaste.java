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

import java.util.Date;
import java.util.HashSet;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.file.json.derived.BlockScriptJson;
import com.github.yuttyann.scriptblockplus.script.SBClipboard;
import com.github.yuttyann.scriptblockplus.script.ScriptKey;
import com.github.yuttyann.scriptblockplus.utils.ItemUtils;
import com.github.yuttyann.scriptblockplus.utils.StreamUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * ScriptBlockPlus CuboidRegionPaste クラス
 * @author yuttyann44581
 */
public final class CuboidRegionPaste {

    private final Region region;
    private final ScriptKey scriptKey;
    private final SBClipboard sbClipboard;

    private int volume;
    private CuboidRegionIterator iterator;

    public CuboidRegionPaste(@NotNull Region region, @NotNull SBClipboard sbClipboard) {
        this.region = region;
        this.scriptKey = sbClipboard.getBlockScriptJson().getScriptKey();
        this.sbClipboard = sbClipboard;
    }

    public int getVolume() {
        return volume;
    }

    @NotNull
    public ScriptKey getScriptKey() {
        return scriptKey;
    }

    @Nullable
    public CuboidRegionIterator iterator() {
        return iterator;
    }

    @NotNull
    public CuboidRegionPaste paste(boolean pasteonair, boolean overwrite) {
        var date = new Date();
        var blocks = new HashSet<BlockCoords>();
        var iterator = new CuboidRegionIterator(region);
        while (iterator.hasNext()) {
            var blockCoords = iterator.next();
            if (!pasteonair && ItemUtils.isAIR(blockCoords.getBlock().getType())) {
                continue;
            }
            var scriptJson = sbClipboard.getBlockScriptJson();
            if (!overwrite && scriptJson.has(blockCoords)) {
                continue;
            }
            blocks.add(blockCoords = BlockCoords.copy(blockCoords));
            lightPaste(date, blockCoords, scriptJson);
        }
        sbClipboard.getBlockScriptJson().init(blocks.toArray(BlockCoords[]::new));
        StreamUtils.ifAction(blocks.size() > 0, () -> sbClipboard.getBlockScriptJson().saveJson());
        this.iterator = iterator;
        this.volume = blocks.size();
        return this;
    }

    private void lightPaste(@NotNull Date date, @NotNull BlockCoords blockCoords, @NotNull BlockScriptJson scriptJson) {
        var blockScript = scriptJson.load(blockCoords);
        var cloneScript = sbClipboard.getBlockScript();
        blockScript.setAuthors(cloneScript.getAuthors());
        blockScript.getAuthors().add(sbClipboard.getSBPlayer().getUniqueId());
        blockScript.setScripts(cloneScript.getScripts());
        blockScript.setLastEdit(date);
        blockScript.setValues(sbClipboard.cloneValues());
    }
}