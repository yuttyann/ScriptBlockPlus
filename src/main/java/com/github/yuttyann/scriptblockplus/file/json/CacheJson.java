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

import java.util.function.Function;

import org.jetbrains.annotations.NotNull;

/**
 * ScriptBlockPlus CacheJson クラス
 * @param <T> 引数の型
 * @author yuttyann44581
 */
public final class CacheJson<T> {

    private final Class<? extends BaseJson<?>> jsonClass;
    private final Function<T, ? extends BaseJson<?>> newInstance;

    /**
     * コンストラクタ
     * @param jsonClass - JSONのクラス
     * @param newInstance - インスタンスの生成処理
     */
    public CacheJson(Class<? extends BaseJson<?>> jsonClass, @NotNull Function<T, ? extends BaseJson<?>> newInstance) {
        this.jsonClass = jsonClass;
        this.newInstance = newInstance;
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
     * @param argment - 引数
     * @return {@link BaseJson} - インスタンス
     */
    @NotNull
    BaseJson<?> newInstance(@NotNull T argment) {
        return newInstance.apply(argment);
    }
}