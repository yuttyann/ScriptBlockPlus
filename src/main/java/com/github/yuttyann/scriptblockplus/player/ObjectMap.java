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
package com.github.yuttyann.scriptblockplus.player;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * ScriptBlockPlus ObjectMap インターフェース
 * @author yuttyann44581
 */
public interface ObjectMap {

    /**
     * 指定された値と指定されたキーをこのマップで関連付けます。
     * @param key - キー
     * @param value - 値
     */
    void put(@NotNull String key, @Nullable Object value);

    /**
     * 指定されたキーがマップされている値を{@link Byte}にキャストして返します。
     * @param key - キー
     * @return {@link Byte} - 指定されたキーがマップされている値
     */
    default byte getByte(@NotNull String key) {
        return get(key, (byte) 0);
    }

    /**
     * 指定されたキーがマップされている値を{@link Short}にキャストして返します。
     * @param key - キー
     * @return {@link Short} - 指定されたキーがマップされている値
     */
    default short getShort(@NotNull String key) {
        return get(key, (short) 0);
    }

    /**
     * 指定されたキーがマップされている値を{@link Integer}にキャストして返します。
     * @param key - キー
     * @return {@link Integer} - 指定されたキーがマップされている値
     */
    default int getInt(@NotNull String key) {
        return get(key, 0);
    }

    /**
     * 指定されたキーがマップされている値を{@link Long}にキャストして返します。
     * @param key - キー
     * @return {@link Long} - 指定されたキーがマップされている値
     */
    default long getLong(@NotNull String key) {
        return get(key, 0L);
    }

    /**
     * 指定されたキーがマップされている値を{@link Character}にキャストして返します。
     * @param key - キー
     * @return {@link Character} - 指定されたキーがマップされている値
     */
    default char getChar(@NotNull String key) {
        return get(key, Character.MIN_VALUE);
    }

    /**
     * 指定されたキーがマップされている値を{@link Float}にキャストして返します。
     * @param key - キー
     * @return {@link Float} - 指定されたキーがマップされている値
     */
    default float getFloat(@NotNull String key) {
        return get(key, 0.0F);
    }

    /**
     * 指定されたキーがマップされている値を{@link Double}にキャストして返します。
     * @param key - キー
     * @return {@link Double} - 指定されたキーがマップされている値
     */
    default double getDouble(@NotNull String key) {
        return get(key, 0.0D);
    }

    /**
     * 指定されたキーがマップされている値を{@link Boolean}にキャストして返します。
     * @param key - キー
     * @return {@link Boolean} - 指定されたキーがマップされている値
     */
    default boolean getBoolean(@NotNull String key) {
        return get(key, false);
    }

    /**
     * 指定されたキーがマップされている値を{@link String}にキャストして返します。
     * @param key - キー
     * @return {@link String} - 指定されたキーがマップされている値
     */
    @NotNull
    default String getString(@NotNull String key) {
        return get(key, "");
    }

    /**
     * 指定されたキーがマップされている値を返します。
     * <p>
     * このマップにそのキーのマッピングが含まれていない場合は{@code null}を返します。
     * @param key - キー
     * @param <T> 戻り値の型
     * @return {@link T} - 指定されたキーがマップされている値
     */
    @Nullable
    <T> T get(@NotNull String key);

    /**
     * 指定されたキーがマップされている値を返します。
     * <p>
     * このマップにそのキーのマッピングが含まれていない場合はotherを返します。
     * @param key - キー
     * @param other - その他
     * @param <T> 戻り値の型
     * @return {@link T} - 指定されたキーがマップされている値
     */
    @NotNull
    default <T> T get(@NotNull String key, @NotNull T other) {
        T t = get(key);
        return t == null ? other : t;
    }

    /**
     * このマップからキーのマッピングを削除します。
     * @param key - キー
     */
    void remove(String key);

    /**
     * 指定されたキーに値がマップされている場合にtrueを返します。
     * @param key - キー
     * @return {@link Boolean} - 指定されたキーに値がマップされている場合は{@code true}
     */
    default boolean has(@NotNull String key) {
        return get(key) != null;
    }

    /**
     * マップからマッピングをすべて削除します。
     */
    void clear();
}