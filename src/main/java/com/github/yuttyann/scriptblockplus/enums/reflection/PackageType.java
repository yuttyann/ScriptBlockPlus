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

import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;

import org.apache.commons.lang.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * ScriptBlockPlus PackageType 列挙型
 * @author yuttyann44581
 */
public enum PackageType {
    MJN("com.mojang.brigadier"),
    NMS("net.minecraft.server." + Utils.getPackageVersion()),
    CB("org.bukkit.craftbukkit." + Utils.getPackageVersion()),
    CB_BLOCK(CB, "block"),
    CB_CHUNKIO(CB, "chunkio"),
    CB_COMMAND(CB, "command"),
    CB_CONVERSATIONS(CB, "conversations"),
    CB_ENCHANTMENS(CB, "enchantments"),
    CB_ENTITY(CB, "entity"),
    CB_EVENT(CB, "event"),
    CB_GENERATOR(CB, "generator"),
    CB_HELP(CB, "help"),
    CB_INVENTORY(CB, "inventory"),
    CB_MAP(CB, "map"),
    CB_METADATA(CB, "metadata"),
    CB_POTION(CB, "potion"),
    CB_PROJECTILES(CB, "projectiles"),
    CB_SCHEDULER(CB, "scheduler"),
    CB_SCOREBOARD(CB, "scoreboard"),
    CB_UPDATER(CB, "updater"),
    CB_UTIL(CB, "util");

    private enum HashType {
        CLASS("class_"),
        FIELD("field_"),
        METHOD("method_"),
        CONSTRUCTOR("constructor_");

        private final String name;

