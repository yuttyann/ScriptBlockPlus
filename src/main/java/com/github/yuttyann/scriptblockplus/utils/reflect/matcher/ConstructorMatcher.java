package com.github.yuttyann.scriptblockplus.utils.reflect.matcher;

import static org.apache.commons.lang3.ArrayUtils.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.github.yuttyann.scriptblockplus.utils.Utils;

public final class ConstructorMatcher extends AbstractReflectionMatcher<Constructor> {

    @NotNull
    public static ConstructorStore store() {
        return CONSTRUCTOR_STORE;
    }

    @NotNull
    public static ConstructorMatcher of(@NotNull String className) throws IllegalArgumentException {
        return of(Utils.getClassForName(className));
    }

    @NotNull
    public static ConstructorMatcher of(@NotNull Class<?> targetType) {
        return new ConstructorMatcher(targetType);
    }

    Class<?>[] parameterTypes;

    private ConstructorMatcher(@NotNull Class<?> targetType) {
        super(targetType, Constructor.class);
    }

    @NotNull
    public DeepConstructorMatcher deep() {
        return new DeepConstructorMatcher(this);
    }

    @Override
    public boolean isConstructor() {
        return true;
    }

    @Override
    @NotNull
    public ConstructorMatcher modifiers(@Nullable int... modifiers) {
        super.modifiers(modifiers);
        return this;
    }

    @Override
    @NotNull
    public ConstructorMatcher name(@Nullable String name) {
        super.name(name);
        return this;
    }

    @Override
    @NotNull
    public ConstructorMatcher nameFilter(@Nullable Predicate<String> nameFilter) {
        super.nameFilter(nameFilter);
        return this;
    }

    @Override
    @NotNull
    public ConstructorMatcher annotation(@Nullable Class<? extends Annotation> annotation) {
        super.annotation(annotation);
        return this;
    }

    @Override
    @NotNull
    public ConstructorMatcher annotationFilter(@Nullable Predicate<Class<? extends Annotation>> annotationFilter) {
        super.annotationFilter(annotationFilter);
        return this;
    }

    @NotNull
    public ConstructorMatcher emptyParameterTypes() {
        return parameterTypes(EMPTY_CLASS_ARRAY);
    }

    @NotNull
    public ConstructorMatcher parameterTypes(@Nullable String... classNames) throws IllegalArgumentException {
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
    public ConstructorMatcher parameterTypes(@Nullable Class<?>... parameterTypes) {
        this.parameterTypes = parameterTypes;
        return this;
    }

    @Nullable
    public Class<?>[] parameterTypes() {
        return parameterTypes;
    }

    @NotNull
    ConstructorStoreImpl link() {
        return CONSTRUCTOR_STORE;
    }

    @Override
    @NotNull
    Constructor[] newArray(int size) {
        return Math.max(size, 0) == 0 ? EMPTY_CONSTRUCTOR_ARRAY : new Constructor[size];
    }

    @Override
    @NotNull
    Constructor[] declaredMembers() {
        return targetType.getDeclaredConstructors();
    }

    @Override
    @NotNull
    StringBuilder toStringBuilder(@NotNull StringBuilder builder) {
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
        if (matcher instanceof ConstructorMatcher) {
            return deepEquals(parameterTypes, ((ConstructorMatcher) matcher).parameterTypes());
        } else if (matcher instanceof DeepConstructorMatcher) {
            return deepEquals(parameterTypes, ((DeepConstructorMatcher) matcher).parameterTypes());
        }
        return false;
    }

    @Override
    boolean matches(@Nullable Constructor constructor) {
        return super.matches(constructor) && deepEquals(parameterTypes, constructor.getParameterTypes());
    }
}