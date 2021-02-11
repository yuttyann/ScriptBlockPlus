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
import com.github.yuttyann.scriptblockplus.file.config.SBConfig;
import com.github.yuttyann.scriptblockplus.file.json.annotation.JsonTag;
import com.github.yuttyann.scriptblockplus.file.json.builder.BlockCoordsDeserializer;
import com.github.yuttyann.scriptblockplus.file.json.builder.BlockCoordsSerializer;
import com.github.yuttyann.scriptblockplus.file.json.builder.FieldExclusion;
import com.github.yuttyann.scriptblockplus.file.json.builder.NumberAdapter;
import com.github.yuttyann.scriptblockplus.file.json.builder.ScriptKeyDeserializer;
import com.github.yuttyann.scriptblockplus.file.json.builder.ScriptKeySerializer;
import com.github.yuttyann.scriptblockplus.script.ScriptKey;
import com.github.yuttyann.scriptblockplus.utils.collection.IntMap;
import com.github.yuttyann.scriptblockplus.utils.FileUtils;
import com.github.yuttyann.scriptblockplus.utils.collection.IntHashMap;
import com.github.yuttyann.scriptblockplus.utils.unmodifiable.UnmodifiableBlockCoords;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.util.*;

/**
 * ScriptBlockPlus BaseJson クラス
 * @param <E> エレメントの型
 * @author yuttyann44581
 */
@SuppressWarnings("unchecked")
public abstract class BaseJson<E extends BaseElement> extends SubElementMap<E> {

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

    public static final String INDENT = "  ";
    public static final GsonHolder GSON_HOLDER = new GsonHolder(new GsonBuilder());

    private static final IntMap<BaseJson<?>> JSON_CACHE = IntHashMap.create();

    private final File file;
    private final String name;
    private final JsonTag jsonTag;

    private int id;
    private File parent;
    private Status status;
    private Class<E[]> arrayClass;

    protected IntMap<E> elementMap;

    // GsonBuilderにアダプター等の追加を行う。
    static {
        GSON_HOLDER.builder(b -> {
            b.serializeNulls();
            b.setPrettyPrinting();
            b.setExclusionStrategies(new FieldExclusion());
            b.registerTypeAdapter(Map.class, new NumberAdapter());
            b.registerTypeAdapter(List.class, new NumberAdapter());
            b.registerTypeAdapter(ScriptKey.class, new ScriptKeySerializer());
            b.registerTypeAdapter(ScriptKey.class, new ScriptKeyDeserializer());
            b.registerTypeAdapter(BlockCoords.class, new BlockCoordsSerializer());
            b.registerTypeAdapter(BlockCoords.class, new BlockCoordsDeserializer());
            b.registerTypeAdapter(UnmodifiableBlockCoords.class, new BlockCoordsSerializer());
            b.registerTypeAdapter(UnmodifiableBlockCoords.class, new BlockCoordsDeserializer());
        });
    }

    // JsonTagを取得する。
    {
        if ((this.jsonTag = getClass().getAnnotation(JsonTag.class)) == null) {
            throw new NullPointerException("Annotation not found @JsonTag()");
        }
    }

