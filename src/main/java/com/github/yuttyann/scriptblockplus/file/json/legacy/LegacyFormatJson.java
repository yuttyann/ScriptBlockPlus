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
package com.github.yuttyann.scriptblockplus.file.json.legacy;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.file.json.BaseJson;
import com.github.yuttyann.scriptblockplus.file.json.element.BlockScript;
import com.github.yuttyann.scriptblockplus.utils.FileUtils;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.github.yuttyann.scriptblockplus.file.json.BaseJson.GSON_HOLDER;

/**
 * ScriptBlockPlus LegacyFormatJson クラス
 * @author yuttyann44581
 */
public final class LegacyFormatJson {

    public static final String[] LEGACY_KEYS = { "elements", "infos", "scripts" };

    private LegacyFormatJson() { }

    /**
     * JSONの要素から{@code elements}または{@code infos}を取り除きます。
     * @param convartList - コンバートリスト
     * @return {@link boolean} - コンバートに成功した場合は{@code true}
     */
    public static boolean convart(@Nullable ConvartList convartList) {
        if (convartList == null) {
            return false;
        }
        var paths = convartList.getConvartPaths();
        if (paths.isEmpty()) {
            return false;
        }
        var result = false;
        var legacyJson = new LegacyFormatJson();
        try {
            for (var path : paths) {
                if (legacyJson.removeElements(new File(path))) {
                    result = true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (result) {
                convartList.saveFormatVersion();
            }
        }
        return result;
    }

    /**
     * JSONの要素から{@code elements}または{@code infos}を取り除きます。
     * @param json - JSONのファイル
     */
    private boolean removeElements(@NotNull File json) throws IOException {
        if (!json.exists()) {
            return false;
        }
        var map = loadFile(json);
        var elements = castList(map.get(LEGACY_KEYS[0]));
        if (elements == null) {
            elements = castList(map.get(LEGACY_KEYS[1]));
        }
        if (elements == null || elements.isEmpty()) {
            return false;
        }
        var element = elements.get(0);
        if (element instanceof Map) {
            var scripts = castMap(element).get(LEGACY_KEYS[2]);
            if (scripts instanceof Map) {
                var list = new ArrayList<BlockScript>(elements.size() + 1);
                for (var entry : castMap(scripts).entrySet()) {
                    list.add(createBlockScript(entry));
                }
                saveFile(json, list);
                return true;
            }
        }
        saveFile(json, elements);
        return true;
    }

    /**
     * {@link BlockScript}を生成します。
     * @param entry - エントリ
     * @return {@link BlockScript} - ブロックスクリプト
     */
    @NotNull
    private BlockScript createBlockScript(@NotNull Entry<String, Object> entry) {
        var blockScript = new BlockScript(BlockCoords.fromString(entry.getKey()));
        for (var scriptEntry : castMap(entry.getValue()).entrySet()) {
            var value = scriptEntry.getValue();
            switch (scriptEntry.getKey()) {
                case "author":
                    blockScript.setAuthors(castList(value));
                    break;
                case "script":
                    blockScript.setScripts(castList(value));
                    break;
                case "lastedit":
                    blockScript.setLastEdit((String) value);   
                    break;
                case "selector":
                    blockScript.setSelector((String) value);
                    break;
                case "amount":
                    blockScript.setAmount(((Number) value).intValue());
                    break;
            }
        }
        return blockScript;
    }

    /**
     * JSONのシリアライズ化を行います。
     * @param json - JSONのファイル
     * @param list - リスト
     */
    private void saveFile(@NotNull File json, @NotNull List<?> list) throws IOException {
        try (var writer = new JsonWriter(FileUtils.newBufferedWriter(json))) {
            writer.setIndent(BaseJson.INDENT);
            GSON_HOLDER.getGson().toJson(list, list.getClass(), writer);
        }
    }

    /**
     * JSONのデシリアライズ化を行います。
     * @param json - JSONのファイル
     * @return {@link Map}&lt;{@link String}, {@link Object}&gt; - マップ
     */
    @Nullable
    private Map<String, Object> loadFile(@NotNull File json) throws IOException {
        try (var reader = new JsonReader(FileUtils.newBufferedReader(json))) {
            return castMap(GSON_HOLDER.getGson().fromJson(reader, Map.class));
        }
    }

    /**
     * オブジェクトを{@link List}へキャストします。
     * @param <E> 要素の型
     * @param object - オブジェクト
     * @return {@link List}&lt;{@link E}&gt; - リスト
     */
    @SuppressWarnings("unchecked")
    private <E> List<E> castList(@NotNull Object object) {
        return (List<E>) object;
    }

    /**
     * オブジェクトを{@link Map}へキャストします。
     * @param object - オブジェクト
     * @return {@link Map}&lt;{@link String}, {@link Object}&gt; - マップ
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> castMap(@NotNull Object object) {
        return (Map<String, Object>) object;
    }
}