package com.github.yuttyann.scriptblockplus.utils.reflect.matcher;

import static com.google.common.base.Preconditions.*;
import static org.apache.commons.lang3.ArrayUtils.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.function.Predicate;

import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.github.yuttyann.scriptblockplus.utils.Utils;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterators;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

abstract class AbstractReflectionMatcher<A extends AccessibleObject & Member> implements ReflectionMatcher<A> {

    static final FieldStoreImpl FIELD_STORE = new FieldStoreImpl();
    static final MethodStoreImpl METHOD_STORE = new MethodStoreImpl();
    static final ConstructorStoreImpl CONSTRUCTOR_STORE = new ConstructorStoreImpl();

    static final Field[] EMPTY_FIELD_ARRAY = new Field[0];
    static final Method[] EMPTY_METHOD_ARRAY = new Method[0];
    static final Constructor[] EMPTY_CONSTRUCTOR_ARRAY = new Constructor[0];

    final Class<?> targetType;
    final Class<A> matcherType;

    int[] modifiers;
    Predicate<String> nameFilter;
    Predicate<Class<? extends Annotation>> annotationFilter;

    AbstractReflectionMatcher(@NotNull Class<?> targetType, @NotNull Class<A> matcherType) {
        this.targetType = checkNotNull(targetType);
        this.matcherType = checkNotNull(matcherType);
    }

    @Override
    @NotNull
    public final Class<?> targetType() {
        return targetType;
    }

    @Override
    @NotNull
    public final Class<A> matcherType() {
        return matcherType;
    }

    @Override
    @NotNull
    public ReflectionMatcher<A> modifiers(@Nullable int... modifiers) {
        this.modifiers = modifiers;
        return this;
    }

    @Override
    @Nullable
    public final int[] modifiers() {
        return modifiers;
    }

    @Override
    @NotNull
    public ReflectionMatcher<A> name(@Nullable String name) {
        return nameFilter(name == null ? null : name::equals);
    }

    @Override
    @NotNull
    public ReflectionMatcher<A> nameFilter(@Nullable Predicate<String> nameFilter) {
        this.nameFilter = nameFilter;
        return this;
    }

    @Override
    @Nullable
    public Predicate<String> nameFilter() {
        return nameFilter;
    }

    @Override
    @NotNull
    public ReflectionMatcher<A> annotation(@Nullable Class<? extends Annotation> annotation) {
        return annotationFilter(annotation == null ? null : annotation::equals);
    }

    @Override
    @NotNull
    public ReflectionMatcher<A> annotationFilter(@Nullable Predicate<Class<? extends Annotation>> annotationFilter) {
        this.annotationFilter = annotationFilter;
        return this;
    }

    @Override
    @Nullable
    public Predicate<Class<? extends Annotation>> annotationFilter() {
        return annotationFilter;
    }

    @Override
    @NotNull
    public A[] find() throws NullPointerException {
        var members = findOrEmpty();
        if (members.length == 0) {
            throw new NullPointerException(toString());
        }
        return members;
    }

    @Override
    @NotNull
    public A[] find(@NotNull String storeKey) throws NullPointerException {
        var members = find();
        link().set(storeKey, new ValuesImpl<>(members));
        return members;
    }

    @Override
    @NotNull
    public A[] find(@NotNull String... storeKeys) throws NullPointerException {
        if (checkNotNull(storeKeys).length <= 0) {
            throw new IllegalArgumentException("Size cannot be less than 0");
        }
        var members = find();
        for (int i = 0, l = Math.min(storeKeys.length, members.length); i < l; i++) {
            if (storeKeys[i] == null) {
                continue;
            }
            link().set(storeKeys[i], new ValueImpl<>(members[i]));
        }
        return members;
    }

    @Override
    @NotNull
    public A[] findOrEmpty() {
        var members = new ObjectArrayList<A>();
        for (var member : declaredMembers()) {
            if (matches(member)) {
                member.setAccessible(true);
                members.add(member);
            }
        }
        return members.toArray(newArray(0));
    }

