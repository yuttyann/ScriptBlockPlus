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
import java.util.Set;

import com.github.yuttyann.scriptblockplus.file.json.BlockScriptJson;
import com.github.yuttyann.scriptblockplus.file.json.PlayerCountJson;
import com.github.yuttyann.scriptblockplus.script.SBClipboard;
import com.github.yuttyann.scriptblockplus.script.ScriptKey;
import com.github.yuttyann.scriptblockplus.utils.Utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

/**
 * ScriptBlockPlus CuboidRegionPaste クラス
 * @author yuttyann44581
 */
public class CuboidRegionPaste {

    private final ScriptKey scriptKey;
    private final SBClipboard sbClipboard;
    private final CuboidRegionBlocks regionBlocks;

    public CuboidRegionPaste(@NotNull SBClipboard sbClipboard, @NotNull Region region) {
        this.scriptKey = sbClipboard.getBlockScriptJson().getScriptKey();
        this.sbClipboard = sbClipboard;
        this.regionBlocks = new CuboidRegionBlocks(region);
    }

    @NotNull
    public ScriptKey getScriptKey() {
        return scriptKey;
    }

    @NotNull
    public CuboidRegionBlocks getRegionBlocks() {
        return regionBlocks;
    }

    public CuboidRegionPaste paste(boolean pasteonair, boolean overwrite) {
        var locations = new HashSet<Location>(regionBlocks.getCount());
        for (var block : regionBlocks.getBlocks()) {
            if (!pasteonair && (block == null || block.getType() == Material.AIR)) {
                continue;
            }
            lightPaste(locations, block.getLocation(), overwrite);
        }
        PlayerCountJson.clear(locations, scriptKey);
        sbClipboard.save();
        return this;
    }

    private boolean lightPaste(@NotNull Set<Location> locations, @NotNull Location location, boolean overwrite) {
        var scriptJson = sbClipboard.getBlockScriptJson();
        if (BlockScriptJson.has(location, scriptJson) && !overwrite) {
            return false;
        }
        var scriptParam = scriptJson.load().get(location);
        scriptParam.setAuthor(sbClipboard.getAuthor());
        scriptParam.getAuthor().add(sbClipboard.getSBPlayer().getUniqueId());
        scriptParam.setScript(sbClipboard.getScript());
        scriptParam.setLastEdit(Utils.getFormatTime(Utils.DATE_PATTERN));
        scriptParam.setSelector(sbClipboard.getSelector());
        scriptParam.setAmount(sbClipboard.getAmount());
        locations.add(location);
        return true;
    }
}