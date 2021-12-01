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
package com.github.yuttyann.scriptblockplus.enums.server.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.github.yuttyann.scriptblockplus.utils.collection.IntHashMap;
import com.github.yuttyann.scriptblockplus.utils.collection.IntMap;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * ScriptBlockPlus Reflect クラス
 * @author yuttyann44581
 */
public class ReflectMatcher {

    private static final IntMap<Map<String, Object>> REFLECT_MAP = IntHashMap.create();

    private ReflectMatcher() { }

    @Nullable
    public static Field field(@NotNull String key) {
        return (Field) getReflect(0).get(key);
    }

    @Nullable
    public static Method method(@NotNull String key) {
        return (Method) getReflect(1).get(key);
    }

    @Nullable
    public static Constructor<?> constructor(@NotNull String key) {
        return (Constructor<?>) getReflect(2).get(key);
    }

    @NotNull
    private static Map<String, Object> getReflect(final int type) {
        var value = REFLECT_MAP.get(type);
        if (value == null) {
            REFLECT_MAP.put(type, value = new HashMap<>());
        }
        return value;
    }

    /**
     * ScriptBlockPlus Builder クラス
     * @author yuttyann44581
     */
    public static final class Builder {

        private final Object[] VALUES = { -1, null, null, null, null, null };

        @NotNull
        public Builder key(@NotNull String key) {
            VALUES[1] = key; 
            return this;
        }

        @NotNull
        public Builder field(@Nullable Class<?> clazz) {
            VALUES[0] = 0;
            VALUES[2] = clazz;
            return this;
        }

        @NotNull
        public Builder method(@Nullable Class<?> clazz) {
            VALUES[0] = 1;
            VALUES[2] = clazz;
            return this;
        }

        @NotNull
        public Builder constructor(@Nullable Class<?> clazz) {
            VALUES[0] = 2;
            VALUES[2] = clazz;
            return this;
        }

        @NotNull
        public Builder name(@Nullable String name) {
            VALUES[3] = name; 
            return this;
        }

        @NotNull
        public Builder type(@Nullable Class<?> clazz) {
            VALUES[4] = clazz;
            return this;
        }

        @NotNull
        public Builder parameter(@Nullable Class<?>... classes) {
            VALUES[5] = classes;
            return this;
        }

        @NotNull
        public Builder match() {
            return match(true);
        }

        @NotNull
        public Builder match(final boolean inStatic) {
            return match(inStatic, -1);
        }

        @NotNull
        public Builder match(final boolean inStatic, final int modFilter) {
            int type = (int) VALUES[0];
            var name = VALUES[3];
            var value = (Object) null;
            var clazz = (Class<?>) Objects.requireNonNull(VALUES[2]);
            var returnType = (Class<?>) VALUES[4];
            var parameterType = (Class<?>[]) VALUES[5];
            switch (type) {
                case 0:
                    for (var field : clazz.getDeclaredFields()) {
                        if (modFilter != -1 && (field.getModifiers() & modFilter) == 0) {
                            continue;
                        }
                        if (!inStatic && Modifier.isStatic(field.getModifiers())) {
                            continue;
                        }
                        if (name != null && !field.getName().equals(name)) {
                            continue;
                        }
                        if (returnType != null && !field.getType().equals(returnType)) {
                            continue;
                        }
                        field.setAccessible(true);
                        value = field;
                        break;
                    }
                    break;
                case 1:
                    for (var method : clazz.getDeclaredMethods()) {
                        if (modFilter != -1 && (method.getModifiers() & modFilter) == 0) {
                            continue;
                        }
                        if (!inStatic && Modifier.isStatic(method.getModifiers())) {
                            continue;
                        }
                        if (name != null && !method.getName().equals(name)) {
                            continue;
                        }
                        if (returnType != null && !method.getReturnType().equals(returnType)) {
                            continue;
                        }
                        if (parameterType != null && !Arrays.equals(method.getParameterTypes(), parameterType)) {
                            continue;
                        }
                        method.setAccessible(true);
                        value = method;
                        break;
                    }
                    break;
                case 2:
                    for (var constructor : clazz.getDeclaredConstructors()) {
                        if (modFilter != -1 && (constructor.getModifiers() & modFilter) == 0) {
                            continue;
                        }
                        if (name != null && !constructor.getName().equals(name)) {
                            continue;
                        }
                        if (parameterType != null && !Arrays.equals(constructor.getParameterTypes(), parameterType)) {
                            continue;
                        }
                        constructor.setAccessible(true);
                        value = constructor;
                        break;
                    }
                    break;
                default:
                    throw new IllegalArgumentException();
            }
            if (value == null) {
                throw new NullPointerException();
            }

            // デバッグ用
            /**
            System.out.println(
                "Type(" + type + "): " + value.getClass().getSimpleName() +
                ", Key: " + (String) VALUES[1] + ", Reflect: " + value.toString()
            );
            */

            getReflect(type).put(Objects.requireNonNull((String) VALUES[1]), value);
            for (int i = 0; i < VALUES.length; i++) {
                VALUES[i] = i == 0 ? -1 : null;
            }
            return this;
        }
    }
}