        HashType(@NotNull String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public static final boolean HAS_NMS;

    static {
        boolean hasNMS;
        try {
            Class.forName(NMS + ".Entity");
            hasNMS = true;
        } catch (ClassNotFoundException e) {
            hasNMS = false;
        }
        HAS_NMS = hasNMS;
    }

    private static final Map<Integer, Object> REFLECTION_CACHE = new HashMap<>();

    private final String path;

    PackageType(@NotNull String path) {
        this.path = path;
    }

    PackageType(@NotNull PackageType parent, @NotNull String path) {
        this(parent + "." + path);
    }

    public static void clear() {
        REFLECTION_CACHE.clear();
    }

    @NotNull
    public String getPath() {
        return path;
    }

    @Override
    @NotNull
    public String toString() {
        return path;
    }

    public void setFieldValue(@NotNull String className, @NotNull String fieldName, @Nullable Object instance, @Nullable Object value) throws ReflectiveOperationException {
        getField(false, className, fieldName).set(instance, value);
    }

    public void setFieldValue(boolean declared, @NotNull String className, @NotNull String fieldName, @Nullable Object instance, @Nullable Object value) throws ReflectiveOperationException {
        if (StringUtils.isEmpty(className)) {
            className = instance.getClass().getSimpleName();
        }
        getField(declared, className, fieldName).set(instance, value);
    }

    @Nullable
    public Object getFieldValue(@NotNull String className, @NotNull String fieldName, @Nullable Object instance) throws ReflectiveOperationException {
        return getField(false, className, fieldName).get(instance);
    }

    @Nullable
    public Object getFieldValue(boolean declared, @NotNull String className, @NotNull String fieldName, @Nullable Object instance) throws ReflectiveOperationException {
        return getField(declared, className, fieldName).get(instance);
    }

    @Nullable
    public Field getField(@NotNull String className, @NotNull String fieldName) throws ReflectiveOperationException {
        return getField(false, className, fieldName);
    }

    @Nullable
    public Field getField(boolean declared, @NotNull String className, @NotNull String fieldName) throws ReflectiveOperationException {
        var hash = createHash(HashType.FIELD, className, fieldName, null);
        var field = (Field) REFLECTION_CACHE.get(hash);
        if (field == null) {
            if (declared) {
                field = getClass(className).getDeclaredField(fieldName);
                field.setAccessible(true);
            } else {
                field = getClass(className).getField(fieldName);
            }
            REFLECTION_CACHE.put(hash, field);
        }
        return field;
    }

    @Nullable
    public Object invokeMethod(@Nullable Object instance, @NotNull String className, @NotNull String methodName) throws ReflectiveOperationException {
        return invokeMethod(false, instance, className, methodName, ArrayUtils.EMPTY_OBJECT_ARRAY);
    }

    @Nullable
    public Object invokeMethod(@Nullable Object instance, @NotNull String className, @NotNull String methodName, @Nullable Object... arguments) throws ReflectiveOperationException {
        return invokeMethod(false, instance, className, methodName, arguments);
    }

    @Nullable
    public Object invokeMethod(boolean declared, @Nullable Object instance, @NotNull String className, @NotNull String methodName, @Nullable Object... arguments) throws ReflectiveOperationException {
        return invokeMethod(declared, instance, className, methodName, ClassType.getPrimitive(arguments), arguments);
    }

    @Nullable
    public Object invokeMethod(boolean declared, @Nullable Object instance, @NotNull String className, @NotNull String methodName, @Nullable Class<?>[] parameterTypes, @Nullable Object... arguments) throws ReflectiveOperationException {
        if (StringUtils.isEmpty(className)) {
            className = instance.getClass().getSimpleName();
        }
        if (arguments == null) {
            arguments = ArrayUtils.EMPTY_OBJECT_ARRAY;
        }
        return getMethod(declared, className, methodName, parameterTypes).invoke(instance, arguments);
    }

    @Nullable
    public Method getMethod(@NotNull String className, @NotNull String methodName) throws ReflectiveOperationException {
        return getMethod(false, className, methodName, ArrayUtils.EMPTY_CLASS_ARRAY);
    }

    @Nullable
    public Method getMethod(@NotNull String className, @NotNull String methodName, @Nullable Class<?>... parameterTypes) throws ReflectiveOperationException {
        return getMethod(false, className, methodName, parameterTypes);
    }

    @Nullable
    public Method getMethod(boolean declared, @NotNull String className, @NotNull String methodName, @Nullable Class<?>... parameterTypes) throws ReflectiveOperationException {
        if (parameterTypes == null) {
            parameterTypes = ArrayUtils.EMPTY_CLASS_ARRAY;
        }
        var hash = createHash(HashType.METHOD, className, methodName, parameterTypes);
        var method = (Method) REFLECTION_CACHE.get(hash);
        if (method == null) {
            if (declared) {
                method = getClass(className).getDeclaredMethod(methodName, parameterTypes);
                method.setAccessible(true);
            } else {
                method = getClass(className).getMethod(methodName, parameterTypes);
            }
            REFLECTION_CACHE.put(hash, method);
        }
        return method;
    }

    @Nullable
    public Object newInstance(@NotNull String className) throws ReflectiveOperationException {
        return newInstance(false, className, ArrayUtils.EMPTY_OBJECT_ARRAY);
    }

    @Nullable
    public Object newInstance(@NotNull String className, @Nullable Object... arguments) throws ReflectiveOperationException {
        return newInstance(false, className, arguments);
    }

    @Nullable
    public Object newInstance(boolean declared, @NotNull String className, @Nullable Object... arguments) throws ReflectiveOperationException {
        return newInstance(declared, className, ClassType.getPrimitive(arguments), arguments);
    }

    @Nullable
    public Object newInstance(boolean declared, @NotNull String className, @Nullable Class<?>[] parameterTypes, @Nullable Object... arguments) throws ReflectiveOperationException {
        if (arguments == null || arguments.length == 0) {
            return getClass(className).getConstructor(ArrayUtils.EMPTY_CLASS_ARRAY).newInstance();
        }
        return getConstructor(declared, className, parameterTypes).newInstance(arguments);
    }

    @Nullable
    public Constructor<?> getConstructor(@NotNull String className) throws ReflectiveOperationException {
        return getConstructor(false, className, ArrayUtils.EMPTY_CLASS_ARRAY);
    }

    @Nullable
    public Constructor<?> getConstructor(@NotNull String className, @Nullable Class<?>... parameterTypes) throws ReflectiveOperationException {
        return getConstructor(false, className, parameterTypes);
    }

    @Nullable
    public Constructor<?> getConstructor(boolean declared, @NotNull String className, @Nullable Class<?>... parameterTypes) throws ReflectiveOperationException {
        if (parameterTypes == null) {
            parameterTypes = ArrayUtils.EMPTY_CLASS_ARRAY;
        }
        var hash = createHash(HashType.CONSTRUCTOR, className, null, parameterTypes);
        var constructor = (Constructor<?>) REFLECTION_CACHE.get(hash);
        if (constructor == null) {
            if (declared) {
                constructor = getClass(className).getDeclaredConstructor(parameterTypes);
                constructor.setAccessible(true);
            } else {
                constructor = getClass(className).getConstructor(parameterTypes);
            }
            REFLECTION_CACHE.put(hash, constructor);
        }
        return constructor;
    }

    @Nullable
    public Enum<?> getEnumValueOf(@NotNull String className, @NotNull String name) throws ReflectiveOperationException {
        var clazz = getClass(className);
        return clazz.isEnum() ? (Enum<?>) getMethod(className, "valueOf", String.class).invoke(null, name) : null;
    }

    @Nullable
    public Class<?> getClass(@NotNull String className) throws IllegalArgumentException, ClassNotFoundException {
        if (!HAS_NMS) {
            throw new UnsupportedOperationException("NMS not found.");
        }
        if (StringUtils.isEmpty(className)) {
            throw new IllegalArgumentException();
        }
        var hash = createHash(HashType.CLASS, className, null, null);
        var clazz = (Class<?>) REFLECTION_CACHE.get(hash);
        if (clazz == null) {
            clazz = Class.forName(this + "." + className);
            REFLECTION_CACHE.put(hash, clazz);
        }
        return clazz;
    }

    @NotNull
    private Integer createHash(@NotNull HashType hashType, @NotNull String className, @Nullable String name, @Nullable Class<?>[] objects) {
        if (!HAS_NMS || StringUtils.isEmpty(className)) {
            throw new IllegalArgumentException();
        }
        int baseHash = hashType.toString().hashCode() + toString().hashCode() + className.hashCode();
        if (objects == null) {
            return Integer.valueOf(name == null ? 11 * baseHash : 21 * (baseHash + name.hashCode()));
        }
        int hash = 1;
        int prime = 31;
        hash = prime * hash + baseHash;
        hash = prime * hash + (name == null ? "null" : name).hashCode();
        hash = prime * hash + Boolean.hashCode(StringUtils.isNotEmpty(name));
        for (var object : objects) {
            hash += Objects.hashCode(object);
        }
        return Integer.valueOf(hash);
    }
}