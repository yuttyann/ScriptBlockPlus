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

import java.util.HashMap;
import java.util.Map;

import com.github.yuttyann.scriptblockplus.file.config.SBConfig;
import com.github.yuttyann.scriptblockplus.file.json.derived.BlockScriptJson;
import com.github.yuttyann.scriptblockplus.file.json.derived.PlayerCountJson;
import com.github.yuttyann.scriptblockplus.file.json.derived.PlayerTimerJson;
import com.google.common.base.Function;
import com.google.gson.internal.UnsafeAllocator;

import org.jetbrains.annotations.NotNull;

/**
 * ScriptBlockPlus CacheJson クラス
 * @author yuttyann44581
 */
public final class CacheJson {

    /**
     * 保存したクラスのインスタンスの生成処理が保存されます。
     */
    public static final Map<Class<? extends BaseJson<?>>, CacheJson> CACHES = new HashMap<>();

    static {
        // キャッシュするクラスを登録
        register(BlockScriptJson.class, BlockScriptJson::new);
        register(PlayerCountJson.class, PlayerCountJson::new);
        register(PlayerTimerJson.class, PlayerTimerJson::new);
    }

    /**
     * コンストラクタを呼び出さずに、インスタンスを生成する(非推奨)クラス
     */
    private static final UnsafeAllocator UNSAFE = UnsafeAllocator.create();

    /**
     * Jsonのクラス
     */
    private final Class<? extends BaseJson<?>> json;

    /**
     * インスタンスの生成処理
     */
    private Function<String, ? extends BaseJson<?>> creator;

    /**
     * コンストラクタ
     * @param json - Jsonのクラス
     */
    private CacheJson(@NotNull Class<? extends BaseJson<?>> json) {
        this(json, s -> {
            try {
               // コンストラクタが呼び出されないためメソッドで値の代入を行う。
               var baseJson = UNSAFE.newInstance(json);
               baseJson.constructor(json, s, s.hashCode());
               return baseJson;
            } catch (Exception e) {
                e.printStackTrace();
            }
            throw new IllegalArgumentException("Failed to create an instance");
        });
    }

    /**
     * コンストラクタ
     * @param json - Jsonのクラス
     * @param creator - インスタンスの生成処理
     */
    private CacheJson(@NotNull Class<? extends BaseJson<?>> json, @NotNull Function<String, ? extends BaseJson<?>> creator) {
        this.json = json;
        this.creator = creator;
    }

    /**
     * キャッシュの保存を許可するJsonを登録します。
     * @param json - Jsonのクラス
     */
    public static void register(@NotNull Class<? extends BaseJson<?>> json) {
        CACHES.put(json, new CacheJson(json));
    }

    /**
     * キャッシュの保存を許可するJsonを登録します。
     * @param <T> Jsonの型
     * @param json - Jsonのクラス
     * @param creator - インスタンスの生成処理
     */
    public static <T extends BaseJson<?>> void register(@NotNull Class<T> json, @NotNull Function<String, T> creator) {
        CACHES.put(json, new CacheJson(json, creator));
    }

    /**
     * インスタンスの生成処理を設定します。
     * @param creator - インスタンスの生成処理
     */
    public void setCreator(@NotNull Function<String, ? extends BaseJson<?>> creator) {
        this.creator = creator;
    }

    /**
     * Jsonのクラスを取得します。
     * @return {@link Class}&lt;? extends {@link BaseJson}&gt; - Jsonのクラス
     */
    @NotNull
    public Class<? extends BaseJson<?>> getJsonClass() {
        return json;
    }

    /**
     * インスタンスを生成します。
     * @param name - ファイルの名前
     * @return {@link BaseJson} - インスタンス
     */
    @NotNull
    BaseJson<?> newInstance(@NotNull String name) {
        return creator.apply(name);
    }

    /**
     * 登録されている全てのJsonのキャッシュを生成します。
     */
    public static void loading() {
        CACHES.values().forEach(c -> loading(c.getJsonClass()));
    }

    /**
     * 指定したJsonのキャッシュを生成します。
     * <p>
     * キャッシュに登録していない場合は、生成できません。
     * <p>
     * また、{@link SBConfig#CACHE_ALL_Json}が無効な場合も生成できません。
     * @param json - Jsonのクラス
     */
    public static void loading(@NotNull Class<? extends BaseJson<?>> json) {
        if (!SBConfig.CACHE_ALL_JSON.getValue()) {
            return;
        }
        var cacheJson = CACHES.get(json);
        if (cacheJson == null) {
            return;
        }
        var names = BaseJson.getNames(cacheJson.getJsonClass());
        for (int i = 0, l = names.size(), e = ".json".length(); i < l; i++) {
            var name = names.get(i);
            BaseJson.newJson(cacheJson.getJsonClass(), name.substring(0, name.length() - e));
        }
    }
}