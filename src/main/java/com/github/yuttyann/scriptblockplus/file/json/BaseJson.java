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
package com.github.yuttyann.scriptblockplus.file.json;

import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.file.SBFiles;
import com.github.yuttyann.scriptblockplus.file.json.annotation.Exclude;
import com.github.yuttyann.scriptblockplus.file.json.annotation.FieldExclusion;
import com.github.yuttyann.scriptblockplus.file.json.annotation.JsonTag;
import com.google.common.base.Charsets;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.*;

/**
 * ScriptBlockPlus BaseJson クラス
 * @param <T> シリアライズ化を行う値の型
 * @author yuttyann44581
 */
@SuppressWarnings("unchecked")
public abstract class BaseJson<T> {

    @Exclude
    private static final Map<Integer, List<?>> LIST_CACHE = new HashMap<>();

    @Exclude
    protected final File file;

    @SerializedName(value = "name", alternate = { "id" })
    protected final String name;

    @SerializedName(value = "elements", alternate = { "infos" })
    protected List<T> list = new ArrayList<>();

    /**
     * コンストラクタ
     * @param uuid - ファイルの名前
     */
    BaseJson(@NotNull UUID uuid) {
        this(uuid.toString());
    }

    /**
     * コンストラクタ
     * @param name - ファイルの名前
     */
    BaseJson(@NotNull String name) {
        this.name = name;
        this.file = getJsonFile();

        int hash = hashCode();
        if (LIST_CACHE.containsKey(hash)) {
            this.list = (List<T>) LIST_CACHE.get(hash);
        } else {
            try {
                Optional.ofNullable((BaseJson<T>) loadFile()).ifPresent(j -> list.addAll(j.list));
            } catch (IOException e) {
                e.printStackTrace();
            }
            LIST_CACHE.put(hash, list);
        }
    }

    /**
     * キャッシュされている要素をすべて削除します。
     */
    public static void clear() {
        LIST_CACHE.clear();
    }

    /**
     * 拡張子を除いたファイルの名前を取得します。
     * @return {@link String} - ファイルの名前
     */
    @NotNull
    public final String getName() {
        return name;
    }

    /**
     * ファイルを取得します。
     * @return {@link File} - ファイル
     */
    @NotNull
    public final File getFile() {
        return file;
    }

    /**
     * ファイルが存在するのか確認します。
     * @return {@link boolean} - ファイルが存在する場合はtrue
     */
    public final boolean exists() {
        return file.exists();
    }

    /**
     * キャッシュされた要素を含め、ファイルを削除します。
     */
    public final void deleteFile() {
        try {
            LIST_CACHE.remove(hashCode());
        } finally {
            file.delete();
        }
    }

    /**
     * 指定した要素を削除します。
     * @param value - 要素
     */
    public final void remove(@NotNull T value) {
        int hash = value.hashCode();
        if (list.removeIf(t -> t.hashCode() == hash)) {
            saveFile();
        }
    }

    /**
     * JSONのシリアライズを行います。
     */
    public final void saveFile() {
        var parent = file.getParentFile();
        if (!parent.exists()) {
            parent.mkdirs();
        }
        try {
            try (var writer = new JsonWriter(new OutputStreamWriter(new FileOutputStream(file), Charsets.UTF_8))) {
                writer.setIndent("  ");
                new GsonBuilder().setExclusionStrategies(new FieldExclusion()).create().toJson(this, getClass(), writer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * JSONのデシリアライズを行います。
     * @return {@link BaseJson} - JSON
     */
    @Nullable
    private BaseJson<?> loadFile() throws IOException {
        if (!file.exists()) {
            return null;
        }
        var parent = file.getParentFile();
        if (!parent.exists()) {
            parent.mkdirs();
        }
        try (var reader = new JsonReader(new InputStreamReader(new FileInputStream(file), Charsets.UTF_8))) {
            return new GsonBuilder().setExclusionStrategies(new FieldExclusion()).create().fromJson(reader, getClass());
        }
    }

    /**
     * JSONのファイルを取得します。
     * @throws NullPointerException {@link JsonTag}が見つからなかった時にスローされます。
     * @return {@link File} - JSONのファイル
     */
    @NotNull
    private File getJsonFile() {
        var jsonTag = getClass().getAnnotation(JsonTag.class);
        if (jsonTag == null) {
            throw new NullPointerException("Annotation not found @JsonTag()");
        }
        var path = jsonTag.path() + SBFiles.S + jsonTag.file();
        path = StringUtils.replace(path, "/", SBFiles.S);
        path = StringUtils.replace(path, "{id}", name);
        return new File(ScriptBlock.getInstance().getDataFolder(), path + ".json");
    }

    /**
     * フォルダ内の全てのファイルの名前を取得します。
     * @throws NullPointerException {@link JsonTag}が見つからなかった時にスローされます。
     * @return {@link String}[] - 全てのファイルの名前
     */
    @NotNull
    public static String[] getNames(@NotNull Class<? extends BaseJson<?>> jsonClass) {
        var jsonTag = jsonClass.getAnnotation(JsonTag.class);
        if (jsonTag == null) {
            throw new NullPointerException("Annotation not found @JsonTag()");
        }
        var folder = new File(ScriptBlock.getInstance().getDataFolder(), jsonTag.path());
        if (folder.exists()) {
            var files = folder.list();
            var names = new String[files.length];
            for (int i = 0; i < files.length; i++) {
                var fileName = files[i];
                var extension = fileName.lastIndexOf('.');
                if (extension > 0) {
                    names[i] = fileName.substring(0, extension);
                }
            }
            return names;
        }
        return ArrayUtils.EMPTY_STRING_ARRAY;
    }

    @Override
    public int hashCode() {
        return file.hashCode();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof BaseJson)) {
            return false;
        }
        var json = (BaseJson<?>) obj;
        return Objects.equals(name, json.name) && Objects.equals(list, json.list);
    }
}