    @Override
    @NotNull
    public A[] findAbsolute(int size) throws NullPointerException, IllegalArgumentException {
        if (size <= 0) {
            throw new IllegalArgumentException("Size cannot be less than 0");
        }
        var count = 0;
        var members = newArray(size);
        for (var member : declaredMembers()) {
            if (matches(member)) {
                member.setAccessible(true);
                members[count] = member;
                if (++count >= members.length) {
                    break;
                }
            }
        }
        if (count == 0) {
            throw new NullPointerException(toString());
        } else if (count != members.length) {
            throw new IllegalArgumentException("The number of fields matched does not match the number specified: " + toString());
        }
        return members;
    }

    @Override
    @NotNull
    public A[] findAbsolute(@NotNull String storeKey, int size) throws NullPointerException, IllegalArgumentException {
        var members = findAbsolute(size);
        link().set(storeKey, new ValuesImpl<>(members));
        return members;
    }

    @Override
    @NotNull
    public A[] findAbsolute(@NotNull String... storeKeys) throws NullPointerException, IllegalArgumentException {
        if (checkNotNull(storeKeys).length <= 0) {
            throw new IllegalArgumentException("Size cannot be less than 0");
        }
        var members = findAbsolute(storeKeys.length);
        for (int i = 0, l = members.length; i < l; i++) {
            if (storeKeys[i] == null) {
                continue;
            }
            link().set(storeKeys[i], new ValueImpl<>(members[i]));
        }
        return members;
    }

    @Override
    @NotNull
    public A findFirst() throws NullPointerException {
        var member = findFirstOrNull();
        if (member == null) {
            throw new NullPointerException(toString());
        }
        return member;
    }

    @Override
    @NotNull
    public A findFirst(@NotNull String storeKey) throws NullPointerException {
        var member = findFirst();
        link().set(storeKey, new ValueImpl<>(member));
        return member;
    }

    @Override
    @Nullable
    public A findFirstOrNull() {
        for (var member : declaredMembers()) {
            if (matches(member)) {
                member.setAccessible(true);
                return member;
            }
        }
        return null;
    }

    @NotNull
    abstract StoreImpl<A> link();

    @NotNull
    abstract A[] newArray(int size);

    @NotNull
    abstract A[] declaredMembers();

    @NotNull
    abstract StringBuilder toStringBuilder(@NotNull StringBuilder builder);

    @Override
    @NotNull
    public final String toString() {
        var builder = new StringBuilder(getClass().getName()).append('[');
        builder.append("targetType:").append(targetType.getName()).append(',').append(' ');
        builder.append("matcherType:").append(matcherType.getName()).append(',').append(' ');
        toStringBuilder(builder).append(']').append(',').append(' ');
        builder.append("modifiers:");
        if (ArrayUtils.isEmpty(modifiers)) {
            builder.append("null");
        } else {
            var joiner = new StringJoiner(", ", "[", "]");
            for (var modifier : modifiers) {
                joiner.add(modifier < 0 ? "-" + Modifier.toString(-modifier) : Modifier.toString(modifier));
            }
            builder.append(joiner);
        }
        return builder.toString();
    }

    @Override
    public final int hashCode() {
        return matcherType.getName().hashCode() ^ targetType.getName().hashCode();
    }

