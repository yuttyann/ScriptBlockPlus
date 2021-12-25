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

import java.lang.reflect.Type;

import com.github.yuttyann.scriptblockplus.file.json.derived.element.ValueHolder;
import com.github.yuttyann.scriptblockplus.file.json.derived.element.ValueHolder.ValueType;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import org.jetbrains.annotations.NotNull;

/**
 * ScriptBlockPlus ValueHolderAdapter クラス
 * @author yuttyann44581
 */
public final class ValueHolderAdapter implements JsonSerializer<ValueHolder>, JsonDeserializer<ValueHolder> {

    @Override
    @NotNull
    public JsonElement serialize(@NotNull ValueHolder valueHolder, @NotNull Type typeOfSrc, @NotNull JsonSerializationContext context) {
        return context.serialize(valueHolder.toString());
    }

    @Override
    @NotNull
    public ValueHolder deserialize(@NotNull JsonElement json, @NotNull Type typeOfT, @NotNull JsonDeserializationContext context) throws JsonParseException {
        var text = json.getAsString();
        var index = text.indexOf(':');
        var valueType = ValueType.valueOf(text.substring(0, index).toUpperCase());
        return new ValueHolder(valueType, valueType.parse(text.substring(index + 1, text.length())));
    }
}