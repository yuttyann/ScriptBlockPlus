package com.github.yuttyann.scriptblockplus.utils.reflect.matcher;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Objects;
import java.util.function.Predicate;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.github.yuttyann.scriptblockplus.utils.Utils;

public final class FieldMatcher extends AbstractReflectionMatcher<Field> {

    @NotNull
    public static FieldStore store() {
        return FIELD_STORE;
    }

    @NotNull
    public static FieldMatcher of(@NotNull String className) throws IllegalArgumentException {
        return of(Utils.getClassForName(className));
    }

    @NotNull
    public static FieldMatcher of(@NotNull Class<?> targetType) {
        return new FieldMatcher(targetType);
    }

    Class<?> fieldType;
    boolean usePrimitive;

    private FieldMatcher(@NotNull Class<?> targetType) {
        super(targetType, Field.class);
        this.usePrimitive = true;
    }

    @NotNull
    public DeepFieldMatcher deep() {
        return new DeepFieldMatcher(this);
    }

    @Override
    public boolean isField() {
        return true;
    }

    @Override
    @NotNull
    public FieldMatcher modifiers(@Nullable int... modifiers) {
        super.modifiers(modifiers);
        return this;
    }

    @Override
    @NotNull
    public FieldMatcher name(@Nullable String name) {
        super.name(name);
        return this;
    }

    @Override
    @NotNull
    public FieldMatcher nameFilter(@Nullable Predicate<String> nameFilter) {
        super.nameFilter(nameFilter);
        return this;
    }

    @Override
    @NotNull
    public FieldMatcher annotation(@Nullable Class<? extends Annotation> annotation) {
        super.annotation(annotation);
        return this;
    }

    @Override
    @NotNull
    public FieldMatcher annotationFilter(@Nullable Predicate<Class<? extends Annotation>> annotationFilter) {
        super.annotationFilter(annotationFilter);
        return this;
    }

    @NotNull
    public FieldMatcher fieldType(@Nullable String className) {
        return fieldType(className == null ? null : Utils.getClassForName(className));
    }

    @NotNull
    public FieldMatcher fieldType(@Nullable Class<?> fieldType) {
        this.fieldType = fieldType;
        return this;
    }

    @Nullable
    public Class<?> fieldType() {
        return fieldType;
    }

    @NotNull
    public FieldMatcher usePrimitive(boolean usePrimitive) {
        this.usePrimitive = usePrimitive;
        return this;
    }

    public boolean usePrimitive() {
        return usePrimitive;
    }

    @NotNull
    FieldStoreImpl link() {
        return FIELD_STORE;
    }

    @Override
    @NotNull
    Field[] newArray(int size) {
        return Math.max(size, 0) == 0 ? EMPTY_FIELD_ARRAY : new Field[size];
    }

    @Override
    @NotNull
    Field[] declaredMembers() {
        return targetType.getDeclaredFields();
    }

    @Override
    @NotNull
    StringBuilder toStringBuilder(@NotNull StringBuilder builder) {
        return builder.append("fieldType").append(':').append(fieldType == null ? null : fieldType.getName()).append(",usePrimitive").append(':').append(usePrimitive);
    }

    @Override
    boolean subEquals(@NotNull ReflectionMatcher<?> matcher) {
        if (matcher instanceof FieldMatcher) {
            return Objects.equals(fieldType, ((FieldMatcher) matcher).fieldType());
        } else if (matcher instanceof DeepFieldMatcher) {
            return Objects.equals(fieldType, ((DeepFieldMatcher) matcher).fieldType());
        }
        return true;
    }

    @Override
    boolean matches(@Nullable Field field) {
        if (!usePrimitive && field.getType().isPrimitive()) {
            return false;
        }
        return super.matches(field) && (fieldType == null || fieldType.equals(field.getType()));
    }
}
