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
package com.github.yuttyann.scriptblockplus.file.json.element;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.file.json.BaseElement;
import com.github.yuttyann.scriptblockplus.file.json.annotation.LegacyName;
import com.github.yuttyann.scriptblockplus.script.ScriptKey;
import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

/**
 * ScriptBlockPlus BlockScript クラス
 * @author yuttyann44581
 */
public class BlockScript extends BaseElement {

    @LegacyName(alternate = { "scripttype" })
    @SerializedName(value = "scriptkey", alternate = { "scripttype" })
    private final ScriptKey scriptKey;

    @SerializedName("scripts")
    private final HashMap<BlockCoords, ScriptParam> scripts = new HashMap<>();

    public BlockScript(@NotNull ScriptKey scriptKey) {
        this.scriptKey = scriptKey;
    }

    @NotNull
    public ScriptKey getScriptKey() {
        return scriptKey;
    }

    public boolean has(@NotNull BlockCoords blockCoords) {
        var scriptParam = scripts.get(blockCoords);
        if (scriptParam == null) {
            return false;
        }
        return scriptParam.getAuthor().size() > 0;
    }

    @NotNull
    public ScriptParam get(@NotNull BlockCoords blockCoords) {
        var scriptParam = scripts.get(blockCoords);
        if (scriptParam == null) {
            scripts.put(blockCoords, scriptParam = new ScriptParam());
        }
        return scriptParam;
    }

    public void remove(@NotNull BlockCoords location) {
        scripts.remove(location);
    }
}