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

import java.util.HashSet;
import java.util.Objects;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.file.json.derived.BlockScriptJson;
import com.github.yuttyann.scriptblockplus.file.json.derived.PlayerCountJson;
import com.github.yuttyann.scriptblockplus.file.json.derived.PlayerTimerJson;
import com.github.yuttyann.scriptblockplus.file.json.element.BlockScript;
import com.github.yuttyann.scriptblockplus.script.SBClipboard;
import com.github.yuttyann.scriptblockplus.script.ScriptKey;
import com.github.yuttyann.scriptblockplus.utils.ItemUtils;
import com.github.yuttyann.scriptblockplus.utils.StreamUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import com.github.yuttyann.scriptblockplus.utils.collection.ReuseIterator;

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
    private final BlockScript cloneScript;

    private CuboidRegionIterator iterator;

    public CuboidRegionPaste(@NotNull Region region, @NotNull SBClipboard sbClipboard) {
        this.region = region;
        this.scriptKey = sbClipboard.getBlockScriptJson().getScriptKey();
        this.sbClipboard = sbClipboard;
        this.cloneScript = Objects.requireNonNull(sbClipboard.getBlockScript());
    }

    @NotNull
    public ScriptKey getScriptKey() {
        return scriptKey;
    }

    @Nullable
    public CuboidRegionIterator result() {
        return iterator;
    }

    @NotNull
    public CuboidRegionPaste paste(boolean pasteonair, boolean overwrite) {
        var time = Utils.getFormatTime(Utils.DATE_PATTERN);
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
            lightPaste(time, blockCoords, scriptJson);
        }
        var reuseIterator = new ReuseIterator<>(blocks, BlockCoords[]::new);
        PlayerTimerJson.removeAll(scriptKey, reuseIterator);
        PlayerCountJson.removeAll(scriptKey, reuseIterator);
        StreamUtils.ifAction(blocks.size() > 0, () -> sbClipboard.getBlockScriptJson().saveJson());
        this.iterator = iterator;
        return this;
    }

    private void lightPaste(@NotNull String time, @NotNull BlockCoords blockCoords, @NotNull BlockScriptJson scriptJson) {
        var blockScript = scriptJson.load(blockCoords);
        blockScript.setAuthors(cloneScript.getAuthors());
        blockScript.getAuthors().add(sbClipboard.getSBPlayer().getUniqueId());
        blockScript.setScripts(cloneScript.getScripts());
        blockScript.setLastEdit(time);
        blockScript.setSelector(cloneScript.getSelector());
        blockScript.setAmount(cloneScript.getAmount());
    }
}