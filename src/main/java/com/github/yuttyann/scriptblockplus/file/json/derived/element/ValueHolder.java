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
package com.github.yuttyann.scriptblockplus.file.json.derived.element;

import java.util.Optional;
import java.util.function.Supplier;

import com.github.yuttyann.scriptblockplus.utils.StreamUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * ScriptBlockPlus ValueHolder クラス
 * @author yuttyann44581
 */
public class ValueHolder implements Cloneable {

    /**
     * ScriptBlockPlus ValueType 列挙型
     * <p>
     * 参照型のクラスのみです。
     * @author yuttyann44581
     */
    public enum ValueType {
        BYTE(Byte.class),
        SHORT(Short.class),
        INTEGER(Integer.class),
        LONG(Long.class),
        FLOAT(Float.class),
        DOUBLE(Double.class),
        BOOLEAN(Boolean.class),
        CHARACTER(Character.class),
        STRING(String.class);

        private final Class<?> reference;

        ValueType(@NotNull Class<?> reference) {
            this.reference = reference;
        }

        /**
         * クラスを取得します。
         * @return {@link Class}&lt;?&gt; - クラス
         */
        @NotNull
        public Class<?> getReference() {
            return reference;
        }

        /**
         * 指定された値を、適切な型へ変換します。
         * @param value - 値
         * @return {@link Object} - 変換後の値
         */
        @NotNull
        public Object parse(@NotNull String value) {
            switch (this) {
                case BYTE:
                    return Byte.parseByte(value);
                case SHORT:
                    return Short.parseShort(value);
                case INTEGER:
                    return Integer.parseInt(value);
                case LONG:
                    return Long.parseLong(value);
                case FLOAT:
                    return Float.parseFloat(value);
                case DOUBLE:
                    return Double.parseDouble(value);
                case BOOLEAN:
                    return Boolean.parseBoolean(value);
                case CHARACTER:
                    return value.charAt(0);
                default:
                    return value;
            }
        }

        /**
         * 指定したクラスと一致する列挙子を取得します。
         * @param clazz - クラス
         * @return {@link Optional}&lt;{@link ValueType}&gt; - 列挙子
         */
        @NotNull
        public static Optional<ValueType> fromClass(@NotNull Class<?> clazz) {
            return StreamUtils.filterFirst(values(), v -> v.reference.equals(clazz));
        }
    }

    /**
     * 空の値
     */
    public static final ValueHolder EMPTY = new ValueHolder() {

        @Override
        @NotNull
        public ValueHolder setValue(@NotNull Object value) {
            return this;
        }

        @Override
        @NotNull
        public ValueHolder clone() {
            return this;
        }

        @Override
        @NotNull
        public String toString() {
            return "string:null";
        }
    };

    private static final Supplier<RuntimeException> THROW = () -> new NullPointerException("Type not found");

    private final ValueType valueType;

    private Object value;

    /**
     * コンストラクタ
     */
    private ValueHolder() {
        this.valueType = ValueType.STRING;
        this.value = "";
    }

    /**
     * コンストラクタ
     * @param value - 値
     */
    public ValueHolder(@NotNull Object value) {
        this.valueType = ValueType.fromClass(value.getClass()).orElseThrow(THROW);
        this.value = value;
    }

    /**
     * コンストラクタ
     * @param valueType - 値の型
     * @param value - 値
     */
    public ValueHolder(@NotNull ValueType valueType, @NotNull Object value) {
        this.valueType = valueType;
        setValue(value);
    }

    /**
     * 値を設定します。
     * @param value - 値
     * @return {@link ValueHolder}
     */
    @NotNull
    public ValueHolder setValue(@NotNull Object value) {
        if (!value.getClass().equals(valueType.getReference())) {
            throw new IllegalArgumentException("Invalid value");
        }
        this.value = value;
        return this;
    }

    /**
     * 値の型を取得します。
     * @return {@link ValueType} - 値の型
     */
    @NotNull
    public ValueType getValueType() {
        return valueType;
    }

