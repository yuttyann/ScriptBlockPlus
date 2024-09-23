package com.github.yuttyann.scriptblockplus.utils.reflect.matcher;

import static org.apache.commons.lang3.ArrayUtils.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.github.yuttyann.scriptblockplus.utils.Utils;

public final class DeepConstructorMatcher extends AbstractReflectionMatcher<Constructor> {

    Type[] parameterTypes;

    DeepConstructorMatcher(@NotNull ConstructorMatcher matcher) {
        super(matcher.targetType, matcher.matcherType);
        this.modifiers = matcher.modifiers;
        this.nameFilter = matcher.nameFilter;
        this.annotationFilter = matcher.annotationFilter;
        this.parameterTypes = matcher.parameterTypes;
    }

    @Override
    public boolean isConstructor() {
        return true;
    }

    @Override
    @NotNull
    public DeepConstructorMatcher modifiers(@Nullable int... modifiers) {
        super.modifiers(modifiers);
        return this;
    }

    @Override
    @NotNull
    public DeepConstructorMatcher name(@Nullable String name) {
        super.name(name);
        return this;
    }

    @Override
    @NotNull
    public DeepConstructorMatcher nameFilter(@Nullable Predicate<String> nameFilter) {
        super.nameFilter(nameFilter);
        return this;
    }

    @Override
    @NotNull
    public DeepConstructorMatcher annotation(@Nullable Class<? extends Annotation> annotation) {
        super.annotation(annotation);
        return this;
    }

    @Override
    @NotNull
    public DeepConstructorMatcher annotationFilter(@Nullable Predicate<Class<? extends Annotation>> annotationFilter) {
        super.annotationFilter(annotationFilter);
        return this;
    }

    @NotNull
    public DeepConstructorMatcher emptyParameterTypes() {
        return parameterTypes(EMPTY_CLASS_ARRAY);
    }

    @NotNull
    public DeepConstructorMatcher parameterTypes(@Nullable String... classNames) throws IllegalArgumentException {
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
    public DeepConstructorMatcher parameterTypes(@Nullable Type... parameterTypes) {
        this.parameterTypes = parameterTypes;
        return this;
    }

    @Nullable
    public Type[] parameterTypes() {
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
            builder.append(Arrays.stream(parameterTypes).map(Type::getTypeName).collect(Collectors.joining(",", "[", "]")));
        }
        return builder;
    }

    @Override
    boolean subEquals(@NotNull ReflectionMatcher<?> matcher) {
        if (matcher instanceof DeepConstructorMatcher) {
            return matcher.isConstructor() && deepEquals(parameterTypes, ((DeepConstructorMatcher) matcher).parameterTypes());
        } else if (matcher instanceof ConstructorMatcher) {
            return matcher.isConstructor() && deepEquals(parameterTypes, ((ConstructorMatcher) matcher).parameterTypes());
        }
        return false;
    }

    @Override
    boolean matches(@Nullable Constructor constructor) {
        return super.matches(constructor) && deepEquals(parameterTypes, constructor.getGenericParameterTypes());
    }
}