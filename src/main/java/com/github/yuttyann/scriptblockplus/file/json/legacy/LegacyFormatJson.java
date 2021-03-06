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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.file.config.SBConfig;
import com.github.yuttyann.scriptblockplus.file.json.BaseJson;
import com.github.yuttyann.scriptblockplus.file.json.element.BlockScript;
import com.github.yuttyann.scriptblockplus.file.json.element.PlayerTimer;
import com.github.yuttyann.scriptblockplus.script.ScriptKey;
import com.github.yuttyann.scriptblockplus.utils.FileUtils;
import com.google.gson.stream.JsonWriter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import static com.github.yuttyann.scriptblockplus.file.json.BaseJson.GSON_HOLDER;

/**
 * ScriptBlockPlus LegacyFormatJson クラス
 * @author yuttyann44581
 */
public final class LegacyFormatJson {

    public static final String[] LEGACY_KEYS = { "elements", "infos", "scripts", "timer" };

    private LegacyFormatJson() { }

    /**
     * Jsonの要素から{@code elements}または{@code infos}を取り除きます。
     * @param convertList - コンバートリスト
     * @return {@code boolean} - コンバートに成功した場合は{@code true}
     */
    public static boolean convert(@Nullable ConvertList convertList) {
        if (convertList == null) {
            return false;
        }
        var paths = convertList.getConvertPaths();
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
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Jsonの要素から{@code elements}または{@code infos}を取り除きます。
     * @throws IOException I/O系の例外が発生した場合にスローされます。
     * @throws ParseException Jsonのパース時に例外が発生した場合にスローされます。
     * @param json - Jsonのファイル
     */
    private boolean removeElements(@NotNull File json) throws IOException, ParseException {
        if (!json.exists()) {
            return false;
        }
        var elements = getElements(json);
        if (!(elements instanceof JSONArray)) {
            return false;
        }
        var array = loadFile(((JSONArray) elements).toJSONString());
        if (array == null) {
            return false;
        }
        if (array.isEmpty()) {
            saveFile(json, Collections.emptyList());
            return true;
        }
        var element = array.get(0);
        if (element instanceof Map) {
            var scripts = castMap(element).get(LEGACY_KEYS[2]);
            if (scripts instanceof Map) {
                var list = new ArrayList<BlockScript>(array.size());
                for (var entry : castMap(scripts).entrySet()) {
                    list.add(createBlockScript(entry));
                }
                saveFile(json, list);
                return true;
            }
            var timer = castMap(element).get(LEGACY_KEYS[3]);
            if (timer instanceof List) {
                var list = new ArrayList<PlayerTimer>(array.size());
                for (var temp : castList(timer)) {
                    list.add(createPlayerTimer(castMap(temp).entrySet()));
                }
                saveFile(json, list);
                return true;
            }
        }
        saveFile(json, array);
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
            if (value == null) {
                continue;
            }
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
     * {@link PlayerTimer}を生成します。
     * @param set - エントリ
     * @return {@link PlayerTimer} - ブロックスクリプト
     */
    @NotNull
    @SuppressWarnings("unchecked")
    private PlayerTimer createPlayerTimer(@NotNull Set<Entry<String, Object>> entries) {
        var time = (long[]) null;
        var uuid = (UUID) null;
        var scriptKey = (ScriptKey) null;
        var blockCoords = (BlockCoords) null;
        for (var entry : entries) {
            var value = entry.getValue();
            if (value == null) {
                continue;
            }
            switch (entry.getKey()) {
                case "params":
                    time = toLongArray((List<Long>) value);
                    break;
                case "uuid":
                    uuid = UUID.fromString((String) value);
                    break;
                case "scripttype":
                case "scriptType":
                case "scriptkey":
                    scriptKey = toScriptKey(value);
                    break;
                case "fullcoords":
                case "fullCoords":
                case "blockcoords":
                    blockCoords = BlockCoords.fromString((String) value);
                    break;
            }
        }
        var playerTimer = new PlayerTimer(uuid, scriptKey, blockCoords);
        playerTimer.setTime(time);
        return playerTimer;
    }

    /**
     * Jsonのシリアライズ化を行います。
     * @throws IOException I/O系の例外が発生した場合にスローされます。
     * @param json - Jsonのファイル
     * @param list - リスト
     */
    private void saveFile(@NotNull File json, @NotNull List<?> list) throws IOException {
        try (var writer = new JsonWriter(FileUtils.newBufferedWriter(json))) {
            if (list.size() < SBConfig.FORMAT_LIMIT.getValue()) {
                writer.setIndent(BaseJson.INDENT);
            }
            GSON_HOLDER.getGson().toJson(list, list.getClass(), writer);
        }
    }

    /**
     * Jsonのデシリアライズ化を行います。
     * @throws IOException I/O系の例外が発生した場合にスローされます。
     * @param json - Json
     * @return {@link Map}&lt;{@link String}, {@link Object}&gt; - マップ
     */
    @Nullable
    private List<Object> loadFile(@NotNull String json) throws IOException {
        return castList(GSON_HOLDER.getGson().fromJson(json, List.class));
    }

    /**
     * Jsonの要素を取得します。
     * @throws IOException I/O系の例外が発生した場合にスローされます。
     * @param json - Jsonのファイル
     * @throws ParseException Jsonのパース時に例外が発生した場合にスローされます。
     * @return {@link Object} - オブジェクト
     */
    @Nullable
    private Object getElements(@NotNull File json) throws IOException, ParseException {
        try (var reader = FileUtils.newBufferedReader(json)) {
            var parse = new JSONParser().parse(reader);
            if (!(parse instanceof JSONObject)) {
                return null;
            }
            var jsonObject = (JSONObject) parse;
            var elements = jsonObject.get(LEGACY_KEYS[0]);
            if (elements == null) {
                elements = jsonObject.get(LEGACY_KEYS[1]);
            }
            return elements;
        }
    }

    /**
     * {@link long}の配列を生成します。
     * @param list - {@link Long}のリスト
     * @return {@link long}[] - {@link long}の配列
     */
    @Nullable
    private long[] toLongArray(@Nullable List<Long> list) {
        var newArray = new long[3];
        for (int i = 0; i < 3; i++) {
            newArray[i] = list.get(i);
        }
        return newArray;
    }

    /**
     * {@link ScriptKey}を生成します。
     * @param value - 値
     * @return {@link ScriptKey} - スクリプトキー
     */
    @Nullable
    private ScriptKey toScriptKey(@Nullable Object value) {
        if (value instanceof Map) {
            return ScriptKey.valueOf((String) castMap(value).get("name"));
        }
        return ScriptKey.valueOf((String) value);
    }

    /**
     * オブジェクトを{@link List}へキャストします。
     * @param <E> 要素の型
     * @param object - オブジェクト
     * @return {@link List}&lt;{@link E}&gt; - リスト
     */
    @Nullable
    @SuppressWarnings("unchecked")
    private <E> List<E> castList(@Nullable Object object) {
        return (List<E>) object;
    }

    /**
     * オブジェクトを{@link Map}へキャストします。
     * @param object - オブジェクト
     * @return {@link Map}&lt;{@link String}, {@link Object}&gt; - マップ
     */
    @Nullable
    @SuppressWarnings("unchecked")
    private Map<String, Object> castMap(@Nullable Object object) {
        return (Map<String, Object>) object;
    }
}