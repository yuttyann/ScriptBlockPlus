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

    private static final Map<Class<?>, ClassType> CLASS = new HashMap<>();

    private final Class<?> primitive;
    private final Class<?> reference;

    static {
        for (ClassType type : values()) {
            CLASS.put(type.primitive, type);
            CLASS.put(type.reference, type);
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
    public static ClassType fromClass(Class<?> clazz) {
        return CLASS.get(clazz);
    }

    @NotNull
    public static Class<?> getPrimitive(@NotNull Class<?> clazz) {
        ClassType type = fromClass(clazz);
        return type == null ? clazz : type.getPrimitive();
    }

    @NotNull
    public static Class<?> getReference(@NotNull Class<?> clazz) {
        ClassType type = fromClass(clazz);
        return type == null ? clazz : type.getReference();
    }

    @NotNull
    public static Class<?>[] getPrimitive(Class<?>[] classes) {
        if (classes == null || classes.length == 0) {
            return ArrayUtils.EMPTY_CLASS_ARRAY;
        }
        int length = classes.length;
        Class<?>[] types = new Class<?>[length];
        for (int index = 0; index < length; index++) {
            types[index] = getPrimitive(classes[index]);
        }
        return types;
    }

    @NotNull
    public static Class<?>[] getReference(Class<?>[] classes) {
        if (classes == null || classes.length == 0) {
            return ArrayUtils.EMPTY_CLASS_ARRAY;
        }
        int length = classes.length;
        Class<?>[] types = new Class<?>[length];
        for (int index = 0; index < length; index++) {
            types[index] = getReference(classes[index]);
        }
        return types;
    }

    @NotNull
    public static Class<?>[] getPrimitive(Object[] objects) {
        if (objects == null || objects.length == 0) {
            return ArrayUtils.EMPTY_CLASS_ARRAY;
        }
        int length = objects.length;
        Class<?>[] types = new Class<?>[length];
        for (int index = 0; index < length; index++) {
            types[index] = getPrimitive(objects[index].getClass());
        }
        return types;
    }

    @NotNull
    public static Class<?>[] getReference(Object[] objects) {
        if (objects == null || objects.length == 0) {
            return ArrayUtils.EMPTY_CLASS_ARRAY;
        }
        int length = objects.length;
        Class<?>[] types = new Class<?>[length];
        for (int index = 0; index < length; index++) {
            types[index] = getReference(objects[index].getClass());
        }
        return types;
    }
}