package com.github.yuttyann.scriptblockplus.utils.reflect.matcher;

import static org.apache.commons.lang3.ArrayUtils.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.github.yuttyann.scriptblockplus.utils.Utils;

import io.leangen.geantyref.TypeFactory;

public final class DeepMethodMatcher extends AbstractReflectionMatcher<Method> {

    Type returnType;
    Type[] parameterTypes;

    DeepMethodMatcher(@NotNull MethodMatcher matcher) {
        super(matcher.targetType, matcher.matcherType);
        this.modifiers = matcher.modifiers;
        this.nameFilter = matcher.nameFilter;
        this.annotationFilter = matcher.annotationFilter;
        this.returnType = matcher.returnType;
        this.parameterTypes = matcher.parameterTypes;
    }

    @Override
    public boolean isMethod() {
        return true;
    }

    @Override
    @NotNull
    public DeepMethodMatcher modifiers(@Nullable int... modifiers) {
        super.modifiers(modifiers);
        return this;
    }

    @Override
    @NotNull
    public DeepMethodMatcher name(@Nullable String name) {
        super.name(name);
        return this;
    }

    @Override
    @NotNull
    public DeepMethodMatcher nameFilter(@Nullable Predicate<String> nameFilter) {
        super.nameFilter(nameFilter);
        return this;
    }

    @Override
    @NotNull
    public DeepMethodMatcher annotation(@Nullable Class<? extends Annotation> annotation) {
        super.annotation(annotation);
        return this;
    }

    @Override
    @NotNull
    public DeepMethodMatcher annotationFilter(@Nullable Predicate<Class<? extends Annotation>> annotationFilter) {
        super.annotationFilter(annotationFilter);
        return this;
    }

    @NotNull
    public DeepMethodMatcher returnType(@Nullable Class<?> returnType, @Nullable Type... arguments) {
        if (returnType == null || ArrayUtils.isEmpty(arguments)) {
            return returnType(returnType);
        }
        this.returnType = TypeFactory.parameterizedClass(returnType, arguments);
        return this;
    }

    @NotNull
    public DeepMethodMatcher returnType(@Nullable String className) throws IllegalArgumentException {
        return returnType(className == null ? null : Utils.getClassForName(className));
    }

    @NotNull
    public DeepMethodMatcher returnType(@Nullable Type returnType) {
        this.returnType = returnType;
        return this;
    }

    @Nullable
    public Type returnType() {
        return returnType;
    }

    @NotNull
    public DeepMethodMatcher emptyParameterTypes() {
        return parameterTypes(EMPTY_CLASS_ARRAY);
    }

    @NotNull
    public DeepMethodMatcher parameterTypes(@Nullable String... classNames) throws IllegalArgumentException {
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
    public DeepMethodMatcher parameterTypes(@Nullable Type... parameterTypes) {
        this.parameterTypes = parameterTypes;
        return this;
    }

    @Nullable
    public Type[] parameterTypes() {
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
        builder.append("returnType").append(':').append(returnType == null ? null : returnType.getTypeName()).append(',');
        if (parameterTypes == null) {
            builder.append("null");
        } else {
            builder.append(Arrays.stream(parameterTypes).map(Type::getTypeName).collect(Collectors.joining(",", "[", "]")));
        }
        return builder;
    }

    @Override
    boolean subEquals(@NotNull ReflectionMatcher<?> matcher) {
        if (matcher instanceof DeepMethodMatcher) {
            var method = (DeepMethodMatcher) matcher;
            return Objects.equals(returnType, method.returnType()) && deepEquals(parameterTypes, method.parameterTypes());
        } else if (matcher instanceof MethodMatcher) {
            var method = (MethodMatcher) matcher;
            return Objects.equals(returnType, method.returnType()) && deepEquals(parameterTypes, method.parameterTypes());
        }
        return false;
    }

    @Override
    boolean matches(@Nullable Method method) {
        return super.matches(method) && (returnType == null || returnType.equals(method.getGenericReturnType())) && deepEquals(parameterTypes, method.getGenericParameterTypes());
    }
}