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
package com.github.yuttyann.scriptblockplus.utils;

import java.util.Objects;

import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

/**
 * ScriptBlockPlus ArrayUtils クラス
 * @author yuttyann44581
 */
public final class ArrayUtils {

    /**
     * 空の {@code boolean} の配列です.
     */
    public static final boolean[] EMPTY_BOOLEAN_ARRAY = {};

    /**
     * 空の {@code Boolean} の配列です。
     */
    public static final Boolean[] EMPTY_BOOLEAN_OBJECT_ARRAY = {};

    /**
     * 空の {@code byte} の配列です。
     */
    public static final byte[] EMPTY_BYTE_ARRAY = {};

    /**
     * 空の {@code Byte} の配列です。
     */
    public static final Byte[] EMPTY_BYTE_OBJECT_ARRAY = {};

    /**
     * 空の {@code char} の配列です。
     */
    public static final char[] EMPTY_CHAR_ARRAY = {};

    /**
     * 空の {@code Character} の配列です。
     */
    public static final Character[] EMPTY_CHARACTER_OBJECT_ARRAY = {};

    /**
     * 空の {@code Class} の配列です。
     */
    public static final Class<?>[] EMPTY_CLASS_ARRAY = {};

    /**
     * 空の {@code double} の配列です。
     */
    public static final double[] EMPTY_DOUBLE_ARRAY = {};

    /**
     * 空の {@code Double} の配列です。
     */
    public static final Double[] EMPTY_DOUBLE_OBJECT_ARRAY = {};

    /**
     * 空の {@code float} の配列です。
     */
    public static final float[] EMPTY_FLOAT_ARRAY = {};

    /**
     * 空の {@code Float} の配列です。
     */
    public static final Float[] EMPTY_FLOAT_OBJECT_ARRAY = {};

    /**
     * 空の {@code int} の配列です。
     */
    public static final int[] EMPTY_INT_ARRAY = {};

    /**
     * 空の {@code Integer} の配列です。
     */
    public static final Integer[] EMPTY_INTEGER_OBJECT_ARRAY = {};

    /**
     * 空の {@code long} の配列です。
     */
    public static final long[] EMPTY_LONG_ARRAY = {};

    /**
     * 空の {@code Long} の配列です。
     */
    public static final Long[] EMPTY_LONG_OBJECT_ARRAY = {};

    /**
     * 空の {@code Object} の配列です。
     */
    public static final Object[] EMPTY_OBJECT_ARRAY = {};

    /**
     * 空の {@code short} の配列です。
     */
    public static final short[] EMPTY_SHORT_ARRAY = {};

    /**
     * 空の {@code Short} の配列です。
     */
    public static final Short[] EMPTY_SHORT_OBJECT_ARRAY = {};

    /**
     * 空の {@code String} の配列です。
     */
    public static final String[] EMPTY_STRING_ARRAY = {};

    /**
     * 検索に失敗した場合は {@code -1} が使用されます。
     */
    public static final int INDEX_NOT_FOUND = -1;

    /**
     * 配列内の指定された値の位置を検索します。
     * <p>
     * 何も見つからない場合は、{@code -1}を返します。
     * @param <T> 配列の型
     * @param array - 配列
     * @param value - 値
     * @return {@code int} - 位置
     */
    public static <T> int indexOf(final @NotNull T[] array, final @Nullable T value) {
        if (array == null || array.length == 0) {
            return INDEX_NOT_FOUND;
        }
        for (int i = 0; i < array.length; i++) {
            if (Objects.equals(array[i], value)) {
                return i;
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * 配列内の指定された値の位置を検索します。
     * <p>
     * 何も見つからない場合は、{@code -1}を返します。
     * @param array - 配列
     * @param value - 値
     * @return {@code int} - 位置
     */
    public static int indexOf(final boolean[] array, final boolean value) {
        if (array == null || array.length == 0) {
            return INDEX_NOT_FOUND;
        }
        for (int i = 0; i < array.length; i++) {
            if (array[i] == value) {
                return i;
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * 配列内の指定された値の位置を検索します。
     * <p>
     * 何も見つからない場合は、{@code -1}を返します。
     * @param array - 配列
     * @param value - 値
     * @return {@code int} - 位置
     */
    public static int indexOf(final byte[] array, final byte value) {
        if (array == null || array.length == 0) {
            return INDEX_NOT_FOUND;
        }
        for (int i = 0; i < array.length; i++) {
            if (array[i] == value) {
                return i;
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * 配列内の指定された値の位置を検索します。
     * <p>
     * 何も見つからない場合は、{@code -1}を返します。
     * @param array - 配列
     * @param value - 値
     * @return {@code int} - 位置
     */
    public static int indexOf(final char[] array, final char value) {
        if (array == null || array.length == 0) {
            return INDEX_NOT_FOUND;
        }
        for (int i = 0; i < array.length; i++) {
            if (array[i] == value) {
                return i;
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * 配列内の指定された値の位置を検索します。
     * <p>
     * 何も見つからない場合は、{@code -1}を返します。
     * @param array - 配列
     * @param value - 値
     * @return {@code int} - 位置
     */
    public static int indexOf(final double[] array, final double value) {
        if (array == null || array.length == 0) {
            return INDEX_NOT_FOUND;
        }
        for (int i = 0; i < array.length; i++) {
            if (array[i] == value) {
                return i;
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * 配列内の指定された値の位置を検索します。
     * <p>
     * 何も見つからない場合は、{@code -1}を返します。
     * @param array - 配列
     * @param value - 値
     * @return {@code int} - 位置
     */
    public static int indexOf(final float[] array, final float value) {
        if (array == null || array.length == 0) {
            return INDEX_NOT_FOUND;
        }
        for (int i = 0; i < array.length; i++) {
            if (array[i] == value) {
                return i;
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * 配列内の指定された値の位置を検索します。
     * <p>
     * 何も見つからない場合は、{@code -1}を返します。
     * @param array - 配列
     * @param value - 値
     * @return {@code int} - 位置
     */
    public static int indexOf(final int[] array, final int value) {
        if (array == null || array.length == 0) {
            return INDEX_NOT_FOUND;
        }
        for (int i = 0; i < array.length; i++) {
            if (array[i] == value) {
                return i;
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * 配列内の指定された値の位置を検索します。
     * <p>
     * 何も見つからない場合は、{@code -1}を返します。
     * @param array - 配列
     * @param value - 値
     * @return {@code int} - 位置
     */
    public static int indexOf(final long[] array, final long value) {
        if (array == null || array.length == 0) {
            return INDEX_NOT_FOUND;
        }
        for (int i = 0; i < array.length; i++) {
            if (array[i] == value) {
                return i;
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * 配列内の指定された値の位置を検索します。
     * <p>
     * 何も見つからない場合は、{@code -1}を返します。
     * @param array - 配列
     * @param value - 値
     * @return {@code int} - 位置
     */
    public static int indexOf(final short[] array, final short value) {
        if (array == null || array.length == 0) {
            return INDEX_NOT_FOUND;
        }
        for (int i = 0; i < array.length; i++) {
            if (array[i] == value) {
                return i;
            }
        }
        return INDEX_NOT_FOUND;
    }
}