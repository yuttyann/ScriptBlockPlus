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
		List<Constructor<? extends T>> constructors = getConstructors();
		for (int i = 0; i < constructors.size(); i++) {
			if (clazz == constructors.get(i).getDeclaringClass()) {
				return i;
			}
		}
		return -1;
	}

	public boolean add(Class<? extends T> clazz) {
		if (indexOf(clazz) >= 0) {
			return false;
		}
		getConstructors().add(getConstructor(clazz));
		return true;
	}

	public boolean add(int index, Class<? extends T> clazz) {
		if (index > getConstructors().size() || indexOf(clazz) >= 0) {
			return false;
		}
		getConstructors().add(index, getConstructor(clazz));
		return true;
	}

	public boolean remove(Class<? extends T> clazz) {
		if (indexOf(clazz) == -1) {
			return false;
		}
		getConstructors().remove(getConstructor(clazz));
		return true;
	}

	public boolean remove(int index) {
		if (index > getConstructors().size()) {
			return false;
		}
		getConstructors().remove(index);
		return true;
	}

	public abstract List<Constructor<? extends T>> getConstructors();

	public final T getCacheInstance() {
		return cacheInstance;
	}

	public final List<T> getCacheList() {
		return Collections.unmodifiableList(getLinkCacheList());
	}

	@Deprecated
	public final List<T> getLinkCacheList() {
		if (cacheList == null || cacheList.isEmpty()) {
			newInstances();
		}
		return cacheList;
	}

	public T[] newInstances() {
		return newInstances(newGenericArray());
	}

	public T[] newInstances(T[] array) {
		clearCacheList();
		T[] instances = array;
		try {
			int count = 0;
			Object[] empty = ArrayUtils.EMPTY_OBJECT_ARRAY;
			for (Constructor<? extends T> constructor : getConstructors()) {
				cacheList.add(instances[count++] = constructor.newInstance(empty));
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
			Object[] empty = ArrayUtils.EMPTY_OBJECT_ARRAY;
			for (Constructor<? extends T> constructor : getConstructors()) {
				if (clazz == constructor.getDeclaringClass()) {
					return cacheInstance = constructor.newInstance(empty);
				}
			}
		} catch (ReflectiveOperationException e) {
			e.printStackTrace();
		}
		return null;
	}

	public final void forEach(Consumer<? super T> action, boolean cache) {
		if (cache) {
			getLinkCacheList().forEach(action);
		} else {
			StreamUtils.forEach(newInstances(), action);
		}
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