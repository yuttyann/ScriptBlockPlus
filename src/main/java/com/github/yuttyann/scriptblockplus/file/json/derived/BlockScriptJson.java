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

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.file.SBLoader;
import com.github.yuttyann.scriptblockplus.file.json.CacheJson;
import com.github.yuttyann.scriptblockplus.file.json.annotation.JsonTag;
import com.github.yuttyann.scriptblockplus.file.json.basic.OneJson;
import com.github.yuttyann.scriptblockplus.file.json.element.BlockScript;
import com.github.yuttyann.scriptblockplus.script.ScriptKey;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashSet;

/**
 * ScriptBlockPlus BlockScriptJson クラス
 * @author yuttyann44581
 */
@JsonTag(path = "json/blockscript")
public class BlockScriptJson extends OneJson<BlockCoords, BlockScript> {

    private static final CacheJson<ScriptKey> CACHE_JSON = new CacheJson<>(BlockScriptJson.class, BlockScriptJson::new);
    
    protected BlockScriptJson(@NotNull ScriptKey scriptKey) {
        super(scriptKey.getName());
    }

    @Override
    protected boolean isCacheFileExists() {
        return false;
    }

    @NotNull
    public ScriptKey getScriptKey() {
        return ScriptKey.valueOf(getName());
    }

    @Override
    @NotNull
    protected BlockScript newInstance(BlockCoords blockCoords) {
        return new BlockScript(blockCoords);
    }

    @NotNull
    public static BlockScriptJson get(@NotNull ScriptKey scriptKey) {
        return newJson(scriptKey, CACHE_JSON);
    }

    public static boolean contains(@NotNull BlockCoords blockCoords) {
        for (var scriptKey : ScriptKey.iterable()) {
            if (contains(blockCoords, get(scriptKey))) {
                return true;
            }
        }
        return false;
    }

    public static boolean contains(@NotNull ScriptKey scriptKey, @NotNull BlockCoords blockCoords) {
        return contains(blockCoords, get(scriptKey));
    }

    public static boolean contains(@NotNull BlockCoords blockCoords, @NotNull BlockScriptJson scriptJson) {
        return scriptJson.has(blockCoords);
    }

    public static void convart(@NotNull ScriptKey scriptKey) {
        // YAML形式のファイルからデータを読み込むクラス
        var scriptLoader = new SBLoader(scriptKey);
        if (!scriptLoader.getFile().exists()) {
            return;
        }
        // JSONを作成
        var scriptJson = get(scriptKey);
        scriptLoader.forEach(s -> {
            // 移行の為、パラメータを設定する
            var author = s.getAuthors();
            if (author.size() == 0) {
                return;
            }
            var blockScript = scriptJson.load(s.getBlockCoords());
            blockScript.setAuthors(new LinkedHashSet<>(author));
            blockScript.setScripts(s.getScripts());
            blockScript.setLastEdit(s.getLastEdit());
            blockScript.setAmount(s.getAmount());
        });
        scriptJson.saveJson();

        // 移行完了後にファイルとディレクトリを削除する
        scriptLoader.getFile().delete();
        var parent = scriptLoader.getFile().getParentFile();
        if (parent.isDirectory() && parent.list().length == 0) {
            parent.delete();
        }
    }
}