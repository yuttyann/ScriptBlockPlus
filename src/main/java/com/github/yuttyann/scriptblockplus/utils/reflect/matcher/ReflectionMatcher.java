package com.github.yuttyann.scriptblockplus.utils.reflect.matcher;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.function.Predicate;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ReflectionMatcher<A extends AccessibleObject & Member> {

    default boolean isField() {
        return matcherType() == Field.class;
    }

    default boolean isMethod() {
        return matcherType() == Method.class;
    }

    default boolean isConstructor() {
        return matcherType() == Constructor.class;
    }

    @NotNull
    Class<?> targetType();

    @NotNull
    Class<A> matcherType();

    @NotNull
    ReflectionMatcher<A> modifiers(@Nullable int... modifiers);

    @Nullable
    int[] modifiers();

    @NotNull
    ReflectionMatcher<A> name(@Nullable String name);

    @NotNull
    ReflectionMatcher<A> nameFilter(@Nullable Predicate<String> nameFilter);

    @Nullable
    Predicate<String> nameFilter();

    @NotNull
    ReflectionMatcher<A> annotation(@Nullable Class<? extends Annotation> annotation);

    @NotNull
    ReflectionMatcher<A> annotationFilter(@Nullable Predicate<Class<? extends Annotation>> annotationFilter);

    @Nullable
    Predicate<Class<? extends Annotation>> annotationFilter();

    @NotNull
    A[] find() throws NullPointerException;

    @NotNull
    A[] find(@NotNull String storeKey) throws NullPointerException;

    @NotNull
    A[] find(@NotNull String... storeKeys) throws NullPointerException;

    @NotNull
    A[] findOrEmpty();

    @NotNull
    A[] findAbsolute(int size) throws NullPointerException, IllegalArgumentException;
    
    @NotNull
    A[] findAbsolute(@NotNull String storeKey, int size) throws NullPointerException, IllegalArgumentException;

    @NotNull
    A[] findAbsolute(@NotNull String... storeKeys) throws NullPointerException, IllegalArgumentException;

    @NotNull
    A findFirst() throws NullPointerException;

    @NotNull
    A findFirst(@NotNull String storeKey) throws NullPointerException;

    @Nullable
    A findFirstOrNull();

    interface FieldStore extends Store<Field> {
    
        void setObject(@NotNull String key, @Nullable Object value) throws NullPointerException, ReflectiveOperationException;
    
        void setObject(@NotNull String key, @Nullable Object instance, @Nullable Object value) throws NullPointerException, ReflectiveOperationException;

        @Nullable
        Object getObject(@NotNull String key) throws NullPointerException, ReflectiveOperationException;

        @Nullable
        Object getObject(@NotNull String key, @Nullable Object instance) throws NullPointerException, ReflectiveOperationException;

        void setByte(@NotNull String key, byte value) throws NullPointerException, ReflectiveOperationException;

        void setByte(@NotNull String key, @Nullable Object instance, byte value) throws NullPointerException, ReflectiveOperationException;

        byte getByte(@NotNull String key) throws NullPointerException, ReflectiveOperationException;

        byte getByte(@NotNull String key, @Nullable Object instance) throws NullPointerException, ReflectiveOperationException;

        void setChar(@NotNull String key, char value) throws NullPointerException, ReflectiveOperationException;
    
        void setChar(@NotNull String key, @Nullable Object instance, char value) throws NullPointerException, ReflectiveOperationException;
    
        char getChar(@NotNull String key) throws NullPointerException, ReflectiveOperationException;

        char getChar(@NotNull String key, @Nullable Object instance) throws NullPointerException, ReflectiveOperationException;

        void setShort(@NotNull String key, short value) throws NullPointerException, ReflectiveOperationException;
    
        void setShort(@NotNull String key, @Nullable Object instance, short value) throws NullPointerException, ReflectiveOperationException;

        short getShort(@NotNull String key) throws NullPointerException, ReflectiveOperationException;

        short getShort(@NotNull String key, @Nullable Object instance) throws NullPointerException, ReflectiveOperationException;

        void setInt(@NotNull String key, int value) throws NullPointerException, ReflectiveOperationException;

        void setInt(@NotNull String key, @Nullable Object instance, int value) throws NullPointerException, ReflectiveOperationException;

        int getInt(@NotNull String key) throws NullPointerException, ReflectiveOperationException;

        int getInt(@NotNull String key, @Nullable Object instance) throws NullPointerException, ReflectiveOperationException;

        void setLong(@NotNull String key, long value) throws NullPointerException, ReflectiveOperationException;

        void setLong(@NotNull String key, @Nullable Object instance, long value) throws NullPointerException, ReflectiveOperationException;

        long getLong(@NotNull String key) throws NullPointerException, ReflectiveOperationException;

        long getLong(@NotNull String key, @Nullable Object instance) throws NullPointerException, ReflectiveOperationException;

        void setFloat(@NotNull String key, float value) throws NullPointerException, ReflectiveOperationException;

        void setFloat(@NotNull String key, @Nullable Object instance, float value) throws NullPointerException, ReflectiveOperationException;

        float getFloat(@NotNull String key) throws NullPointerException, ReflectiveOperationException;

        float getFloat(@NotNull String key, @Nullable Object instance) throws NullPointerException, ReflectiveOperationException;

        void setDouble(@NotNull String key, double value) throws NullPointerException, ReflectiveOperationException;

        void setDouble(@NotNull String key, @Nullable Object instance, double value) throws NullPointerException, ReflectiveOperationException;

        double getDouble(@NotNull String key) throws NullPointerException, ReflectiveOperationException;

        double getDouble(@NotNull String key, @Nullable Object instance) throws NullPointerException, ReflectiveOperationException;

        void setBoolean(@NotNull String key, boolean value) throws NullPointerException, ReflectiveOperationException;

        void setBoolean(@NotNull String key, @Nullable Object instance, boolean value) throws NullPointerException, ReflectiveOperationException;

        boolean getBoolean(@NotNull String key) throws NullPointerException, ReflectiveOperationException;

        boolean getBoolean(@NotNull String key, @Nullable Object instance) throws NullPointerException, ReflectiveOperationException;
    }

    interface MethodStore extends Store<Method> {

        @Nullable
        Object invokeStatic(@NotNull String key) throws NullPointerException, ReflectiveOperationException;

        @Nullable
        Object invokeStatic(@NotNull String key, @Nullable Object... arguments) throws NullPointerException, ReflectiveOperationException;

        @Nullable
        Object invoke(@NotNull String key, @Nullable Object instance) throws NullPointerException, ReflectiveOperationException;

        @Nullable
        Object invoke(@NotNull String key, @Nullable Object instance, @Nullable Object... arguments) throws NullPointerException, ReflectiveOperationException;
    }

    interface ConstructorStore extends Store<Constructor> {

        @NotNull
        Object newInstance(@NotNull String key) throws NullPointerException, ReflectiveOperationException;

        @NotNull
        Object newInstance(@NotNull String key, @Nullable Object... arguments) throws NullPointerException, ReflectiveOperationException;
    }

    interface Store<A extends AccessibleObject & Member> {

        boolean has(@NotNull String key);

        @Nullable
        A first(@NotNull String key);

        @Nullable
        Value<A> get(@NotNull String key);

        @Nullable
        Value<A> remove(@NotNull String key);
    }

    interface Value<A extends AccessibleObject & Member> extends Iterable<A> {

        @NotNull
        A first();
    }
}