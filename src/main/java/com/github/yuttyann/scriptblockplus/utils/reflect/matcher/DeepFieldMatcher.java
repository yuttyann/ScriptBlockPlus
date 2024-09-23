package com.github.yuttyann.scriptblockplus.utils.reflect.matcher;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.function.Predicate;

import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.github.yuttyann.scriptblockplus.utils.Utils;

import io.leangen.geantyref.TypeFactory;

public final class DeepFieldMatcher extends AbstractReflectionMatcher<Field> {

    Type fieldType;
    boolean usePrimitive;

    DeepFieldMatcher(@NotNull FieldMatcher matcher) {
        super(matcher.targetType, matcher.matcherType);
        this.modifiers = matcher.modifiers;
        this.nameFilter = matcher.nameFilter;
        this.annotationFilter = matcher.annotationFilter;
        this.fieldType = matcher.fieldType;
        this.usePrimitive = matcher.usePrimitive;
    }

    @Override
    public boolean isField() {
        return true;
    }

    @Override
    @NotNull
    public DeepFieldMatcher modifiers(@Nullable int... modifiers) {
        super.modifiers(modifiers);
        return this;
    }

    @Override
    @NotNull
    public DeepFieldMatcher name(@Nullable String name) {
        super.name(name);
        return this;
    }

    @Override
    @NotNull
    public DeepFieldMatcher nameFilter(@Nullable Predicate<String> nameFilter) {
        super.nameFilter(nameFilter);
        return this;
    }

    @Override
    @NotNull
    public DeepFieldMatcher annotation(@Nullable Class<? extends Annotation> annotation) {
        super.annotation(annotation);
        return this;
    }

    @Override
    @NotNull
    public DeepFieldMatcher annotationFilter(@Nullable Predicate<Class<? extends Annotation>> annotationFilter) {
        super.annotationFilter(annotationFilter);
        return this;
    }

    @NotNull
    public DeepFieldMatcher fieldType(@Nullable Class<?> fieldType, @Nullable Type... arguments) {
        if (fieldType == null || ArrayUtils.isEmpty(arguments)) {
            return fieldType(fieldType);
        }
        this.fieldType = TypeFactory.parameterizedClass(fieldType, arguments);
        return this;
    }

    @NotNull
    public DeepFieldMatcher fieldType(@Nullable String className) {
        return fieldType(className == null ? null : Utils.getClassForName(className));
    }

    @NotNull
    public DeepFieldMatcher fieldType(@Nullable Type fieldType) {
        this.fieldType = fieldType;
        return this;
    }

    @Nullable
    public Type fieldType() {
        return fieldType;
    }

    @NotNull
    public DeepFieldMatcher usePrimitive(boolean usePrimitive) {
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
        return builder.append("fieldType").append(':').append(fieldType == null ? null : fieldType.getTypeName()).append(",usePrimitive").append(':').append(usePrimitive);
    }

    @Override
    boolean subEquals(@NotNull ReflectionMatcher<?> matcher) {
        if (matcher instanceof DeepFieldMatcher) {
            return Objects.equals(fieldType, ((DeepFieldMatcher) matcher).fieldType());
        } else if (matcher instanceof FieldMatcher) {
            return Objects.equals(fieldType, ((FieldMatcher) matcher).fieldType());
        }
        return false;
    }

    @Override
    boolean matches(@Nullable Field field) {
        if (!usePrimitive && field.getType().isPrimitive()) {
            return false;
        }
        return super.matches(field) && (fieldType == null || fieldType.equals(field.getGenericType()));
    }
}