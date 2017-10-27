package com.github.yuttyann.scriptblockplus.manager.auxiliary;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import org.apache.commons.lang.ArrayUtils;

import com.github.yuttyann.scriptblockplus.utils.StreamUtils;

public abstract class AbstractConstructor<T> {

	private T cacheInstance;
	private List<T> cacheList;
	private Class<?> genericClass;

	public abstract void registerDefaults();

	public int size() {
		return getConstructors().size();
	}

	public boolean isEmpty() {
		return getConstructors().isEmpty();
	}

	public int indexOf(Class<? extends T> clazz) {
		return getConstructors().indexOf(getConstructor(clazz));
	}

	public void add(Class<? extends T> clazz) {
		Constructor<? extends T> constructor = getConstructor(clazz);
		if (getConstructors().contains(constructor)) {
			return;
		}
		getConstructors().add(constructor);
	}

	public void add(int index, Class<? extends T> clazz) {
		Constructor<? extends T> constructor = getConstructor(clazz);
		if (getConstructors().contains(constructor)) {
			return;
		}
		getConstructors().add(index, constructor);
	}

	public void remove(Class<? extends T> clazz) {
		getConstructors().remove(getConstructor(clazz));
	}

	public void remove(int index) {
		getConstructors().remove(index);
	}

	public abstract List<Constructor<? extends T>> getConstructors();

	public final T getCacheInstance() {
		return cacheInstance;
	}

	public final List<T> getCacheList() {
		if (cacheList == null || cacheList.isEmpty()) {
			newInstances();
		}
		return Collections.unmodifiableList(cacheList);
	}

	public T[] newInstances() {
		return newInstances(newGenericArray());
	}

	public T[] newInstances(T[] array) {
		clearCacheList();
		T[] instances = array;
		try {
			int i = 0;
			for (Constructor<? extends T> constructor : getConstructors()) {
				cacheList.add(instances[i++] = constructor.newInstance(ArrayUtils.EMPTY_OBJECT_ARRAY));
			}
		} catch (ReflectiveOperationException e) {
			e.printStackTrace();
		}
		return instances;
	}

	public <R extends T> T newInstance(R t) {
		return newInstance(t.getClass());
	}

	public T newInstance(Class<?> clazz) {
		clearCacheInstance();
		try {
			for (Constructor<? extends T> constructor : getConstructors()) {
				if (clazz == constructor.getDeclaringClass()) {
					return cacheInstance = constructor.newInstance(ArrayUtils.EMPTY_OBJECT_ARRAY);
				}
			}
		} catch (ReflectiveOperationException e) {
			e.printStackTrace();
		}
		return null;
	}

	public final void forEach(Consumer<? super T> action) {
		StreamUtils.forEach(newInstances(), action);
	}

	public final void forEachCache(Consumer<? super T> action) {
		getCacheList().forEach(action);
	}

	protected final void clearCacheInstance() {
		cacheInstance = null;
	}

	protected final void clearCacheList() {
		clearCacheInstance();
		cacheList = new ArrayList<T>(getConstructors().size());
	}

	private T[] newGenericArray() {
		try {
			if (genericClass == null) {
				Type type = getClass().getGenericSuperclass();
				Type[] types = ((ParameterizedType) type).getActualTypeArguments();
				genericClass = Class.forName(types[0].getTypeName());
			}
			@SuppressWarnings("unchecked")
			T[] array = (T[]) Array.newInstance(genericClass, getConstructors().size());
			return array;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private <R extends T> Constructor<R> getConstructor(Class<R> clazz) {
		try {
			Constructor<R> constructor = clazz.getDeclaredConstructor();
			constructor.setAccessible(true);
			return constructor;
		} catch (ReflectiveOperationException e) {
			e.printStackTrace();
		}
		return null;
	}
}