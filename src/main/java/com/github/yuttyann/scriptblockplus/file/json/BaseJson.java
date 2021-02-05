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

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.file.SBFile;
import com.github.yuttyann.scriptblockplus.file.json.annotation.Exclude;
import com.github.yuttyann.scriptblockplus.file.json.annotation.JsonTag;
import com.github.yuttyann.scriptblockplus.file.json.builder.BlockCoordsDeserializer;
import com.github.yuttyann.scriptblockplus.file.json.builder.BlockCoordsSerializer;
import com.github.yuttyann.scriptblockplus.file.json.builder.FieldExclusion;
import com.github.yuttyann.scriptblockplus.utils.StreamUtils;
import com.github.yuttyann.scriptblockplus.utils.unmodifiable.UnmodifiableBlockCoords;
import com.google.common.base.Charsets;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.*;

/**
 * ScriptBlockPlus BaseJson クラス
 * @param <E> エレメントの型
 * @author yuttyann44581
 */
@SuppressWarnings("unchecked")
public abstract class BaseJson<E extends BaseElement> {

    @Exclude
    private static final Map<Integer, BaseJson<?>> JSON_CACHE = new HashMap<>();

    @Exclude
    private static final GsonBuilder GSON_BUILDER = new GsonBuilder();

    @Exclude
    private static final String INDENT = "  ";

    @Exclude
    private final JsonTag jsonTag;

    @Exclude
    private File file;

    @Exclude
    private File parent;

    @Exclude
    private Integer hash;

    @Exclude
    private boolean cache;

    @SerializedName(value = "name", alternate = { "id" })
    protected String name;

    @SerializedName(value = "elements", alternate = { "infos" })
    protected List<E> list = Collections.emptyList();
    
    static {
        GSON_BUILDER.setExclusionStrategies(new FieldExclusion());
        GSON_BUILDER.registerTypeAdapter(BlockCoords.class, new BlockCoordsSerializer());
        GSON_BUILDER.registerTypeAdapter(BlockCoords.class, new BlockCoordsDeserializer());
        GSON_BUILDER.registerTypeAdapter(UnmodifiableBlockCoords.class, new BlockCoordsSerializer());
        GSON_BUILDER.registerTypeAdapter(UnmodifiableBlockCoords.class, new BlockCoordsDeserializer());
    }

    {
        this.jsonTag = getClass().getAnnotation(JsonTag.class);
        if (jsonTag == null) {
            throw new NullPointerException("Annotation not found @JsonTag()");
        }
    }

    /**
     * コンストラクタ
     * <p>
     * 必ずシリアライズ、デシリアライズ化が可能なファイルを指定してください。
     * @param json - JSONのファイル
     */
    protected BaseJson(@NotNull File json) {
        loadList(json);
    }

    /**
     * コンストラクタ
     * @param name - ファイルの名前
     */
    protected BaseJson(@NotNull String name) {
        loadList(name);
    }

    /**
     * JSONを取得します。
     * <p>
     * このメソッドは、キャッシュの保存を行います。
     * <p>
     * キャッシュが見つからない場合は、インスタンスの生成を行います。
     * @param <T> - 引数の型
     * @param <R> - JSONの型
     * @param argment - 引数
     * @param cacheJson - キャッシュ
     * @return {@link BaseJson}
     */
    @NotNull
    protected static <T, R extends BaseJson<?>> R newJson(@NotNull T argment, @NotNull CacheJson<T> cacheJson) {
        var hash = hashCode(argment, cacheJson.getJsonClass());
        var json = JSON_CACHE.get(hash);
        if (json == null) {
            json = cacheJson.newInstance(argment);
            json.hash = hash;
            json.cache = true;
            JSON_CACHE.put(hash, json);
        }
        return (R) json;
    }

    /**
     * {@link GsonBuilder}を取得します。
     * @return {@link GsonBuilder}
     */
    @NotNull
    protected static GsonBuilder getGsonBuilder() {
        return GSON_BUILDER;
    }

    /**
     * キャッシュされた全ての要素を削除します。
     */
    public static void clear() {
        JSON_CACHE.forEach((k, v) -> v.nonCache());
        JSON_CACHE.clear();
    }

    /**
     * キャッシュされた全ての要素を削除します。
     * @param jsonClass - JSONのクラス 
     */
    public static final void clear(@NotNull Class<? extends BaseJson<?>> jsonClass) {
       JSON_CACHE.entrySet().removeIf(e -> {
           if (e.getValue().getClass().equals(jsonClass)) {
                e.getValue().nonCache();
                return true;
           }
           return false;
        });
    }

    /**
     * 要素の再読み込みを行います。
     */
    public final void reload() {
        StreamUtils.ifAction(name != null, () -> loadList(name));
    }