    /**
     * コンストラクタ
     * <p>
     * 必ずシリアライズとデシリアライズ化が可能なファイルを指定してください。
     * @param json - JSONのファイル
     */
    protected BaseJson(@NotNull File json) {
        var path = json.getPath();
        if (path.lastIndexOf(SBFile.setSeparator(jsonTag.path())) == -1) {
            throw new IllegalArgumentException("File: " + json.getPath() + ", JsonTag: " + jsonTag.path());
        }
        this.file = json;
        this.name = path.substring(path.lastIndexOf(File.separatorChar) + 1, path.lastIndexOf('.'));
        try {
            setMap(loadJson());
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
        this.file = getJsonFile(name);
        this.name = name;
        try {
            setMap(loadJson());
        } catch (IOException e) {
            e.printStackTrace();
        }
        noCache();
    }

    /**
     * マップに要素を追加します。
     * @param elements - エレメントの配列
     */
    private void setMap(@Nullable E[] elements) {
        if (elements == null || elements.length == 0) {
            this.elementMap = createMap(IntHashMap.DEFAULT_CAPACITY);
        } else {
            int size = elements.length;
            var newMap = createMap(size);
            if (size == 1) {
                var element = elements[0];
                newMap.put(element.hashCode(), element);
            } else {
                for (int i = 0; i < size; i++) {
                    var element = elements[i];
                    int hashCode = element.hashCode();
                    if (newMap.containsKey(hashCode)) {
                        subPut(hashCode, element);
                    } else {
                        newMap.put(hashCode, element);
                    }
                }
            }
            this.elementMap = newMap;
        }
    }

    /**
     * {@link IntMap}&lt;{@link E}&gt;を生成します。
     * @param size - マップサイズ
     * @return {@link IntMap}&lt;{@link E}&gt; - マップ
     */
    @NotNull
    protected IntMap<E> createMap(int size) {
        return IntHashMap.create(size);
    }

    /**
     * 要素の再読み込みを行います。
     */
    public final void reload() {
        Objects.requireNonNull(file, "Please load the file");
        try {
            setMap(loadJson());
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
     * キャッシュされた全ての要素を削除します。
     */
    public static void clear() {
        JSON_CACHE.iterable().forEach(e -> e.value().clearCache());
        JSON_CACHE.clear();
    }

    /**
     * キャッシュされた全ての要素を削除します。
     * @param jsonClass - JSONのクラス 
     */
    public static final void clear(@NotNull Class<? extends BaseJson<?>> jsonClass) {
        JSON_CACHE.removeIf(e -> {
            if (e.value().getClass().equals(jsonClass)) {
                e.value().clearCache();
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
     * 親ディレクトリを取得します。
     * @return {@link File} - 親ディレクトリ
     */
    @NotNull
    public final File getParentFile() {
        return parent == null ? parent = file.getParentFile() : parent;
    }

    /**
     * キャッシュIDを設定します。
     * @param id - キャッシュID
     */
    private void setCacheId(final int id) {
        if (this.id == 0) {
            this.id = id;
        }
    }

    /**
     * キャッシュIDを取得します。
     * @return {@link int} - キャッシュID
     */
    public int getCacheId() {
        return id;
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
        return false;
    }

    /**
     * ファイルが存在する時のみキャッシュを保存するのかどうか。
     * @return {@link boolean} - ファイルが存在する時のみキャッシュを保存する場合は{@code true}
     */
    protected boolean isCacheFileExists() {
        return true;
    }

    /**
     * リストに要素が存在しない場合に{@code true}を返します。
     * @return {@link boolean} - 要素が存在しない場合は{@code true}
     */
    public final boolean isEmpty() {
        return elementMap.isEmpty();
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
        if (!file.exists()) {
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
    public final void saveJson() {
        var parent = getParentFile();
        if (!parent.exists()) {
            parent.mkdirs();
        }
        var elements = copyElements();
        try (var writer = new JsonWriter(FileUtils.newBufferedWriter(file))) {
            if (elementMap.size() < SBConfig.FORMAT_LIMIT.getValue()) {
                writer.setIndent(INDENT);
            }
            GSON_HOLDER.getGson().toJson(elements, Collection.class, writer);
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
     * エレメントのコレクションをコピーします。
     * @return {@link Collection}&lt;{@link E}&gt; - エレメントのコレクション
     */
    private Collection<E> copyElements() {
        int size = elementMap.size() + subSize();
        if (size == 0) {
            return Collections.emptyList();
        }
        if (isSubEmpty()) {
            return elementMap.values();
        }
        var newList = new ArrayList<E>(size + 1);
        for (var element : elementMap.values()) {
            newList.add(element);
        }
        for (var subElement : subValues()) {
            newList.add(subElement);
        }
        return newList;
    }

    /**
     * JSONのデシリアライズ化を行います。
     * @return {@link Set}&lt;{@link E}&gt; - エレメントの配列
     */
    @Nullable
    private E[] loadJson() throws IOException {
        if (!file.exists()) {
            return null;
        }
        var parent = getParentFile();
        if (!parent.exists()) {
            parent.mkdirs();
        }
        try (var reader = new JsonReader(FileUtils.newBufferedReader(file))) {
            return (E[]) GSON_HOLDER.getGson().fromJson(reader, getArrayClass());
        } catch (NegativeArraySizeException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 配列のクラスを取得します。
     * @throws ClassNotFoundException クラスが見つからなかった時にスローされます。
     * @return {@link Class}&lt;{@link E}[]&gt; - 配列のクラス
     */
    private Class<E[]> getArrayClass() throws ClassNotFoundException {
        if (arrayClass == null) {
            var type = getClass().getGenericSuperclass();
            var args = ((ParameterizedType) type).getActualTypeArguments();
            var element = Class.forName(args[args.length - 1].getTypeName());
            this.arrayClass = (Class<E[]>) Array.newInstance(element, 0).getClass();
        }
        return arrayClass;
    }

    /**
     * JSONのファイルを取得します。
     * @throws NullPointerException {@link JsonTag}が見つからなかった時にスローされます。
     * @param jsonTag - JSONタグ
     * @return {@link File} - JSONのファイル
     */
    @NotNull
    private File getJsonFile(@Nullable String name) {
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