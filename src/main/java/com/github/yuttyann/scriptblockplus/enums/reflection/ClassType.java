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
package com.github.yuttyann.scriptblockplus.enums.reflection;

import org.apache.commons.lang.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * ScriptBlockPlus ClassType 列挙型
 * @author yuttyann44581
 */
public enum ClassType {
    BYTE(byte.class, Byte.class),
    SHORT(short.class, Short.class),
    INTEGER(int.class, Integer.class),
    LONG(long.class, Long.class),
    CHARACTER(char.class, Character.class),
    FLOAT(float.class, Float.class),
    DOUBLE(double.class, Double.class),
    BOOLEAN(boolean.class, Boolean.class);

    private static final Map<Class<?>, ClassType> CLASSES = new HashMap<>();

    private final Class<?> primitive;
    private final Class<?> reference;

    static {
        for (var type : values()) {
            CLASSES.put(type.primitive, type);
            CLASSES.put(type.reference, type);
        }
    }

    ClassType(@NotNull Class<?> primitive, @NotNull Class<?> reference) {
        this.primitive = primitive;
        this.reference = reference;
    }

    @NotNull
    public Class<?> getPrimitive() {
        return primitive;
    }

    @NotNull
    public Class<?> getReference() {
        return reference;
    }

    @Nullable
    public static ClassType fromClass(@NotNull Class<?> clazz) {
        return CLASSES.get(clazz);
    }

    @NotNull
    public static Class<?> getPrimitive(@NotNull Class<?> clazz) {
        var type = fromClass(clazz);
        return type == null ? clazz : type.getPrimitive();
    }

    @NotNull
    public static Class<?> getReference(@NotNull Class<?> clazz) {
        var type = fromClass(clazz);
        return type == null ? clazz : type.getReference();
    }

    @NotNull
    public static Class<?>[] getPrimitive(@Nullable Class<?>[] classes) {
        if (classes == null || classes.length == 0) {
            return ArrayUtils.EMPTY_CLASS_ARRAY;
        }
        var newArray = new Class<?>[classes.length];
        for (int i = 0; i < classes.length; i++) {
            newArray[i] = getPrimitive(classes[i]);
        }
        return newArray;
    }

    @NotNull
    public static Class<?>[] getReference(@Nullable Class<?>[] classes) {
        if (classes == null || classes.length == 0) {
            return ArrayUtils.EMPTY_CLASS_ARRAY;
        }
        var newArray = new Class<?>[classes.length];
        for (int i = 0; i < classes.length; i++) {
            newArray[i] = getReference(classes[i]);
        }
        return newArray;
    }

    @NotNull
    public static Class<?>[] getPrimitive(@Nullable Object[] objects) {
        if (objects == null || objects.length == 0) {
            return ArrayUtils.EMPTY_CLASS_ARRAY;
        }
        var newArray = new Class<?>[objects.length];
        for (int i = 0; i < objects.length; i++) {
            newArray[i] = getPrimitive(objects[i].getClass());
        }
        return newArray;
    }

    @NotNull
    public static Class<?>[] getReference(@Nullable Object[] objects) {
        if (objects == null || objects.length == 0) {
            return ArrayUtils.EMPTY_CLASS_ARRAY;
        }
        var newArray = new Class<?>[objects.length];
        for (int i = 0; i < objects.length; i++) {
            newArray[i] = getReference(objects[i].getClass());
        }
        return newArray;
    }
}