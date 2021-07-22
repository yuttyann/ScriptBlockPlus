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

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.file.config.SBConfig;
import com.github.yuttyann.scriptblockplus.file.json.derived.BlockScriptJson;
import com.github.yuttyann.scriptblockplus.file.json.derived.PlayerCountJson;
import com.github.yuttyann.scriptblockplus.file.json.derived.PlayerTimerJson;
import com.github.yuttyann.scriptblockplus.file.json.element.BlockScript;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.utils.Utils;
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
    private final BlockScript blockScript;
    private final BlockScriptJson scriptJson;

    public SBClipboard(@NotNull SBPlayer sbPlayer, @NotNull ScriptKey scriptKey, @NotNull BlockCoords blockCoords) {
        this.sbPlayer = sbPlayer;
        this.scriptKey = scriptKey;
        this.scriptJson = BlockScriptJson.newJson(scriptKey); 
        this.blockCoords = new UnmodifiableBlockCoords(blockCoords);

        var copyScript = scriptJson.fastLoad(this.blockCoords);
        try {
            copyScript = copyScript == null ? null : (BlockScript) copyScript.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        this.blockScript = copyScript;
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
        return blockScript;
    }

    @NotNull
    public BlockScriptJson getBlockScriptJson() {
        return scriptJson;
    }

    public boolean copy() {
        if (blockScript == null || !scriptJson.has(blockCoords)) {
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
        if (blockScript == null || (scriptJson.has(blockCoords) && !overwrite)) {
            return false;
        }
        try {
            var scriptParam = scriptJson.load(blockCoords);
            scriptParam.setAuthors(blockScript.getAuthors());
            scriptParam.getAuthors().add(sbPlayer.getUniqueId());
            scriptParam.setScripts(blockScript.getScripts());
            scriptParam.setLastEdit(Utils.getFormatTime(Utils.DATE_PATTERN));
            scriptParam.setSelector(blockScript.getSelector());
            scriptParam.setAmount(blockScript.getAmount());
            scriptJson.saveJson();
            PlayerTimerJson.removeAll(scriptKey, blockCoords);
            PlayerCountJson.removeAll(scriptKey, blockCoords);
            SBConfig.SCRIPT_PASTE.replace(scriptKey).send(sbPlayer);
            SBConfig.CONSOLE_SCRIPT_EDIT.replace(scriptKey, blockCoords).console();
        } finally {
            sbPlayer.setScriptEdit(null);
            sbPlayer.setSBClipboard(null);
        }
        return true;
    }
}