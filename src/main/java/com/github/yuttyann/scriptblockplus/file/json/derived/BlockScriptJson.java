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
package com.github.yuttyann.scriptblockplus.file.json.derived;

import com.github.yuttyann.scriptblockplus.file.SBLoader;
import com.github.yuttyann.scriptblockplus.file.json.SingleJson;
import com.github.yuttyann.scriptblockplus.file.json.annotation.JsonTag;
import com.github.yuttyann.scriptblockplus.file.json.element.BlockScript;
import com.github.yuttyann.scriptblockplus.script.ScriptKey;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashSet;

/**
 * ScriptBlockPlus BlockScriptJson クラス
 * @author yuttyann44581
 */
@JsonTag(path = "json/blockscript")
public class BlockScriptJson extends SingleJson<BlockScript> {

    public BlockScriptJson(@NotNull ScriptKey scriptKey) {
        super(scriptKey.getName());
    }

    @NotNull
    public ScriptKey getScriptKey() {
        return ScriptKey.valueOf(getId());
    }

    @Override
    @NotNull
    public BlockScript newInstance() {
        return new BlockScript(getScriptKey());
    }

    public static boolean has(@NotNull Location location) {
        for (var scriptKey : ScriptKey.values()) {
            if (has(location, new BlockScriptJson(scriptKey))) {
                return true;
            }
        }
        return false;
    }

    public static boolean has(@NotNull Location location, @NotNull ScriptKey scriptKey) {
        return has(location, new BlockScriptJson(scriptKey));
    }

    public static boolean has(@NotNull Location location, @NotNull BlockScriptJson scriptJson) {
        return scriptJson.exists() && scriptJson.load().has(location);
    }

    public static void convart(@NotNull ScriptKey scriptKey) {
        // YAML形式のファイルからデータを読み込むクラス
        var scriptLoader = new SBLoader(scriptKey);
        if (!scriptLoader.getFile().exists()) {
            return;
        }
        // JSONを作成
        var scriptJson = new BlockScriptJson(scriptKey);
        var blockScript = scriptJson.load();
        scriptLoader.forEach(s -> {
            // 移行の為、パラメータを設定する
            var author = s.getAuthors();
            if (author.size() == 0) {
                return;
            }
            var scriptParam = blockScript.get(s.getLocation());
            scriptParam.setAuthor(new LinkedHashSet<>(author));
            scriptParam.setScript(s.getScripts());
            scriptParam.setLastEdit(s.getLastEdit());
            scriptParam.setAmount(s.getAmount());
        });
        scriptJson.saveFile();

        // 移行完了後にファイルとディレクトリを削除する
        scriptLoader.getFile().delete();
        var parent = scriptLoader.getFile().getParentFile();
        if (parent.isDirectory() && parent.list().length == 0) {
            parent.delete();
        }
    }
}