    /**
     * 値を{@code byte}型として取得します。
     * @return {@code byte} - 値
     */
    public byte asByte() {
        return asByte((byte) 0);
    }

    /**
     * 値を{@code byte}型として取得します。
     * @param def - デフォルト値
     * @return {@code byte} - 値
     */
    public byte asByte(byte def) {
        return value instanceof Byte && valueType == ValueType.BYTE ? (byte) value : def;
    }

    /**
     * 値を{@code short}型として取得します。
     * @return {@code short} - 値
     */
    public short asShort() {
        return asShort((short) 0);
    }

    /**
     * 値を{@code short}型として取得します。
     * @param def - デフォルト値
     * @return {@code short} - 値
     */
    public short asShort(short def) {
        return value instanceof Short && valueType == ValueType.SHORT ? (short) value : def;
    }

    /**
     * 値を{@code int}型として取得します。
     * @return {@code int} - 値
     */
    public int asInt() {
        return asInt(0);
    }

    /**
     * 値を{@code int}型として取得します。
     * @param def - デフォルト値
     * @return {@code int} - 値
     */
    public int asInt(int def) {
        return value instanceof Integer && valueType == ValueType.INTEGER ? (int) value : def;
    }

    /**
     * 値を{@code long}型として取得します。
     * @return {@code long} - 値
     */
    public long asLong() {
        return asLong(0L);
    }

    /**
     * 値を{@code long}型として取得します。
     * @param def - デフォルト値
     * @return {@code long} - 値
     */
    public long asLong(long def) {
        return value instanceof Long && valueType == ValueType.LONG ? (long) value : def;
    }

    /**
     * 値を{@code float}型として取得します。
     * @return {@code float} - 値
     */
    public float asFloat() {
        return asFloat(0.0F);
    }

    /**
     * 値を{@code float}型として取得します。
     * @param def - デフォルト値
     * @return {@code float} - 値
     */
    public float asFloat(float def) {
        return value instanceof Float && valueType == ValueType.FLOAT ? (float) value : def;
    }

    /**
     * 値を{@code double}型として取得します。
     * @return {@code double} - 値
     */
    public double asDouble() {
        return asDouble(0.0D);
    }

    /**
     * 値を{@code double}型として取得します。
     * @param def - デフォルト値
     * @return {@code double} - 値
     */
    public double asDouble(double def) {
        return value instanceof Double && valueType == ValueType.DOUBLE ? (long) value : def;
    }

    /**
     * 値を{@code boolean}型として取得します。
     * @return {@code boolean} - 値
     */
    public boolean asBoolean() {
        return asBoolean(false);
    }

    /**
     * 値を{@code boolean}型として取得します。
     * @param def - デフォルト値
     * @return {@code boolean} - 値
     */
    public boolean asBoolean(boolean def) {
        return value instanceof Boolean && valueType == ValueType.BOOLEAN ? (boolean) value : def;
    }

    /**
     * 値を{@code char}型として取得します。
     * @return {@code char} - 値
     */
    public char asChar() {
        return asChar(Character.MIN_VALUE);
    }

    /**
     * 値を{@code char}型として取得します。
     * @param def - デフォルト値
     * @return {@code char} - 値
     */
    public char asChar(char def) {
        return value instanceof Character && valueType == ValueType.CHARACTER ? (char) value : def;
    }

    /**
     * 値を{@link String}型として取得します。
     * @return {@link String} - 値
     */
    @NotNull
    public String asString() {
        return asString("");
    }

    /**
     * 値を{@link String}型として取得します。
     * @param def - デフォルト値
     * @return {@link String} - 値
     */
    @Nullable
    public String asString(@Nullable String def) {
        return value instanceof String && valueType == ValueType.STRING ? (String) value : def;
    }

    @Override
    @NotNull
    public ValueHolder clone() {
        return new ValueHolder(valueType, value);
    }

    @Override
    @NotNull
    public String toString() {
        return valueType.name().toLowerCase() + ":" + value;
    }
}