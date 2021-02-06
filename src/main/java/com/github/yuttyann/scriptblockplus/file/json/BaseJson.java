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

import io.netty.util.collection.IntObjectHashMap;
import io.netty.util.collection.IntObjectMap;

import java.io.*;
import java.util.*;

/**
 * ScriptBlockPlus BaseJson クラス
 * @param <E> エレメントの型
 * @author yuttyann44581
 */
@SuppressWarnings("unchecked")
public abstract class BaseJson<E extends BaseElement> {

    /**
     * ScriptBlockPlus Status 列挙型
     * <p>
     * キャッシュの状態を表す列挙型
     * @author yuttyann44581
     */
    public enum Status {
        
        /**
         * キャッシュが生成されていない状態
         * <p>
         * デフォルトの状態です。
        */
        NO_CACHE,

        /**
         * キャッシュを保持している状態
         * <p>
         * 生成方法は{@link BaseJson#newJson(Object, CacheJson)}を参照してください。
        */
        KEEP_CACHE,

        /**
         * キャッシュが削除されている状態
         * <p>
         * この状態のインスタンスを保持し続けないでください。
        */
        CLEAR_CACHE,
    }

    @Exclude
    private static final IntObjectMap<BaseJson<?>> JSON_CACHE = new IntObjectHashMap<>();

    @Exclude
    private static final GsonBuilder GSON_BUILDER = new GsonBuilder();

    @Exclude
    private static final String INDENT = "  ";

    @Exclude
    private final JsonTag JSON_TAG = getClass().getAnnotation(JsonTag.class);

    @Exclude
    private File file;

    @Exclude
    private File parent;

    @Exclude
    private int cacheId;

    @Exclude
    private Status status;

    @SerializedName(value = "name", alternate = { "id" })
    protected String name;

    @SerializedName(value = "elements", alternate = { "infos" })
    protected List<E> list = Collections.emptyList();
    
    static {
        GSON_BUILDER.setPrettyPrinting();
        GSON_BUILDER.setExclusionStrategies(new FieldExclusion());
        GSON_BUILDER.registerTypeAdapter(BlockCoords.class, new BlockCoordsSerializer());
        GSON_BUILDER.registerTypeAdapter(BlockCoords.class, new BlockCoordsDeserializer());
        GSON_BUILDER.registerTypeAdapter(UnmodifiableBlockCoords.class, new BlockCoordsSerializer());
        GSON_BUILDER.registerTypeAdapter(UnmodifiableBlockCoords.class, new BlockCoordsDeserializer());
    }

