package com.github.yuttyann.scriptblockplus.utils.reflect.matcher;

import static org.apache.commons.lang3.ArrayUtils.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.github.yuttyann.scriptblockplus.utils.Utils;

public final class MethodMatcher extends AbstractReflectionMatcher<Method> {

    @NotNull
    public static MethodStore store() {
        return METHOD_STORE;
    }

    @NotNull
    public static MethodMatcher of(@NotNull String className) throws IllegalArgumentException {
        return of(Utils.getClassForName(className));
    }

    @NotNull
    public static MethodMatcher of(@NotNull Class<?> targetType) {
        return new MethodMatcher(targetType);
    }

    Class<?> returnType;
    Class<?>[] parameterTypes;

    private MethodMatcher(@NotNull Class<?> targetType) {
        super(targetType, Method.class);
    }

    @NotNull
    public DeepMethodMatcher deep() {
        return new DeepMethodMatcher(this);
    }

    @Override
    public boolean isMethod() {
        return true;
    }

    @Override
    @NotNull
    public MethodMatcher modifiers(@Nullable int... modifiers) {
        super.modifiers(modifiers);
        return this;
    }

    @Override
    @NotNull
    public MethodMatcher name(@Nullable String name) {
        super.name(name);
        return this;
    }

    @Override
    @NotNull
    public MethodMatcher nameFilter(@Nullable Predicate<String> nameFilter) {
        super.nameFilter(nameFilter);
        return this;
    }

    @Override
    @NotNull
    public MethodMatcher annotation(@Nullable Class<? extends Annotation> annotation) {
        super.annotation(annotation);
        return this;
    }

    @Override
    @NotNull
    public MethodMatcher annotationFilter(@Nullable Predicate<Class<? extends Annotation>> annotationFilter) {
        super.annotationFilter(annotationFilter);
        return this;
    }

    @NotNull
    public MethodMatcher returnType(@Nullable String className) throws IllegalArgumentException {
        return returnType(className == null ? null : Utils.getClassForName(className));
    }

    @NotNull
    public MethodMatcher returnType(@Nullable Class<?> returnType) {
        this.returnType = returnType;
        return this;
    }

    @Nullable
    public Class<?> returnType() {
        return returnType;
    }

    @NotNull
    public MethodMatcher emptyParameterTypes() {
        return parameterTypes(EMPTY_CLASS_ARRAY);
    }

    @NotNull
    public MethodMatcher parameterTypes(@Nullable String... classNames) throws IllegalArgumentException {
        if (classNames == null) {
            return parameterTypes((Class<?>[]) null);
        } else if (classNames.length == 0) {
            return emptyParameterTypes();
        }
        var parameterTypes = new Class<?>[classNames.length];
        for (int i = 0, l = classNames.length; i < l; i++) {
            parameterTypes[i++] = Utils.getClassForName(classNames[i]);
        }
        return this;
    }

    @NotNull
    public MethodMatcher parameterTypes(@Nullable Class<?>... parameterTypes) {
        this.parameterTypes = parameterTypes;
        return this;
    }

    @Nullable
    public Class<?>[] parameterTypes() {
        return parameterTypes;
    }

    @NotNull
    MethodStoreImpl link() {
        return METHOD_STORE;
    }

    @Override
    @NotNull
    Method[] newArray(int size) {
        return Math.max(size, 0) == 0 ? EMPTY_METHOD_ARRAY : new Method[size];
    }

    @Override
    @NotNull
    Method[] declaredMembers() {
        return targetType.getDeclaredMethods();
    }

    @Override
    @NotNull
    public StringBuilder toStringBuilder(@NotNull StringBuilder builder) {
        builder.append("returnType").append(':').append(returnType == null ? null : returnType.getName()).append(',');
        builder.append("parameterTypes").append(':');
        if (parameterTypes == null) {
            builder.append("null");
        } else {
            builder.append(Arrays.stream(parameterTypes).map(Class::getName).collect(Collectors.joining(",", "[", "]")));
        }
        return builder;
    }

    @Override
    boolean subEquals(@NotNull ReflectionMatcher<?> matcher) {
        if (matcher instanceof MethodMatcher) {
            var method = (MethodMatcher) matcher;
            return Objects.equals(returnType, method.returnType()) && deepEquals(parameterTypes, method.parameterTypes());
        } else if (matcher instanceof DeepMethodMatcher) {
            var method = (DeepMethodMatcher) matcher;
            return Objects.equals(returnType, method.returnType()) && deepEquals(parameterTypes, method.parameterTypes());
        }
        return false;
    }

    @Override
    boolean matches(@Nullable Method method) {
        return super.matches(method) && (returnType == null || returnType.equals(method.getReturnType())) && deepEquals(parameterTypes, method.getParameterTypes());
    }
}