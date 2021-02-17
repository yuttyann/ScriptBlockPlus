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
 * 
 * Gson license <https://github.com/google/gson/blob/master/LICENSE>
 */
package com.github.yuttyann.scriptblockplus.file.json.legacy;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.github.yuttyann.scriptblockplus.file.json.annotation.Alternate;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * ScriptBlockPlus LegacyReflectiveFactory クラス
 * <p>
 * Gsonのバージョンが古い場合は、これが呼ばれる。
 * @author gson(https://github.com/google/gson), yuttyann44581
 */
public final class LegacyEnumFactory implements TypeAdapterFactory {

    public static final TypeAdapterFactory INSTANCE = new LegacyEnumFactory();

    private LegacyEnumFactory() { }

    @Override
    @Nullable
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public <T> TypeAdapter<T> create(@NotNull Gson gson, @NotNull TypeToken<T> typeToken) {
        var rawType = typeToken.getRawType();
        if (!Enum.class.isAssignableFrom(rawType) || rawType == Enum.class) {
            return null;
        }
        if (!rawType.isEnum()) {
            rawType = rawType.getSuperclass();
        }
        return (TypeAdapter<T>) new EnumTypeAdapter(rawType);
    }

    private final class EnumTypeAdapter<T extends Enum<T>> extends TypeAdapter<T> {

        private final Map<String, T> nameToConstant = new HashMap<>();
        private final Map<T, String> constantToName = new HashMap<>();
    
        public EnumTypeAdapter(@NotNull Class<T> rawType) {
            try {
                for (T constant : rawType.getEnumConstants()) {
                    var name = constant.name();
                    var field = rawType.getField(name);
                    var serializedName = field.getAnnotation(SerializedName.class);
                    if (serializedName != null) {
                        name = serializedName.value();
                    }
                    var legacyAlternate = field.getAnnotation(Alternate.class);
                    if (legacyAlternate != null) {
                        for (var alternate : legacyAlternate.value()) {
                            nameToConstant.put(alternate, constant);
                        }
                    }
                    nameToConstant.put(name, constant);
                    constantToName.put(constant, name);
                }
            } catch (NoSuchFieldException e) {
                throw new AssertionError(e);
            }
        }

        @Override
        public void write(@NotNull JsonWriter writer, @Nullable T value) throws IOException {
            writer.value(value == null ? null : constantToName.get(value));
        }

        @Override
        public T read(@NotNull JsonReader reader) throws IOException {
            if (reader.peek() == JsonToken.NULL) {
                reader.nextNull();
                return null;
            }
            return nameToConstant.get(reader.nextString());
        }
    }
}