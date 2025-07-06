package com.github.yuttyann.scriptblockplus.utils.reflect;
 
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum ClassType {
    BYTE(false, byte.class, Byte.class),
    SHORT(false, short.class, Short.class),
    INTEGER(false, int.class, Integer.class),
    LONG(false, long.class, Long.class),
    FLOAT(true, float.class, Float.class),
    DOUBLE(true, double.class, Double.class),
    BOOLEAN(false, boolean.class, Boolean.class),
    CHARACTER(false, char.class, Character.class);

    private static final Map<Class<?>, ClassType> CLASSES = new HashMap<>();

    private final boolean decimal;
    private final Class<?> primitive, reference;

    static {
        for (var type : values()) {
            CLASSES.put(type.primitive, type);
            CLASSES.put(type.reference, type);
        }
    }

    ClassType(boolean decimal, @NotNull Class<?> primitive, @NotNull Class<?> reference) {
        this.decimal = decimal;
        this.primitive = primitive;
        this.reference = reference;
    }

    public boolean isDecimal() {
        return decimal;
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
    public static ClassType fromName(@NotNull String name) {
        for (var type : values()) {
            if (name.equalsIgnoreCase(type.name())) {
                return type;
            }
        }
        return null;
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