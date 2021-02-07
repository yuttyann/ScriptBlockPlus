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
package com.github.yuttyann.scriptblockplus.file.json.builder;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.github.yuttyann.scriptblockplus.file.json.annotation.LegacyName;
import com.google.gson.FieldNamingStrategy;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.annotations.SerializedName;
import com.google.gson.internal.ConstructorConstructor;
import com.google.gson.internal.$Gson$Types;
import com.google.gson.internal.Excluder;
import com.google.gson.internal.ObjectConstructor;
import com.google.gson.internal.Primitives;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * ScriptBlockPlus LegacyReflectiveFactory クラス
 * <p>
 * Gsonのバージョンが古い場合はこちらが呼び出される。
 * @author gson(https://github.com/google/gson), yuttyann44581
 */
public final class LegacyReflectiveFactory implements TypeAdapterFactory {

    private static final Constructor<?> RUNTIME_CONSTRUCTOR;

    static {
        var constructor = (Constructor<?>) null;
        try {
            var runtime = Class.forName("com.google.gson.internal.bind.TypeAdapterRuntimeTypeWrapper");
            constructor = runtime.getDeclaredConstructor(Gson.class, TypeAdapter.class, Type.class);
            constructor.setAccessible(true);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
        RUNTIME_CONSTRUCTOR = constructor;
    }

    private final ConstructorConstructor constructorConstructor;
    private final FieldNamingStrategy fieldNamingPolicy;
    private final Excluder excluder;
  
    public LegacyReflectiveFactory(@NotNull ConstructorConstructor constructorConstructor, @NotNull FieldNamingStrategy fieldNamingPolicy, @NotNull Excluder excluder) {
        this.constructorConstructor = constructorConstructor;
        this.fieldNamingPolicy = fieldNamingPolicy;
        this.excluder = excluder;
    }

    public boolean excludeField(@NotNull Field field, boolean serialize) {
        return !excluder.excludeClass(field.getType(), serialize) && !excluder.excludeField(field, serialize);
    }

    @Override
    public <T> TypeAdapter<T> create(@NotNull Gson gson, @NotNull TypeToken<T> type) {
        var raw = type.getRawType();
        if (!Object.class.isAssignableFrom(raw)) {
            return null;
        }
        var constructor = constructorConstructor.get(type);
        return new Adapter<T>(constructor, getBoundFields(gson, type, raw));
    }
  
    @NotNull
    private List<String> getFieldNames(Field field) {
        var serializedName = field.getAnnotation(SerializedName.class);
        if (serializedName == null) {
            return Collections.singletonList(fieldNamingPolicy.translateName(field));
        }
        var value = serializedName.value();
        var legacyName = field.getAnnotation(LegacyName.class);
        if (legacyName == null) {
            return Collections.singletonList(value);
        }
        var alternates = legacyName.alternate();
        if (alternates.length == 0) {
            return Collections.singletonList(value);
        }
        var names = new ArrayList<String>(alternates.length + 1);
        names.add(value);
        for (var alternate : alternates) {
            names.add(alternate);
        }
        return names;
    }

    @NotNull
    private Map<String, BoundField> getBoundFields(@NotNull Gson context, @NotNull TypeToken<?> type, @NotNull Class<?> raw) {
        var result = new LinkedHashMap<String, BoundField>();
        if (raw.isInterface()) {
            return result;
        }
        var declaredType = type.getType();
        while (raw != Object.class) {
            var fields = raw.getDeclaredFields();
            for (var field : fields) {
                boolean serialize = excludeField(field, true);
                boolean deserialize = excludeField(field, false);
                if (!serialize && !deserialize) {
                  continue;
                }
                field.setAccessible(true);
                var fieldType = $Gson$Types.resolve(type.getType(), raw, field.getGenericType());
                var fieldNames = getFieldNames(field);
                var previous = (BoundField) null;
                for (int i = 0, l = fieldNames.size(); i < l; ++i) {
                    var name = fieldNames.get(i);
                    if (i != 0) {
                        serialize = false;
                    }
                    var replaced = result.put(name, createBoundField(context, field, name, TypeToken.get(fieldType), serialize, deserialize));
                    if (previous == null) {
                        previous = replaced;
                    }
                }
                if (previous != null) {
                    throw new IllegalArgumentException(declaredType + " declares multiple JSON fields named " + previous.name);
                }
            }
            type = TypeToken.get($Gson$Types.resolve(type.getType(), raw, raw.getGenericSuperclass()));
            raw = type.getRawType();
        }
        return result;
    }

    @NotNull
    private BoundField createBoundField(final Gson context, final Field field, final String name, final TypeToken<?> fieldType, boolean serialize, boolean deserialize) {
        var isPrimitive = Primitives.isPrimitive(fieldType.getRawType());
        var typeAdapter = context.getAdapter(fieldType);
        return new BoundField(name, serialize, deserialize) {     

            @Override
            protected boolean writeField(@NotNull Object value) throws IOException, IllegalAccessException {
                if (!serialized) {
                    return false;
                }
                var fieldValue = field.get(value);
                return fieldValue != value;
            }

            @Override
            @SuppressWarnings({ "unchecked", "rawtypes" })
            protected void write(@NotNull JsonWriter writer, @NotNull Object value) throws IOException, IllegalAccessException {
                try {
                    ((TypeAdapter) RUNTIME_CONSTRUCTOR.newInstance(context, typeAdapter, fieldType.getType())).write(writer, field.get(value));
                } catch (ReflectiveOperationException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected void read(@NotNull JsonReader reader, @NotNull Object value) throws IOException, IllegalAccessException {
                var fieldValue = typeAdapter.read(reader);
                if (fieldValue != null || !isPrimitive) {
                    field.set(value, fieldValue);
                }
            }
        };
    }
  
    private static abstract class BoundField {

        final String name;
        final boolean serialized;
        final boolean deserialized;
    
        protected BoundField(String name, boolean serialized, boolean deserialized) {
            this.name = name;
            this.serialized = serialized;
            this.deserialized = deserialized;
        }

        protected abstract boolean writeField(@NotNull Object value) throws IOException, IllegalAccessException;
    
        protected abstract void write(@NotNull JsonWriter writer, @NotNull Object value) throws IOException, IllegalAccessException;

        protected abstract void read(@NotNull JsonReader reader, @NotNull Object value) throws IOException, IllegalAccessException;
    }
  
    public static final class Adapter<T> extends TypeAdapter<T> {

        private final ObjectConstructor<T> constructor;
        private final Map<String, BoundField> boundFields;
    
        private Adapter(@NotNull ObjectConstructor<T> constructor, @NotNull Map<String, BoundField> boundFields) {
            this.constructor = constructor;
            this.boundFields = boundFields;
        }
  
        @Override
        public T read(@NotNull JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            T instance = constructor.construct();
            try {
                in.beginObject();
                while (in.hasNext()) {
                    var name = in.nextName();
                    var field = boundFields.get(name);
                    if (field == null || !field.deserialized) {
                        in.skipValue();
                    } else {
                        field.read(in, instance);
                    }
                }
            } catch (IllegalStateException e) {
                throw new JsonSyntaxException(e);
            } catch (IllegalAccessException e) {
                throw new AssertionError(e);
            }
            in.endObject();
            return instance;
        }
  
        @Override
        public void write(@NotNull JsonWriter out, @Nullable T value) throws IOException {
            if (value == null) {
                out.nullValue();
                return;
            }
            out.beginObject();
            try {
                for (var boundField : boundFields.values()) {
                    if (boundField.writeField(value)) {
                        out.name(boundField.name);
                        boundField.write(out, value);
                    }
                }
            } catch (IllegalAccessException e) {
                throw new AssertionError(e);
            }
            out.endObject();
        }
    }
}