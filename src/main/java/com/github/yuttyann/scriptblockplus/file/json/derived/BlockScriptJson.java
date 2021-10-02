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
import com.github.yuttyann.scriptblockplus.item.action.TickRunnable;
import com.github.yuttyann.scriptblockplus.script.ScriptKey;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * ScriptBlockPlus BlockScriptJson クラス
 * @author yuttyann44581
 */
@JsonTag(path = "json/blockscript", cachefileexists = false)
public final class BlockScriptJson extends OneJson<BlockCoords, BlockScript> {

    public static final List<BiConsumer<ScriptKey, BlockCoords[]>> INIT_PROCESS = new ArrayList<>();

    static {
        CacheJson.register(BlockScriptJson.class);
        CacheJson.register(PlayerCountJson.class);
        CacheJson.register(PlayerTimerJson.class);
        INIT_PROCESS.add((s, b) -> PlayerCountJson.removeAll(s, b));
        INIT_PROCESS.add((s, b) -> PlayerTimerJson.removeAll(s, b));
        INIT_PROCESS.add((s, b) -> {
            try {
                for (var blockCoords : b) {
                    TickRunnable.GLOW_ENTITY.broadcastDestroy(blockCoords);
                }
            } catch (ReflectiveOperationException e) {
                e.printStackTrace();
            }
        });
    }

    private final ScriptKey scriptKey;

    private BlockScriptJson(@NotNull String name) {
        super(name);
        this.scriptKey = ScriptKey.valueOf(name);
    }

    public void init(@NotNull BlockCoords... blockCoords) {
        if (blockCoords.length > 0) {
            var scriptKey = getScriptKey();
            INIT_PROCESS.forEach(a -> a.accept(scriptKey, blockCoords));
        }
    }

    @NotNull
    public ScriptKey getScriptKey() {
        return scriptKey;
    }

    @Override
    @NotNull
    protected BlockScript newInstance(@NotNull BlockCoords blockCoords) {
        return new BlockScript(blockCoords);
    }

    @NotNull
    public static BlockScriptJson newJson(@NotNull ScriptKey scriptKey) {
        return newJson(BlockScriptJson.class, scriptKey.getName());
    }

    public static boolean contains(@NotNull BlockCoords blockCoords) {
        for (var scriptKey : ScriptKey.iterable()) {
            if (newJson(scriptKey).has(blockCoords)) {
                return true;
            }
        }
        return false;
    }

    public static void convert(@NotNull ScriptKey scriptKey) {
        // YAML形式のファイルからデータを読み込むクラス
        var scriptLoader = new SBLoader(scriptKey);
        if (!scriptLoader.getFile().exists()) {
            return;
        }
        // JSONを作成
        var scriptJson = newJson(scriptKey);
        if (scriptJson.exists()) {
            return;
        }
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