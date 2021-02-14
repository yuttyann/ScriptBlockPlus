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

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

import com.github.yuttyann.scriptblockplus.file.json.BaseJson;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * ScriptBlockPlus CollectionType クラス
 * @author yuttyann44581
 */
@SuppressWarnings("rawtypes")
public final class CollectionType implements ParameterizedType {

    private final Type[] types;
    private final Class<? extends Collection> rawType;

    public CollectionType(Class<? extends Collection> rawType, @NotNull BaseJson<?> json) throws ClassNotFoundException {
        var type = json.getClass().getGenericSuperclass();
        var args = ((ParameterizedType) type).getActualTypeArguments();
        this.types = new Type[] { Class.forName(args[args.length - 1].getTypeName()) };
        this.rawType = rawType;
    }

    @Override
    @NotNull
    public Type[] getActualTypeArguments() {
        return types;
    }

    @Override
    @NotNull
    public Type getRawType() {
        return rawType;
    }

    @Override
    @Nullable
    public Type getOwnerType() {
        return null;
    }
}