    @Override
    public final boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof ReflectionMatcher) {
            var matcher = (ReflectionMatcher<?>) obj;
            return matcherType.equals(matcher.matcherType()) && targetType.equals(matcher.targetType()) && deepEquals(modifiers, matcher.modifiers()) && subEquals(matcher);
        }
        return false;
    }

    boolean deepEquals(@Nullable Object a, @Nullable Object b) {
        if (a == null || a == b) {
            return true;
        } else if (b == null) {
            return false;
        } else if (a.getClass().isArray()) {
            if (a instanceof Object[] && b instanceof Object[]) {
                return Arrays.equals((Object[]) a, (Object[]) b);
            } else if (a instanceof byte[] && b instanceof byte[]) {
                return Arrays.equals((byte[]) a, (byte[]) b);
            } else if (a instanceof short[] && b instanceof short[]) {
                return Arrays.equals((short[]) a, (short[]) b);
            } else if (a instanceof char[] && b instanceof char[]) {
                return Arrays.equals((char[]) a, (char[]) b);
            } else if (a instanceof int[] && b instanceof int[]) {
                return Arrays.equals((int[]) a, (int[]) b);
            } else if (a instanceof long[] && b instanceof long[]) {
                return Arrays.equals((long[]) a, (long[]) b);
            } else if (a instanceof float[] && b instanceof float[]) {
                return Arrays.equals((float[]) a, (float[]) b);
            } else if (a instanceof double[] && b instanceof double[]) {
                return Arrays.equals((double[]) a, (double[]) b);
            } else if (a instanceof boolean[] && b instanceof boolean[]) {
                return Arrays.equals((boolean[]) a, (boolean[]) b);
            }
            return false;
        }
        return Objects.equals(a, b);
    }

    boolean subEquals(@NotNull ReflectionMatcher<?> matcher) {
        return true;
    }

    boolean matches(@NotNull A member) {
        return matcherType.equals(member.getClass()) && Utils.isSupported(targetType, member.getDeclaringClass()) && modifiers(member) && nameFilter(member) && annotationFilter(member);
    }

    private boolean modifiers(@NotNull A member) {
        if (ArrayUtils.isEmpty(modifiers)) {
            return true;
        }
        for (var modifier : modifiers) {
            if (modifier < 0 ? (member.getModifiers() & -modifier) != 0 : (member.getModifiers() & modifier) == 0) {
                return false;
            }
        }
        return true;
    }

    private boolean nameFilter(@NotNull A member) {
        if (nameFilter == null || Predicates.alwaysTrue().equals(nameFilter) || nameFilter.test(member.getName())) {
            return true;
        }
        return false;
    }

    private boolean annotationFilter(@NotNull A member) {
        if (annotationFilter == null || Predicates.alwaysTrue().equals(annotationFilter)) {
            return true;
        }
        for (var annotation : member.getAnnotations()) {
            if (annotationFilter.test(annotation.annotationType())) {
                return true;
            }
        }
        return false;
    }

    static class FieldStoreImpl extends StoreImpl<Field> implements FieldStore {

        @Override
        public void setObject(@NotNull String key, @Nullable Object value) throws NullPointerException, ReflectiveOperationException {
            setObject(key, null, value);
        }
    
        @Override
        public void setObject(@NotNull String key, @Nullable Object instance, @Nullable Object value) throws NullPointerException, ReflectiveOperationException {
            first(key).set(instance, value);
        }

        @Override
        @Nullable
        public Object getObject(@NotNull String key) throws NullPointerException, ReflectiveOperationException {
            return getObject(key, null);
        }

        @Override
        @Nullable
        public Object getObject(@NotNull String key, @Nullable Object instance) throws NullPointerException, ReflectiveOperationException {
            return first(key).get(instance);
        }

        @Override
        public void setByte(@NotNull String key, byte value) throws NullPointerException, ReflectiveOperationException {
            setByte(key, null, value);
        }

        @Override
        public void setByte(@NotNull String key, @Nullable Object instance, byte value) throws NullPointerException, ReflectiveOperationException {
            first(key).setByte(instance, value);
        }

        @Override
        public byte getByte(@NotNull String key) throws NullPointerException, ReflectiveOperationException {
            return getByte(key, null);
        }

        @Override
        public byte getByte(@NotNull String key, @Nullable Object instance) throws NullPointerException, ReflectiveOperationException {
            return first(key).getByte(instance);
        }

        @Override
        public void setChar(@NotNull String key, char value) throws NullPointerException, ReflectiveOperationException {
            setChar(key, null, value);
        }

        @Override
        public void setChar(@NotNull String key, @Nullable Object instance, char value) throws NullPointerException, ReflectiveOperationException {
            first(key).setChar(instance, value);
        }

        @Override
        public char getChar(@NotNull String key) throws NullPointerException, ReflectiveOperationException {
            return getChar(key, null);
        }

        @Override
        public char getChar(@NotNull String key, @Nullable Object instance) throws NullPointerException, ReflectiveOperationException {
            return first(key).getChar(instance);
        }

        @Override
        public void setShort(@NotNull String key, short value) throws NullPointerException, ReflectiveOperationException {
            setShort(key, null, value);
        }
    
        @Override
        public void setShort(@NotNull String key, @Nullable Object instance, short value) throws NullPointerException, ReflectiveOperationException {
            first(key).setShort(instance, value);
        }

        @Override
        public short getShort(@NotNull String key) throws NullPointerException, ReflectiveOperationException {
            return getShort(key, null);
        }

        @Override
        public short getShort(@NotNull String key, @Nullable Object instance) throws NullPointerException, ReflectiveOperationException {
            return first(key).getShort(instance);
        }

        @Override
        public void setInt(@NotNull String key, int value) throws NullPointerException, ReflectiveOperationException {
            setInt(key, null, value);
        }

        @Override
        public void setInt(@NotNull String key, @Nullable Object instance, int value) throws NullPointerException, ReflectiveOperationException {
            first(key).setInt(instance, value);
        }

        @Override
        public int getInt(@NotNull String key) throws NullPointerException, ReflectiveOperationException {
            return getInt(key, null);
        }

        @Override
        public int getInt(@NotNull String key, @Nullable Object instance) throws NullPointerException, ReflectiveOperationException {
            return first(key).getInt(instance);
        }

        @Override
        public void setLong(@NotNull String key, long value) throws NullPointerException, ReflectiveOperationException {
            setLong(key, null, value);
        }

        @Override
        public void setLong(@NotNull String key, @Nullable Object instance, long value) throws NullPointerException, ReflectiveOperationException {
            first(key).setLong(instance, value);
        }

        @Override
        public long getLong(@NotNull String key) throws NullPointerException, ReflectiveOperationException {
            return getLong(key, null);
        }

        @Override
        public long getLong(@NotNull String key, @Nullable Object instance) throws NullPointerException, ReflectiveOperationException {
            return first(key).getLong(instance);
        }

        @Override
        public void setFloat(@NotNull String key, float value) throws NullPointerException, ReflectiveOperationException {
            setFloat(key, null, value);
        }
    
        @Override
        public void setFloat(@NotNull String key, @Nullable Object instance, float value) throws NullPointerException, ReflectiveOperationException {
            first(key).setFloat(instance, value);
        }

        @Override
        public float getFloat(@NotNull String key) throws NullPointerException, ReflectiveOperationException {
            return getFloat(key, null);
        }

        @Override
        public float getFloat(@NotNull String key, @Nullable Object instance) throws NullPointerException, ReflectiveOperationException {
            return first(key).getFloat(instance);
        }

        @Override
        public void setDouble(@NotNull String key, double value) throws NullPointerException, ReflectiveOperationException {
            setDouble(key, null, value);
        }

        @Override
        public void setDouble(@NotNull String key, @Nullable Object instance, double value) throws NullPointerException, ReflectiveOperationException {
            first(key).setDouble(instance, value);
        }

        @Override
        public double getDouble(@NotNull String key) throws NullPointerException, ReflectiveOperationException {
            return getDouble(key, null);
        }

        @Override
        public double getDouble(@NotNull String key, @Nullable Object instance) throws NullPointerException, ReflectiveOperationException {
            return first(key).getDouble(instance);
        }

        @Override
        public void setBoolean(@NotNull String key, boolean value) throws NullPointerException, ReflectiveOperationException {
            setBoolean(key, value);
        }

        @Override
        public void setBoolean(@NotNull String key, @Nullable Object instance, boolean value) throws NullPointerException, ReflectiveOperationException {
            first(key).setBoolean(instance, value);
        }

        @Override
        public boolean getBoolean(@NotNull String key) throws NullPointerException, ReflectiveOperationException {
            return getBoolean(key, null);
        }

        @Override
        public boolean getBoolean(@NotNull String key, @Nullable Object instance) throws NullPointerException, ReflectiveOperationException {
            return first(key).getBoolean(instance);
        }
    }

    static class MethodStoreImpl extends StoreImpl<Method> implements MethodStore {

        @Override
        @Nullable
        public Object invokeStatic(@NotNull String key) throws ReflectiveOperationException {
            return invoke(key, null, EMPTY_OBJECT_ARRAY);
        }

        @Override
        @Nullable
        public Object invokeStatic(@NotNull String key, @Nullable Object... arguments) throws ReflectiveOperationException {
            return invoke(key, null, arguments);
        }

        @Override
        @Nullable
        public Object invoke(@NotNull String key, @Nullable Object instance) throws ReflectiveOperationException {
            return invoke(key, instance, EMPTY_OBJECT_ARRAY);
        }

        @Override
        @Nullable
        public Object invoke(@NotNull String key, @Nullable Object instance, @Nullable Object... arguments) throws ReflectiveOperationException {
            return first(key).invoke(instance, arguments == null ? EMPTY_OBJECT_ARRAY : arguments);
        }
    }

    static class ConstructorStoreImpl extends StoreImpl<Constructor> implements ConstructorStore {

        @Override
        @NotNull
        public Object newInstance(@NotNull String key) throws ReflectiveOperationException {
            return newInstance(key, EMPTY_OBJECT_ARRAY);
        }

        @Override
        @NotNull
        public Object newInstance(@NotNull String key, @Nullable Object... arguments) throws ReflectiveOperationException {
            return first(key).newInstance(arguments == null ? EMPTY_OBJECT_ARRAY : arguments);
        }
    }

    static abstract class StoreImpl<A extends AccessibleObject & Member> implements Store<A> {

        private final Map<String, Value<A>> inner;

        private StoreImpl() {
            this.inner = new Object2ObjectOpenHashMap<>();
        }

        @Override
        public boolean has(@NotNull String key) {
            return inner.containsKey(checkNotNull(key));
        }

        @Override
        @Nullable
        public A first(@NotNull String key) {
            var value = get(key);
            if (value != null) {
                return value.first();
            }
            return null;
        }

        @Override
        @Nullable
        public Value<A> get(@NotNull String key) {
            return inner.get(checkNotNull(key));
        }

        @Override
        @Nullable
        public Value<A> remove(@NotNull String key) {
            return inner.remove(checkNotNull(key));
        }
    
        void set(@NotNull String storeKey, @NotNull Value<A> value) {
            inner.put(checkNotNull(storeKey), checkNotNull(value));
        }
    }

    private static class ValueImpl<A extends AccessibleObject & Member> implements Value<A> {

        private final A member;

        private ValueImpl(@NotNull A member) {
            this.member = checkNotNull(member);
        }

        @Override
        @NotNull
        public A first() {
            return member;
        }

        @Override
        public Iterator<A> iterator() {
            return Iterators.singletonIterator(member);
        }
    }

    private static class ValuesImpl<A extends AccessibleObject & Member> implements Value<A> {

        private final A[] members;

        private ValuesImpl(@NotNull A[] matcher) {
            this.members = checkNotNull(matcher);
        }

        @Override
        @NotNull
        public A first() {
            return members[0];
        }

        @Override
        public Iterator<A> iterator() {
            return Iterators.forArray(members);
        }
    }
}