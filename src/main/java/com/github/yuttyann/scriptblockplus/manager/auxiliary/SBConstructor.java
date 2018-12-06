package com.github.yuttyann.scriptblockplus.manager.auxiliary;

import java.lang.reflect.Constructor;
import java.util.Objects;

import org.apache.commons.lang.ArrayUtils;

import com.github.yuttyann.scriptblockplus.enums.InstanceType;
import com.github.yuttyann.scriptblockplus.script.SBInstance;

public final class SBConstructor<T> {

	private SBInstance<? extends T> sbInstance;
	private Constructor<? extends T> constructor;

	public SBConstructor(Class<? extends T> clazz) {
		this.constructor = getConstructor(Objects.requireNonNull(clazz));
	}

	public SBConstructor(SBInstance<? extends T> sbInstance) {
		this.sbInstance = Objects.requireNonNull(sbInstance);
	}

	public Class<? extends T> getDeclaringClass() {
		return sbInstance == null ? constructor.getDeclaringClass() : (Class<? extends T>) sbInstance.getClass();
	}

	public T getInstance() {
		return sbInstance == null ? newInstance(InstanceType.REFLECTION) : (T) sbInstance;
	}

	public T newInstance(InstanceType instanceType) {
		switch (instanceType) {
		case SBINSTANCE:
			return sbInstance == null ? newInstance(InstanceType.REFLECTION) : sbInstance.newInstance();
		case REFLECTION:
			try {
				if (constructor == null) {
					constructor = getConstructor(getDeclaringClass());
				}
				T t = constructor.newInstance(ArrayUtils.EMPTY_OBJECT_ARRAY);
				if (!(t instanceof SBInstance)) {
					throw new IllegalArgumentException("newInstance: " + t.getClass().getName());
				}
				if (sbInstance == null) {
					sbInstance = (SBInstance<? extends T>) t;
				}
				return t;
			} catch (ReflectiveOperationException e) {
				e.printStackTrace();
			}
			return null;
		default:
			return null;
		}
	}

	private <R extends T> Constructor<R> getConstructor(Class<R> clazz) {
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
