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
import com.github.yuttyann.scriptblockplus.script.ScriptKey;
import com.google.gson.annotations.SerializedName;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Set;
import java.util.Map.Entry;

/**
 * ScriptBlockPlus BlockScript クラス
 * 
 * @author yuttyann44581
 */
public class BlockScript {

    @SerializedName(value = "scriptkey", alternate = { "scripttype" })
    private final ScriptKey scriptKey;

    @SerializedName("scripts")
    private final HashMap<String, ScriptParam> scripts = new HashMap<>();

    public BlockScript(@NotNull ScriptKey scriptKey) {
        this.scriptKey = scriptKey;
    }

    @NotNull
    public Set<Entry<String, ScriptParam>> getEntrySet() {
        return scripts.entrySet();
    }

    @NotNull
    public ScriptKey getScriptKey() {
        return scriptKey;
    }

    public boolean has(@NotNull Location location) {
        var fullCoords = BlockCoords.getFullCoords(location);
        var scriptParam = scripts.get(fullCoords);
        if (scriptParam == null) {
            return false;
        }
        return scriptParam.getAuthor().size() > 0;
    }

    @NotNull
    public ScriptParam get(@NotNull Location location) {
        var fullCoords = BlockCoords.getFullCoords(location);
        var scriptParam = scripts.get(fullCoords);
        if (scriptParam == null) {
            scripts.put(fullCoords, scriptParam = new ScriptParam());
        }
        return scriptParam;
    }

    public void remove(@NotNull Location location) {
        scripts.remove(BlockCoords.getFullCoords(location));
    }

    @Override
    public int hashCode() {
        return scriptKey.hashCode();
    }
}