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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.file.SBFile;
import com.github.yuttyann.scriptblockplus.utils.FileUtils;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * ScriptBlockPlus ConvertList クラス
 * @author yuttyann44581
 */
public final class ConvertList {

    private static final String FORMAT_FILE = "format.sbp";

    static {
        var folder = ScriptBlock.getInstance().getDataFolder();
        FileUtils.move(new SBFile(folder, "json/playertemp"), new SBFile(folder, "json/playertimer"));
    }

    private final File folder;
    private final File format;
    private final JSONParser parser;

    private List<String> convertPaths;
    private FormatVersion formatVersion;

    /**
     * コンストラクタ
     * @param plugin - プラグイン
     * @param folder - フォルダの名前(例: json)
     */
    private ConvertList(@NotNull Plugin plugin, @NotNull String folder) {
        this.folder = new SBFile(plugin.getDataFolder(), folder);
        this.format = new SBFile(this.folder, FORMAT_FILE);
        this.parser = new JSONParser();
    }

    /**
     * {@link ConvertList}を作成します。
     * @param plugin - プラグイン
     * @param folder - フォルダの名前(例: json)
     * @return {@link ConvertList} - コンバートリスト
     */
    @Nullable
    public static ConvertList create(@NotNull Plugin plugin, @NotNull String folder) {
        var convertList = new ConvertList(plugin, folder);
        if (!convertList.getFolder().exists()) {
            convertList.saveFormatVersion();
            return null;
        }
        if (convertList.getFormatVersion().getVersion() >= FormatVersion.CURRENT.getVersion()) {
            return null;
        }
        try {
            convertList.searchFiles();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        convertList.saveFormatVersion();
        return convertList.getConvertPaths().isEmpty() ? null : convertList;
    }

    /**
     * フォーマットバージョンを保存します。
     */
    void saveFormatVersion() {
        var formatMap = getFormatVersionMap();
        formatMap.put(format.getPath(), new FormatVersion());
        FileUtils.saveFile(format, formatMap);
    }

    /**
     * フォルダを取得します。
     * @return {@link File} - フォルダ
     */
    @NotNull
    public File getFolder() {
        return folder;
    }

    /**
     * フォーマットバージョンを取得します。
     * @return {@link FormatVersion} - フォーマットバージョン
     */
    @NotNull
    public FormatVersion getFormatVersion() {
        var formatMap = getFormatVersionMap();
        if (formatMap.containsKey(format.getPath())) {
            return formatVersion == null ? this.formatVersion = formatMap.get(format.getPath()) : formatVersion;
        }
        return FormatVersion.ZERO;
    }

    /**
     * フォーマットバージョンのマップを取得します。
     * @return {@link Map}&lt;{@link String}, {@link FormatVersion}&gt; - フォーマットバージョンのマップ
     */
    @NotNull
    private Map<String, FormatVersion> getFormatVersionMap() {
        if (!format.exists()) {
            return new HashMap<String, FormatVersion>();
        }
        Map<String, FormatVersion> formatMap = FileUtils.loadFile(format);
        return formatMap == null ? new HashMap<String, FormatVersion>() : formatMap;
    }

    /**
     * 検索を行った、JSONのファイルパスのリストを取得します。
     * @return {@link List}&lt;{@link String}&gt; - ファイルパスのリスト
     */
    @NotNull
    public List<String> getConvertPaths() {
        return convertPaths == null ? Collections.emptyList() : convertPaths;
    }

    /**
     * JSONの検索を行います。
     * @throws IOException I/O系の例外が発生した場合にスローされます。
     * @throws ParseException JSONの読み込みに失敗した場合にスローされます。
     */
    public void searchFiles() throws IOException, ParseException {
        if (folder.exists()) {
            this.convertPaths = new ArrayList<>(32);
            searchFiles(folder);
        } else {
            this.convertPaths = null;
        }
    }

    /**
     * JSONの検索を行います。
     * @throws IOException I/O系の例外が発生した場合にスローされます。
     * @throws ParseException JSONの読み込みに失敗した場合にスローされます。
     * @param folder - 検索を行うフォルダ
     */
    private void searchFiles(@NotNull File folder) throws IOException, ParseException {
        var files = folder.listFiles();
        if (files == null || files.length == 0) {
            return;
        }
        for (var file : files) {
            if (file.isDirectory()) {
                searchFiles(file);
                continue;
            }
            var path = file.getPath();
            if (path.endsWith(".json") && isLegacy(file)) {
                convertPaths.add(path);
            }
        }
    }

    /**
     * 古い形式のJSONなのか判定します。
     * @throws IOException I/O系の例外が発生した場合にスローされます。
     * @throws ParseException JSONの読み込みに失敗した場合にスローされます。
     * @param json - JSONのファイル
     * @return {@code boolean} - 古い形式の場合は{@code true}
     */
    private boolean isLegacy(@NotNull File json) throws IOException, ParseException {
        /**
         * 今後新たに実装する場合はバージョンごとに処理を分けるようにする。
         * if (getFormatVersion().getVersion() != 2.0) {
         *     return false;
         * }
        */
        try (var reader = FileUtils.newBufferedReader(json)) {
            var parse = parser.parse(reader);
            if (!(parse instanceof JSONObject)) {
                return false;
            }
            var jsonObject = (JSONObject) parse;
            return jsonObject.containsKey(LegacyFormatJson.LEGACY_KEYS[0]) || jsonObject.containsKey(LegacyFormatJson.LEGACY_KEYS[1]);
        }
    }
}