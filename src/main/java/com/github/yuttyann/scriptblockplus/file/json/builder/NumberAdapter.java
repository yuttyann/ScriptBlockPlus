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
package com.github.yuttyann.scriptblockplus.file.json.builder;

import java.io.IOException;
import java.util.ArrayList;

import com.github.yuttyann.scriptblockplus.file.json.BaseJson;
import com.google.gson.TypeAdapter;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * ScriptBlockPlus NumberAdapter クラス
 * @author yuttyann44581
 */
public class NumberAdapter extends TypeAdapter<Object> {

    private final TypeAdapter<Object> DELEGATE = BaseJson.GSON_HOLDER.getGson().getAdapter(Object.class);

    @Override
    public void write(@NotNull JsonWriter out, @Nullable Object value) throws IOException {
        DELEGATE.write(out, value);
    }

    @Override
    @Nullable
    public Object read(@NotNull JsonReader reader) throws IOException {
        var jsonToken = reader.peek();
        switch (jsonToken) {
            case BEGIN_ARRAY: {
                var list = new ArrayList<Object>();
                reader.beginArray();
                while (reader.hasNext()) {
                    list.add(read(reader));
                }
                reader.endArray();
                return list;
            }
            case BEGIN_OBJECT: {
                var map = new LinkedTreeMap<String, Object>();
                reader.beginObject();
                while (reader.hasNext()) {
                    map.put(reader.nextName(), read(reader));
                }
                reader.endObject();
                return map;
            }
            case STRING: {
                return reader.nextString();
            }
            case NUMBER: {
                var next = reader.nextString();
                if (next.indexOf('.') != -1) {
                    return Double.parseDouble(next);
                }
                return Long.parseLong(next);
            }
            case BOOLEAN: {
                return reader.nextBoolean();
            }
            case NULL: {
                reader.nextNull();
                return null;
            }
            default:
                throw new IllegalStateException();
        }
    }
}