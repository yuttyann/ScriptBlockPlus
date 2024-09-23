package com.github.yuttyann.scriptblockplus.utils.reflect;

import static com.google.common.base.Preconditions.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.github.yuttyann.scriptblockplus.utils.Utils;

public interface SimpleReflection {

    @NotNull
    String getPath();

    default void setFieldValue(@NotNull String className, @NotNull String fieldName, @Nullable Object instance, @Nullable Object value) throws ReflectiveOperationException {
        getField(false, className, fieldName).set(instance, value);
    }

    default void setFieldValue(boolean declared, @NotNull String className, @NotNull String fieldName, @Nullable Object instance, @Nullable Object value) throws ReflectiveOperationException {
        if (StringUtils.isEmpty(className)) {
            className = instance.getClass().getSimpleName();
        }
        getField(declared, className, fieldName).set(instance, value);
    }

    @Nullable
    default Object getFieldValue(@NotNull String className, @NotNull String fieldName, @Nullable Object instance) throws ReflectiveOperationException {
        return getField(false, className, fieldName).get(instance);
    }

    @Nullable
    default Object getFieldValue(boolean declared, @NotNull String className, @NotNull String fieldName, @Nullable Object instance) throws ReflectiveOperationException {
        return getField(declared, className, fieldName).get(instance);
    }

    @Nullable
    default Field getField(@NotNull String className, @NotNull String fieldName) throws ReflectiveOperationException {
        return getField(false, className, fieldName);
    }

    @Nullable
    default Field getField(boolean declared, @NotNull String className, @NotNull String fieldName) throws ReflectiveOperationException {
        var field = (Field) null;
        if (field == null) {
            if (declared) {
                field = getClass(className).getDeclaredField(fieldName);
                field.setAccessible(true);
            } else {
                field = getClass(className).getField(fieldName);
            }
        }
        return field;
    }

    @Nullable
    default Object invokeMethod(@Nullable Object instance, @NotNull String className, @NotNull String methodName) throws ReflectiveOperationException {
        return invokeMethod(false, instance, className, methodName, ArrayUtils.EMPTY_OBJECT_ARRAY);
    }

    @Nullable
    default Object invokeMethod(@Nullable Object instance, @NotNull String className, @NotNull String methodName, @Nullable Object... arguments) throws ReflectiveOperationException {
        return invokeMethod(false, instance, className, methodName, arguments);
    }

    @Nullable
    default Object invokeMethod(boolean declared, @Nullable Object instance, @NotNull String className, @NotNull String methodName, @Nullable Object... arguments) throws ReflectiveOperationException {
        return invokeMethod(declared, instance, className, methodName, ClassType.getPrimitive(arguments), arguments);
    }

    @Nullable
    default Object invokeMethod(boolean declared, @Nullable Object instance, @NotNull String className, @NotNull String methodName, @Nullable Class<?>[] parameterTypes, @Nullable Object... arguments) throws ReflectiveOperationException {
        if (StringUtils.isEmpty(className)) {
            className = instance.getClass().getSimpleName();
        }
        if (arguments == null) {
            arguments = ArrayUtils.EMPTY_OBJECT_ARRAY;
        }
        return getMethod(declared, className, methodName, parameterTypes).invoke(instance, arguments);
    }

    @Nullable
    default Method getMethod(@NotNull String className, @NotNull String methodName) throws ReflectiveOperationException {
        return getMethod(false, className, methodName, ArrayUtils.EMPTY_CLASS_ARRAY);
    }

    @Nullable
    default Method getMethod(@NotNull String className, @NotNull String methodName, @Nullable Class<?>... parameterTypes) throws ReflectiveOperationException {
        return getMethod(false, className, methodName, parameterTypes);
    }

    @Nullable
    default Method getMethod(boolean declared, @NotNull String className, @NotNull String methodName, @Nullable Class<?>... parameterTypes) throws ReflectiveOperationException {
        if (parameterTypes == null) {
            parameterTypes = ArrayUtils.EMPTY_CLASS_ARRAY;
        }
        var method = (Method) null;
        if (method == null) {
            if (declared) {
                method = getClass(className).getDeclaredMethod(methodName, parameterTypes);
                method.setAccessible(true);
            } else {
                method = getClass(className).getMethod(methodName, parameterTypes);
            }
        }
        return method;
    }

    @Nullable
    default Object newInstance(@NotNull String className) throws ReflectiveOperationException {
        return newInstance(false, className, ArrayUtils.EMPTY_OBJECT_ARRAY);
    }

    @Nullable
    default Object newInstance(@NotNull String className, @Nullable Object... arguments) throws ReflectiveOperationException {
        return newInstance(false, className, arguments);
    }

    @Nullable
    default Object newInstance(boolean declared, @NotNull String className, @Nullable Object... arguments) throws ReflectiveOperationException {
        return newInstance(declared, className, ClassType.getPrimitive(arguments), arguments);
    }

    @Nullable
    default Object newInstance(boolean declared, @NotNull String className, @Nullable Class<?>[] parameterTypes, @Nullable Object... arguments) throws ReflectiveOperationException {
        if (arguments == null || arguments.length == 0) {
            return getClass(className).getConstructor(ArrayUtils.EMPTY_CLASS_ARRAY).newInstance();
        }
        return getConstructor(declared, className, parameterTypes).newInstance(arguments);
    }

    @Nullable
    default Constructor<?> getConstructor(@NotNull String className) throws ReflectiveOperationException {
        return getConstructor(false, className, ArrayUtils.EMPTY_CLASS_ARRAY);
    }

    @Nullable
    default Constructor<?> getConstructor(@NotNull String className, @Nullable Class<?>... parameterTypes) throws ReflectiveOperationException {
        return getConstructor(false, className, parameterTypes);
    }

    @Nullable
    default Constructor<?> getConstructor(boolean declared, @NotNull String className, @Nullable Class<?>... parameterTypes) throws ReflectiveOperationException {
        if (parameterTypes == null) {
            parameterTypes = ArrayUtils.EMPTY_CLASS_ARRAY;
        }
        var constructor = (Constructor<?>) null;
        if (constructor == null) {
            if (declared) {
                constructor = getClass(className).getDeclaredConstructor(parameterTypes);
                constructor.setAccessible(true);
            } else {
                constructor = getClass(className).getConstructor(parameterTypes);
            }
        }
        return constructor;
    }

    @Nullable
    default Enum<?> getEnumValueOf(@NotNull String className, @NotNull String name) throws ReflectiveOperationException {
        var clazz = getClass(className);
        return clazz.isEnum() ? (Enum<?>) getMethod(className, "valueOf", String.class).invoke(null, name) : null;
    }

    @Nullable
    default Class<?> getClass(@NotNull String className) throws IllegalArgumentException {
        return Utils.getClassForName(getPath() + "." + checkNotNull(className));
    }
}