package com.github.yuttyann.scriptblockplus.manager;

import com.github.yuttyann.scriptblockplus.enums.InstanceType;
import com.github.yuttyann.scriptblockplus.script.SBInstance;
import org.apache.commons.lang.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.util.Objects;

/**
 * ScriptBlockPlus SBConstructor クラス
 * @param <T> コンストラクタの型
 * @author yuttyann44581
 */
@SuppressWarnings("unchecked")
public final class SBConstructor<T> {

    private SBInstance<? extends T> sbInstance;
    private Constructor<? extends T> constructor;

    public SBConstructor(@NotNull Class<? extends T> clazz) {
        this.constructor = getConstructor(Objects.requireNonNull(clazz));
    }

    public SBConstructor(@NotNull SBInstance<? extends T> sbInstance) {
        this.sbInstance = Objects.requireNonNull(sbInstance);
    }

    @NotNull
    public Class<? extends T> getDeclaringClass() {
        return sbInstance == null ? constructor.getDeclaringClass() : (Class<? extends T>) sbInstance.getClass();
    }

    @NotNull
    public T getInstance() {
        return sbInstance == null ? newInstance(InstanceType.REFLECTION) : (T) sbInstance;
    }

    @NotNull
    public T newInstance(@NotNull InstanceType instanceType) {
        switch (instanceType) {
        case SBINSTANCE:
            return sbInstance == null ? newInstance(InstanceType.REFLECTION) : sbInstance.newInstance();
        case REFLECTION:
            try {
                if (constructor == null) {
                    constructor = Objects.requireNonNull(getConstructor(getDeclaringClass()));
                }
                T t = constructor.newInstance(ArrayUtils.EMPTY_OBJECT_ARRAY);
                if (!(t instanceof SBInstance)) {
                    throw new IllegalArgumentException("newInstance: " + t.getClass().getName());
                }
                if (sbInstance == null) {
                    sbInstance = (SBInstance<? extends T>) t;
                }
                return t;
            } catch (IllegalArgumentException | ReflectiveOperationException e) {
                e.printStackTrace();
            }
        }
        throw new NullPointerException("Constructor not found.");
    }

    @Nullable
    private <R extends T> Constructor<R> getConstructor(@NotNull Class<R> clazz) {
        try {
            Constructor<R> constructor = clazz.getDeclaredConstructor(ArrayUtils.EMPTY_CLASS_ARRAY);
            constructor.setAccessible(true);
            return constructor;
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
        return null;
    }
}