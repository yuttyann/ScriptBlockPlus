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
import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.file.SBFile;
import com.github.yuttyann.scriptblockplus.file.config.SBConfig;
import com.github.yuttyann.scriptblockplus.file.json.annotation.JsonTag;
import com.github.yuttyann.scriptblockplus.file.json.builder.BlockCoordsAdapter;
import com.github.yuttyann.scriptblockplus.file.json.builder.CollectionType;
import com.github.yuttyann.scriptblockplus.file.json.builder.FieldExclusion;
import com.github.yuttyann.scriptblockplus.file.json.builder.NumberAdapter;
import com.github.yuttyann.scriptblockplus.file.json.builder.ScriptKeyAdapter;
import com.github.yuttyann.scriptblockplus.file.json.element.BlockScript;
import com.github.yuttyann.scriptblockplus.file.json.element.PlayerCount;
import com.github.yuttyann.scriptblockplus.file.json.element.PlayerTimer;
import com.github.yuttyann.scriptblockplus.script.ScriptKey;
import com.github.yuttyann.scriptblockplus.utils.collection.IntMap;
import com.github.yuttyann.scriptblockplus.utils.FileUtils;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.collection.IntHashMap;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.*;
import java.util.function.Supplier;

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
         * 生成方法は{@link BaseJson#newJson(String, CacheJson)}を参照してください。
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
    private CollectionType collectionType;

    private IntMap<E> elementMap;

    // GsonBuilderにアダプター等の追加を行う。
    static {
        GSON_HOLDER.builder(b -> {
            b.setExclusionStrategies(new FieldExclusion());
            b.registerTypeAdapter(Map.class, new NumberAdapter());
            b.registerTypeAdapter(List.class, new NumberAdapter());
            b.registerTypeAdapter(ScriptKey.class, new ScriptKeyAdapter());
            b.registerTypeHierarchyAdapter(BlockCoords.class, new BlockCoordsAdapter());
            b.registerTypeAdapter(BlockScript.class, newInstance(() -> new BlockScript(BlockCoords.ZERO)));
            b.registerTypeAdapter(PlayerCount.class, newInstance(() -> new PlayerCount(ScriptKey.INTERACT, BlockCoords.ZERO)));
            b.registerTypeAdapter(PlayerTimer.class, newInstance(() -> new PlayerTimer(null, ScriptKey.INTERACT, BlockCoords.ZERO))); 
        });
    }

    /**
     * コンストラクタ
     * @param name - ファイルの名前
     */
    protected BaseJson(@NotNull String name) {
        if ((this.jsonTag = getClass().getAnnotation(JsonTag.class)) == null) {
            throw new NullPointerException("Annotation not found @JsonTag()");
        }
        this.file = getJsonFile(name);
        this.name = name;
        try {
            setMap(loadJson());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        noCache();
    }

    /**
     * JSONを取得します。
     * <p>
     * このメソッドは、キャッシュの保存を行います。
     * <p>
     * キャッシュが見つからない場合は、インスタンスの生成を行います。
     * <p>
     * また、キャッシュを利用する場合は基本的に"private"なコンストラクタの実装を推奨します。
     * @param <T> - JSONの型
     * @param name - ファイルの名前
     * @param cacheJson - キャッシュ
     * @return {@link BaseJson} - インスタンス
     */
    @NotNull
    public static <T extends BaseJson<?>> T newJson(@NotNull String name, @NotNull CacheJson cacheJson) {
        var hash = hash(name, cacheJson.getJsonClass());
        var baseJson = JSON_CACHE.get(hash);
        if (baseJson == null) {
            baseJson = cacheJson.newInstance(name);
            if (baseJson.isCacheFileExists() && !baseJson.exists()) {
                return (T) baseJson;
            }
            baseJson.keepCache();
            baseJson.setCacheId(hash);
            JSON_CACHE.put(hash, baseJson);
        }
        return (T) baseJson;
    }

    /**
     * キャッシュを取得します。
     * <p>
     * キャッシュが見つからない場合は、生成したインスタンスを返します。
     * <p>
     * また、キャッシュを利用する場合は基本的に"private"なコンストラクタの実装を推奨します。
     * @param <T> JSONの型
     * @param name - ファイルの名前
     * @param cacheJson - キャッシュ
     * @return {@link BaseJson} - インスタンス
     */
    @NotNull
    public static <T extends BaseJson<?>> T getCache(@NotNull String name, @NotNull CacheJson cacheJson) {
        var baseJson = JSON_CACHE.get(hash(name, cacheJson.getJsonClass()));
        if (baseJson == null) {
            baseJson = cacheJson.newInstance(name);
        }
        return (T) baseJson;
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
     * {@link IntMap}&lt;{@link E}&gt;を取得します。
     * @return {@link IntMap}&lt;{@link E}&gt; - エレメントのマップ
     */
    @Override
    @NotNull
    protected final IntMap<E> getElementMap() {
        return elementMap;
    }

    /**
     * {@link IntMap}&lt;{@link E}&gt;を生成します。
     * @return {@link IntMap}&lt;{@link E}&gt; - マップ
     */
    @NotNull
    protected IntMap<E> createMap() {
        return IntHashMap.create();
    }

    /**
     * 要素の再読み込みを行います。
     */
    public final void reload() {
        Objects.requireNonNull(file, "Please load the file");
        try {
            setMap(loadJson());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
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
     * @return {@code int} - キャッシュID
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
     * @return {@code boolean} - 削除を行う場合は{@code true}
     */
    protected boolean isTemporary() {
        return false;
    }

    /**
     * ファイルが存在する時のみキャッシュを保存するのかどうか。
     * @return {@code boolean} - ファイルが存在する時のみキャッシュを保存する場合は{@code true}
     */
    protected boolean isCacheFileExists() {
        return true;
    }

    /**
     * マップに要素が存在しない場合に{@code true}を返します。
     * @return {@code boolean} - 要素が存在しない場合は{@code true}
     */
    public final boolean isEmpty() {
        return elementMap.isEmpty();
    }

    /**
     * ファイルが存在するのか確認します。
     * @return {@code boolean} - ファイルが存在する場合は{@code true}
     */
    public final boolean exists() {
        return file.exists();
    }

    /**
     * キャッシュされた要素を含め、ファイルを削除します。
     * @return {@code boolean} - 削除に成功した場合は{@code true}
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
            GSON_HOLDER.getGson().toJson(elements, getCollectionType(), writer);
        } catch (IOException | ClassNotFoundException e) {
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
    @NotNull
    private Collection<E> copyElements() {
        int size = elementMap.size() + subSize();
        if (size == 0) {
            return Collections.emptyList();
        }
        if (isSubEmpty()) {
            return elementMap.values();
        }
        var newList = new ArrayList<E>(size);
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
     * @throws ClassNotFoundException
     */
    @Nullable
    private List<E> loadJson() throws IOException, ClassNotFoundException {
        if (!file.exists()) {
            return null;
        }
        var parent = getParentFile();
        if (!parent.exists()) {
            parent.mkdirs();
        }
        try (var reader = new JsonReader(FileUtils.newBufferedReader(file))) {
            return (List<E>) GSON_HOLDER.getGson().fromJson(reader, getCollectionType());
        }
    }

    /**
     * コレクションタイプを取得します。
     * @return {@link CollectionType} - コレクションタイプ 
     */
    @NotNull
    private CollectionType getCollectionType() throws ClassNotFoundException {
        if (collectionType == null) {
            this.collectionType = new CollectionType(Collection.class, this);
        }
        return collectionType;
    }

    /**
     * マップに要素を追加します。
     * @param elements - エレメントの配列
     */
    private void setMap(@Nullable List<E> elements) {
        var newMap = createMap();
        if (elements != null && elements.size() > 0) {
            int size = elements.size();
            if (size == 1) {
                var element = elements.get(0);
                newMap.put(element.hashCode(), element);
            } else {
                for (int i = 0; i < size; i++) {
                    var element = elements.get(i);
                    int hashCode = element.hashCode();
                    if (newMap.containsKey(hashCode)) {
                        subPut(hashCode, element);
                    } else {
                        newMap.put(hashCode, element);
                    }
                }
            }
        }
        this.elementMap = newMap;
    }

    /**
     * JSONのファイルを取得します。
     * @param name - ファイルの名前
     * @return {@link File} - JSONのファイル
     */
    @NotNull
    private File getJsonFile(@Nullable String name) {
        return new SBFile(getPlugin(jsonTag).getDataFolder(), jsonTag.path() + "/" + name + ".json");
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
        var folder = new SBFile(getPlugin(jsonTag).getDataFolder(), jsonTag.path());
        return folder.exists() ? folder.listFiles(f -> f.getName().endsWith(".json")) : new File[0];
    }

    /**
     * フォルダ内の全てのファイルの名前を取得します。
     * @throws NullPointerException {@link JsonTag}が見つからなかった時にスローされます。
     * @param jsonClass - {@link BaseJson}を継承したクラス
     * @return {@link List}&lt;{@link String}&gt; - 全てのファイルの名前
     */
    @NotNull
    public static List<String> getNames(@NotNull Class<? extends BaseJson<?>> jsonClass) {
        var jsonTag = jsonClass.getAnnotation(JsonTag.class);
        if (jsonTag == null) {
            throw new NullPointerException("Annotation not found @JsonTag()");
        }
        var folder = new SBFile(getPlugin(jsonTag).getDataFolder(), jsonTag.path());
        return folder.exists() ? folder.listNames((f, s) -> s.endsWith(".json")) : Collections.emptyList();
    }

    /**
     * プラグインを取得します。
     * @param jsonTag - JSONタグ
     * @return {@link Plugin} - プラグイン
     */
    @NotNull
    private static Plugin getPlugin(@NotNull JsonTag jsonTag) {
        var plugin = (Plugin) ScriptBlock.getInstance();
        if (StringUtils.isNotEmpty(jsonTag.plugin())) {
            plugin = Bukkit.getPluginManager().getPlugin(jsonTag.plugin());
        }
        return plugin;
    }

    /**
     * {@link InstanceCreator}を生成します。
     * @param <T> インスタンスの型
     * @param newInstance - インスタンスの生成処理
     * @return {@link InstanceCreator}&lt;{@link T}&gt; - インスタンスクリエイター
     */
    @NotNull
    private static <T> InstanceCreator<T> newInstance(@NotNull Supplier<T> newInstance) {
        return t -> newInstance.get();
    }

    /**
     * ハッシュコードを生成します。
     * @param name - ファイルの名前
     * @param jsonClass - JSONのクラス
     * @return {@code int} - ハッシュコード
     */
    private static int hash(@NotNull String name, @NotNull Class<?> jsonClass) {
        int hash = 1;
        int prime = 31;
        hash = prime * hash + name.hashCode();
        hash = prime * hash + jsonClass.hashCode();
        return hash;
    }

    @Override
    public int hashCode() {
        return hash(name, getClass());
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

    @Override
    @NotNull
    public String toString() {
        try {
            return GSON_HOLDER.getGson().toJson(copyElements(), getCollectionType());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return "[]";
    }
}