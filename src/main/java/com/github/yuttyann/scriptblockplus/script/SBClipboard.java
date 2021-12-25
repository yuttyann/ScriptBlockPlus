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
package com.github.yuttyann.scriptblockplus.script;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.file.config.SBConfig;
import com.github.yuttyann.scriptblockplus.file.json.derived.BlockScriptJson;
import com.github.yuttyann.scriptblockplus.file.json.derived.element.BlockScript;
import com.github.yuttyann.scriptblockplus.file.json.derived.element.ValueHolder;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.utils.unmodifiable.UnmodifiableBlockCoords;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * ScriptBlockPlus SBClipboard クラス
 * @author yuttyann44581
 */
public final class SBClipboard {

    private final SBPlayer sbPlayer;
    private final ScriptKey scriptKey;
    private final BlockCoords blockCoords;
    private final BlockScript cloneScript;
    private final BlockScriptJson scriptJson;

    public SBClipboard(@NotNull SBPlayer sbPlayer, @NotNull ScriptKey scriptKey, @NotNull BlockCoords blockCoords) {
        this.sbPlayer = sbPlayer;
        this.scriptKey = scriptKey;
        this.scriptJson = BlockScriptJson.get(scriptKey); 
        this.blockCoords = new UnmodifiableBlockCoords(blockCoords);

        var cloneScript = scriptJson.fastLoad(this.blockCoords);
        try {
            cloneScript = cloneScript == null ? null : (BlockScript) cloneScript.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        this.cloneScript = cloneScript;
    }

    @NotNull
    public SBPlayer getSBPlayer() {
        return sbPlayer;
    }

    @NotNull
    public ScriptKey getScriptKey() {
        return scriptKey;
    }

    @NotNull
    public BlockCoords getBlockCoords() {
        return blockCoords;
    }

    @Nullable
    public BlockScript getBlockScript() {
        return cloneScript;
    }

    @NotNull
    public BlockScriptJson getBlockScriptJson() {
        return scriptJson;
    }

    public boolean copy() {
        if (cloneScript == null || !scriptJson.has(blockCoords)) {
            SBConfig.ERROR_SCRIPT_FILE_CHECK.send(sbPlayer);
            return false;
        }
        try {
            sbPlayer.setSBClipboard(this);
            SBConfig.SCRIPT_COPY.replace(scriptKey).send(sbPlayer);
            SBConfig.CONSOLE_SCRIPT_EDIT.replace(scriptKey, blockCoords).console();
        } finally {
            sbPlayer.setScriptEdit(null);
        }
        return true;
    }

    public boolean paste(@NotNull BlockCoords blockCoords, boolean overwrite) {
        if (cloneScript == null || (scriptJson.has(blockCoords) && !overwrite)) {
            return false;
        }
        try {
            var blockScript = scriptJson.load(blockCoords);
            blockScript.setAuthors(cloneScript.getAuthors());
            blockScript.getAuthors().add(sbPlayer.getUniqueId());
            blockScript.setScripts(cloneScript.getScripts());
            blockScript.setLastEdit(new Date());
            blockScript.setValues(cloneValues());
            scriptJson.init(blockCoords);
            scriptJson.saveJson();
            SBConfig.SCRIPT_PASTE.replace(scriptKey).send(sbPlayer);
            SBConfig.CONSOLE_SCRIPT_EDIT.replace(scriptKey, blockCoords).console();
        } finally {
            sbPlayer.setScriptEdit(null);
            sbPlayer.setSBClipboard(null);
        }
        return true;
    }

    @Nullable
    public Map<String, ValueHolder> cloneValues() {
        if (!cloneScript.hasValues()) {
            return null;
        }
        var newMap = new HashMap<String, ValueHolder>();
        for (var entry : cloneScript.getValues().entrySet()) {
            newMap.put(entry.getKey(), entry.getValue().clone());
        }
        return newMap;
    }
}