    /**
     * コンストラクタ
     * <p>
     * 必ずシリアライズ、デシリアライズ化が可能なファイルを指定してください。
     * @param json - JSONのファイル
     */
    protected BaseJson(@NotNull File json) {
        if (JSON_TAG == null) {
            throw new NullPointerException("Annotation not found @JsonTag()");
        }
        var path = json.getPath();
        if (path.lastIndexOf(SBFile.setSeparator(JSON_TAG.path())) == -1) {
            throw new IllegalArgumentException("File: " + json.getPath() + ", JsonTag: " + JSON_TAG.path());
        }
        this.name = path.substring(path.lastIndexOf(File.separatorChar) + 1, path.lastIndexOf('.'));
        this.file = json;
        try {
            setList(createList(), loadFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
        noCache();
    }

    /**
     * コンストラクタ
     * @param name - ファイルの名前
     */
    protected BaseJson(@NotNull String name) {
        if (JSON_TAG == null) {
            throw new NullPointerException("Annotation not found @JsonTag()");
        }
        this.name = name;
        this.file = getJsonFile(JSON_TAG);
        try {
            setList(createList(), loadFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
        noCache();
    }

    /**
     * 新しいリストに要素を追加します。
     */
    private void setList(@NotNull List<E> newList, @NotNull BaseJson<E> baseJson) {
        if (baseJson != null) {
            var jsonList = baseJson.list;
            if (newList instanceof ArrayList) {
                newList = jsonList;
            } else {
                newList.addAll(jsonList);
            }
        }
        this.list = newList;
    }

    /**
     * {@link ArrayList}&lt;{@link E}&gt;を生成します。
     * @return {@link ArrayList}&lt;{@link E}&gt; - リスト
     */
    @NotNull
    protected List<E> createList() {
        return new ArrayList<>();
    }

    /**
     * 要素の再読み込みを行います。
     */
    public final void reload() {
        Objects.requireNonNull(file, "Please load the file");
        try {
            setList(createList(), loadFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        var hash = hash(argment, cacheJson.getJsonClass());
        var baseJson = JSON_CACHE.get(hash);
        if (baseJson == null) {
            baseJson = cacheJson.newInstance(argment);
            if (baseJson.isCacheFileExists() && !baseJson.exists()) {
                return (R) baseJson;
            }
            baseJson.keepCache();
            baseJson.setCacheId(hash);
            JSON_CACHE.put(hash, baseJson);
        }
        return (R) baseJson;
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
        JSON_CACHE.forEach((k, v) -> v.clearCache());
        JSON_CACHE.clear();
    }

    /**
     * キャッシュされた全ての要素を削除します。
     * @param jsonClass - JSONのクラス 
     */
    public static final void clear(@NotNull Class<? extends BaseJson<?>> jsonClass) {
       JSON_CACHE.entrySet().removeIf(e -> {
           if (e.getValue().getClass().equals(jsonClass)) {
                e.getValue().clearCache();
                return true;
           }
           return false;
        });
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
            this.parent = file.getParentFile();
        }
        return parent;
    }

    /**
     * キャッシュIDを設定します。
     * @param cacheId - キャッシュID
     */
    private void setCacheId(final int cacheId) {
        if (this.cacheId == 0) {
            this.cacheId = cacheId;
        }
    }

    /**
     * キャッシュIDを取得します。
     * @return {@link int} - キャッシュID
     */
    public int getCacheId() {
        return cacheId;
    }

    /**
     * キャッシュが生成されていない状態に設定します。
     * <p>
     * 値が設定されていない場合のみ設定される。
     */
    private void noCache() {
        if (getStatus() == null) {
            this.status = Status.NO_CACHE;
        }
    }

    /**
     * キャッシュを保持している状態に設定します。
     * <p>
     * キャッシュが生成されていない場合のみ設定される。
     */
    private void keepCache() {
        if (getStatus() == Status.NO_CACHE) {
            this.status = Status.KEEP_CACHE;
        }
    }

    /**
     * キャッシュが削除されている状態に設定します。
     * <p>
     * キャッシュが保存されている場合のみ設定される。
     */
    private void clearCache() {
        if (getStatus() == Status.KEEP_CACHE) {
            this.status = Status.CLEAR_CACHE;
        }
    }

    /**
     * キャッシュの状態を取得します。
     * @return {@link Status} - キャッシュの状態
     */
    public final Status getStatus() {
        return status;
    }

    /**
     * ファイルを保存した時にキャッシュを削除するのかどうか。
     * @return {@link boolean} - 削除を行う場合は{@code true}
     */
    protected boolean isTemporary() {
        return true;
    }

    /**
     * ファイルが存在する時のみキャッシュを保存するのかどうか。
     * @return {@link boolean} - ファイルが存在する時のみキャッシュを保存する場合は{@code true}
     */
    protected boolean isCacheFileExists() {
        return true;
    }

    /**
     * ファイルが存在するのか確認します。
     * @return {@link boolean} - ファイルが存在する場合は{@code true}
     */
    public final boolean exists() {
        return file.exists();
    }

    /**
     * キャッシュされた要素を含め、ファイルを削除します。
     * @return {@link boolean} - 削除に成功した場合は{@code true}
     */
    public final boolean deleteFile() {
        if (!exists()) {
            return false;
        }
        try {
            if (getStatus() == Status.KEEP_CACHE) {
                clearCache();
                JSON_CACHE.remove(getCacheId());
            }
        } finally {
            file.delete();
        }
        return true;
    }

    /**
     * JSONのシリアライズ化を行います。
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
            if (isTemporary() && getStatus() == Status.KEEP_CACHE) {
                clearCache();
                JSON_CACHE.remove(getCacheId());
            }
        }
    }

    /**
     * JSONのデシリアライズ化を行います。
     * @return {@link BaseJson} - JSON
     */
    @Nullable
    private BaseJson<E> loadFile() throws IOException {
        if (!exists()) {
            return null;
        }
        if (!getParentFile().exists()) {
            getParentFile().mkdirs();
        }
        try (var reader = new JsonReader(new InputStreamReader(new FileInputStream(file), Charsets.UTF_8))) {
            return (BaseJson<E>) GSON_BUILDER.create().fromJson(reader, getClass());
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
     * ハッシュコードを生成します。
     * @param argment - 引数
     * @param jsonClass - JSONのクラス
     * @return {@link int} - ハッシュコード
     */
    @NotNull
    private static int hash(@NotNull Object argment, @NotNull Class<?> jsonClass) {
        int hash = 1;
        int prime = 31;
        hash = prime * hash + Objects.hashCode(argment);
        hash = prime * hash + jsonClass.hashCode();
        return hash;
    }

    @Override
    public int hashCode() {
        int hash = 1;
        int prime = 31;
        hash = prime * hash + name.hashCode();
        hash = prime * hash + file.hashCode();
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
        var baseJson = (BaseJson<?>) obj;
        return Objects.equals(name, baseJson.name) && Objects.equals(file, baseJson.file);
    }
}