    /**
     * 要素の読み込みを行います。
     * @param file - ファイル
     */
    private final void loadList(@NotNull File file) {
        var path = file.getPath();
        if (file.getPath().lastIndexOf(SBFile.setSeparator(jsonTag.path())) == -1) {
            throw new IllegalArgumentException("File: " + file.getPath() + ", JsonTag: " + jsonTag.path());
        }
        this.name = path.substring(path.lastIndexOf(File.separatorChar) + 1, path.lastIndexOf('.'));
        this.file = file;
        try {
            var json = loadFile();
            this.list = json == null ? new ArrayList<>() : (List<E>) loadFile().list;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 要素の読み込みを行います。
     * @param name - ファイル名
     */
    private final void loadList(@NotNull String name) {
        this.name = name;
        this.file = getJsonFile(jsonTag);
        try {
            var json = loadFile();
            this.list = json == null ? new ArrayList<>() : (List<E>) loadFile().list;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 拡張子を除いたファイルの名前を取得します。
     * @return {@link String} - ファイルの名前
     */
    @NotNull
    public final String getName() {
        return Objects.requireNonNull(name, "Please load the file");
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
     * 親ディレクトリを取得します。
     * @return {@link File} - 親ディレクトリ
     */
    @NotNull
    public final File getParentFile() {
        Validate.notNull(file, "Please load the file");
        if (parent == null) {
            parent = file.getParentFile();
        }
        return parent;
    }

    /**
     * ファイルが存在するのか確認します。
     * @return {@link boolean} - ファイルが存在する場合は{@code true}
     */
    public final boolean exists() {
        return file.exists();
    }

    /**
     * キャッシュが存在するのかどうか。
     * @return {@link boolean} - 削除を行う場合は{@code true}
     */
    public final boolean isCache() {
        return cache;
    }

    /**
     * ファイルを保存した時にキャッシュを削除するのかどうか。
     * @return {@link boolean} - 削除を行う場合は{@code true}
     */
    protected boolean isTemporary() {
        return true;
    }

    /**
     * キャッシュの判定をオフにします。
     */
    private void nonCache() {
        this.cache = false;
    }

    /**
     * キャッシュされた要素を含め、ファイルを削除します。
     */
    public final void deleteFile() {
        try {
            if (isCache() && hash != null) {
                JSON_CACHE.remove(hash);
                nonCache();
            }
        } finally {
            file.delete();
        }
    }

    /**
     * JSONのシリアライズを行います。
     */
    public final void saveFile() {
        if (!getParentFile().exists()) {
            getParentFile().mkdirs();
        }
        try {
            try (var writer = new JsonWriter(new OutputStreamWriter(new FileOutputStream(file), Charsets.UTF_8))) {
                writer.setIndent(INDENT);
                GSON_BUILDER.create().toJson(this, getClass(), writer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (isTemporary() && isCache() && hash != null) {
                JSON_CACHE.remove(hash);
                nonCache();
            }
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
        if (!getParentFile().exists()) {
            getParentFile().mkdirs();
        }
        try (var reader = new JsonReader(new InputStreamReader(new FileInputStream(file), Charsets.UTF_8))) {
            return GSON_BUILDER.create().fromJson(reader, getClass());
        }
    }

    /**
     * JSONのファイルを取得します。
     * @throws NullPointerException {@link JsonTag}が見つからなかった時にスローされます。
     * @param jsonTag - JSONタグ
     * @return {@link File} - JSONのファイル
     */
    @NotNull
    private File getJsonFile(@NotNull JsonTag jsonTag) {
        if (file == null) {
            var path = StringUtils.replace(jsonTag.path() + "/" + jsonTag.file(), "{id}", name);
            var plugin = Bukkit.getPluginManager().getPlugin(jsonTag.plugin());
            return new SBFile(plugin.getDataFolder(), path + ".json");
        }
        return file;
    }

    /**
     * フォルダ内の全てのファイルを取得します。
     * <p>
     * また、ファイルの名前の取得に成功した場合は関連する全てのキャッシュを削除します。
     * @throws NullPointerException {@link JsonTag}が見つからなかった時にスローされます。
     * @param jsonClass - {@link BaseJson}を継承したクラス
     * @return {@link File}[] - 全てのファイル
     */
    @NotNull
    public static File[] getFiles(@NotNull Class<? extends BaseJson<?>> jsonClass) {
        var jsonTag = jsonClass.getAnnotation(JsonTag.class);
        if (jsonTag == null) {
            throw new NullPointerException("Annotation not found @JsonTag()");
        }
        var plugin = Bukkit.getPluginManager().getPlugin(jsonTag.plugin());
        var folder = new SBFile(plugin.getDataFolder(), jsonTag.path());
        if (folder.exists()) {
            try {
                return folder.listFiles(f -> f.getName().endsWith(".json"));
            } finally {
                clear(jsonClass);
            }
        }
        return new File[0];
    }

    /**
     * キャッシュデータのハッシュコードを取得します。
     * @return {@link Integer} - ハッシュコード
     */
    @Nullable
    private Integer hash() {
        return hash;
    }

    @Override
    public int hashCode() {
        return hashCode(name, getClass());
    }

    private static int hashCode(@NotNull Object obj, @NotNull Class<?> jsonClass) {
        int hash = 1;
        int prime = 31;
        hash = prime * hash + obj.hashCode();
        hash = prime * hash + jsonClass.hashCode();
        return hash;
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
        return Objects.equals(name, json.name) && Objects.equals(file, json.file);
    }
}