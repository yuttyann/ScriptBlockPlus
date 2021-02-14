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
import java.util.function.Function;

import com.github.yuttyann.scriptblockplus.file.config.SBConfig;
import com.github.yuttyann.scriptblockplus.file.json.derived.BlockScriptJson;
import com.github.yuttyann.scriptblockplus.file.json.derived.PlayerCountJson;
import com.github.yuttyann.scriptblockplus.file.json.derived.PlayerTempJson;

import org.jetbrains.annotations.NotNull;

/**
 * ScriptBlockPlus CacheJson クラス
 * @author yuttyann44581
 */
public final class CacheJson {

    public static final Map<Class<? extends BaseJson<?>>, CacheJson> CACHE_MAP = new HashMap<>();

    private final Class<? extends BaseJson<?>> jsonClass;
    private final Function<String, ? extends BaseJson<?>> newInstance;

    /**
     * コンストラクタ
     * @param jsonClass - JSONのクラス
     * @param newInstance - インスタンスの生成処理
     */
    public CacheJson(@NotNull Class<? extends BaseJson<?>> jsonClass, @NotNull Function<String, ? extends BaseJson<?>> newInstance) {
        this.jsonClass = jsonClass;
        this.newInstance = newInstance;
    }

    /**
     * 自動的なキャッシュの生成を許可します。
     * <p>
     * プラグインの起動時とリロード時に、自動でキャッシュを生成することができる。
     * <p>
     * ※{@link SBConfig#CACHE_ALL_JSON}を有効にしている必要があります。
     */
    public void setLoader() {
        CACHE_MAP.put(getJsonClass(), this);
    }

    /**
     * JSONのクラスを取得します。
     * @return {@link Class}&lt;? extends {@link BaseJson}&gt; - JSONのクラス
     */
    @NotNull
    public Class<? extends BaseJson<?>> getJsonClass() {
        return jsonClass;
    }

    /**
     * インスタンスを生成します。
     * @param name - ファイルの名前
     * @return {@link BaseJson} - インスタンス
     */
    @NotNull
    BaseJson<?> newInstance(@NotNull String name) {
        return newInstance.apply(name);
    }

    /**
     * 登録されている全てのJSONのキャッシュを生成する。
     */
    public static void loading() {
        if (CACHE_MAP.isEmpty()) {
            PlayerTempJson.CACHE_JSON.setLoader();
            PlayerCountJson.CACHE_JSON.setLoader();
            BlockScriptJson.CACHE_JSON.setLoader();
        }
        CACHE_MAP.values().forEach(c -> loading(c.getJsonClass()));
    }

    /**
     * 指定したJSONのキャッシュを生成する。
     * <p>
     * 自動的なキャッシュの生成を許可していない場合は、生成できません。
     * <p>
     * また、{@link SBConfig#CACHE_ALL_JSON}が無効な場合も生成できません。
     * @param jsonClass - JSONのクラス
     */
    public static void loading(@NotNull Class<? extends BaseJson<?>> jsonClass) {
        if (!SBConfig.CACHE_ALL_JSON.getValue()) {
            return;
        }
        var cacheJson = CACHE_MAP.get(jsonClass);
        if (cacheJson == null) {
            return;
        }
        var names = BaseJson.getNames(cacheJson.getJsonClass());
        for (int i = 0, l = names.size(), e = ".json".length(); i < l; i++) {
            var name = names.get(i);
            BaseJson.newJson(name.substring(0, name.length() - e), cacheJson);
        }
    }
}