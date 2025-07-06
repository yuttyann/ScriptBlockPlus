package com.github.yuttyann.scriptblockplus.utils.reflect;

import org.jetbrains.annotations.NotNull;

import com.github.yuttyann.scriptblockplus.utils.reflect.matcher.ConstructorMatcher;
import com.github.yuttyann.scriptblockplus.utils.reflect.matcher.FieldMatcher;
import com.github.yuttyann.scriptblockplus.utils.reflect.matcher.MethodMatcher;
import com.github.yuttyann.scriptblockplus.utils.reflect.matcher.ReflectionMatcher.ConstructorStore;
import com.github.yuttyann.scriptblockplus.utils.reflect.matcher.ReflectionMatcher.FieldStore;
import com.github.yuttyann.scriptblockplus.utils.reflect.matcher.ReflectionMatcher.MethodStore;

public final class Reflection {

    private Reflection() { }

    @NotNull
    public static FieldMatcher field(@NotNull Class<?> targetType) {
        return FieldMatcher.of(targetType);
    }

    @NotNull
    public static FieldStore fields() {
        return FieldMatcher.store();
    }

    @NotNull
    public static MethodMatcher method(@NotNull Class<?> targetType) {
        return MethodMatcher.of(targetType);
    }

    @NotNull
    public static MethodStore methods() {
        return MethodMatcher.store();
    }

    @NotNull
    public static ConstructorMatcher construct(@NotNull Class<?> targetType) {
        return ConstructorMatcher.of(targetType);
    }

    @NotNull
    public static ConstructorStore constructs() {
        return ConstructorMatcher.store();
    }
}