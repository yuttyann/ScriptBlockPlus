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
import com.google.common.base.Function;
import com.google.gson.internal.UnsafeAllocator;

import org.jetbrains.annotations.NotNull;

/**
 * ScriptBlockPlus CacheJson クラス
 * @author yuttyann44581
 */
public final class CacheJson {

    private static final UnsafeAllocator UNSAFE = UnsafeAllocator.create();

    static final Map<Class<? extends BaseJson<?>>, CacheJson> CACHE_MAP = new HashMap<>();

    private final Class<? extends BaseJson<?>> json;

    private Function<String, ? extends BaseJson<?>> creator;

    /**
     * コンストラクタ
     * @param json - JSONのクラス
     */
    private CacheJson(@NotNull Class<? extends BaseJson<?>> json) {
        this.json = json;
        this.creator = s -> {
            try {
               var baseJson = UNSAFE.newInstance(json);
               baseJson.constructor(json, s, s.hashCode());
               return baseJson;
            } catch (Exception e) {
                e.printStackTrace();
            }
            throw new IllegalArgumentException("Failed to create an instance");
        };
    }

    /**
     * キャッシュを登録します。
     * @param json - JSONのクラス
     */
    public static void register(@NotNull Class<? extends BaseJson<?>> json) {
        CACHE_MAP.put(json, new CacheJson(json));
    }

    /**
     * キャッシュを登録します。
     * @param json - JSONのクラス
     * @param creator - インスタンスの生成処理
     */
    public static void register(@NotNull Class<? extends BaseJson<?>> json, @NotNull Function<String, ? extends BaseJson<?>> creator) {
        var cacheJson = new CacheJson(json);
        cacheJson.creator = creator;
        CACHE_MAP.put(json, cacheJson);
    }

    /**
     * インスタンスの生成処理を設定します。
     * @param creator - インスタンスの生成処理
     */
    public void setCreator(@NotNull Function<String, ? extends BaseJson<?>> creator) {
        this.creator = creator;
    }

    /**
     * JSONのクラスを取得します。
     * @return {@link Class}&lt;? extends {@link BaseJson}&gt; - JSONのクラス
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
     * 登録されている全てのJSONのキャッシュを生成します。
     */
    public static void loading() {
        CACHE_MAP.values().forEach(c -> loading(c.getJsonClass()));
    }

    /**
     * 指定したJSONのキャッシュを生成します。
     * <p>
     * キャッシュに登録していない場合は、生成できません。
     * <p>
     * また、{@link SBConfig#CACHE_ALL_JSON}が無効な場合も生成できません。
     * @param json - JSONのクラス
     */
    public static void loading(@NotNull Class<? extends BaseJson<?>> json) {
        if (!SBConfig.CACHE_ALL_JSON.getValue()) {
            return;
        }
        var cacheJson = CACHE_MAP.get(json);
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