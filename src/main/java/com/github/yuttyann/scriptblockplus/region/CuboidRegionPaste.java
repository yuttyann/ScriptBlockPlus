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

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.file.json.derived.PlayerCountJson;
import com.github.yuttyann.scriptblockplus.file.json.derived.PlayerTempJson;
import com.github.yuttyann.scriptblockplus.script.SBClipboard;
import com.github.yuttyann.scriptblockplus.script.ScriptKey;
import com.github.yuttyann.scriptblockplus.utils.StreamUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * ScriptBlockPlus CuboidRegionPaste クラス
 * @author yuttyann44581
 */
public class CuboidRegionPaste {

    private final Region region;
    private final ScriptKey scriptKey;
    private final SBClipboard sbClipboard;

    private CuboidRegionIterator iterator;

    public CuboidRegionPaste(@NotNull Region region, @NotNull SBClipboard sbClipboard) {
        this.region = region;
        this.scriptKey = sbClipboard.getBlockScriptJson().getScriptKey();
        this.sbClipboard = sbClipboard;
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
        var blocks = new HashSet<BlockCoords>();
        var iterator = new CuboidRegionIterator(region);
        while (iterator.hasNext()) {
            var blockCoords = iterator.next();
            if (!pasteonair && blockCoords.getBlock().getType() == Material.AIR) {
                continue;
            }
            if (lightPaste(blockCoords, overwrite)) {
                blocks.add(blockCoords);
            }
        }
        PlayerTempJson.removeAll(scriptKey, blocks);
        PlayerCountJson.removeAll(scriptKey, blocks);
        StreamUtils.ifAction(blocks.size() > 0, sbClipboard::save);
        this.iterator = iterator;
        return this;
    }

    private boolean lightPaste(@NotNull BlockCoords blockCoords, boolean overwrite) {
        var scriptJson = sbClipboard.getBlockScriptJson();
        if (scriptJson.has() && scriptJson.load().has(blockCoords) && !overwrite) {
            return false;
        }
        var scriptParam = scriptJson.load().get(blockCoords);
        scriptParam.setAuthor(sbClipboard.getAuthor());
        scriptParam.getAuthor().add(sbClipboard.getSBPlayer().getUniqueId());
        scriptParam.setScript(sbClipboard.getScript());
        scriptParam.setLastEdit(Utils.getFormatTime(Utils.DATE_PATTERN));
        scriptParam.setSelector(sbClipboard.getSelector());
        scriptParam.setAmount(sbClipboard.getAmount());
        return true;
    }
}