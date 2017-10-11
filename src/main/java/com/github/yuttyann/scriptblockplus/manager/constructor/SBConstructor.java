package com.github.yuttyann.scriptblockplus.manager.constructor;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ScriptBlockPlus 独自コンストラクタ クラス
 * <br>
 * このクラスは主にコンストラクタを予め保存しておき使用したいときにインスタンスを生成するクラスです。
 * <br>
 * また、空のインスタンスを保存していますので getCashList で取得できます。
 * @author ゆっちゃん
 */
public abstract class SBConstructor<T> {

	private List<T> cacheList;

	public void add(Class<? extends T> clazz) {
		getConstructors().add(getConstructor(clazz));
	}

	public void add(int index, Class<? extends T> clazz) {
		getConstructors().add(index, getConstructor(clazz));
	}

	public void remove(Class<? extends T> clazz) {
		getConstructors().remove(getConstructor(clazz));
	}

	public void remove(int index) {
		getConstructors().remove(index);
	}

	public int indexOf(Class<? extends T> clazz) {
		return getConstructors().indexOf(getConstructor(clazz));
	}

	public boolean isEmpty() {
		return getConstructors().isEmpty();
	}

	public final List<T> getCacheList() {
		if (cacheList == null) {
			newInstances();
		}
		return Collections.unmodifiableList(cacheList);
	}

	public abstract List<Constructor<? extends T>> getConstructors();

	public T[] newInstances() {
		return newInstances(getGenericArray());
	}

	private T[] getGenericArray() {
		try {
			Type type = getClass().getGenericSuperclass();
			String className = ((ParameterizedType) type).getActualTypeArguments()[0].getTypeName();
			@SuppressWarnings("unchecked")
			T[] t = (T[]) Array.newInstance(Class.forName(className), getConstructors().size());
			return t;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public final T[] newInstances(T[] array) {
		clearCache();
		T[] instances = array;
		try {
			int i = 0;
			for (Constructor<? extends T> constructor : getConstructors()) {
				cacheList.add(instances[i++] = constructor.newInstance());
			}
		} catch (ReflectiveOperationException e) {
			e.printStackTrace();
		}
		return instances;
	}

	public final T newInstance(T t) {
		try {
			for (Constructor<? extends T> constructor : getConstructors()) {
				if (t.getClass() == constructor.getDeclaringClass()) {
					return constructor.newInstance();
				}
			}
		} catch (ReflectiveOperationException e) {
			e.printStackTrace();
		}
		return null;
	}

	protected final List<T> clearCache() {
		if (cacheList == null) {
			cacheList = new ArrayList<T>(getConstructors().size());
		} else {
			cacheList.clear();
		}
		return cacheList;
	}

	private <R extends T> Constructor<R> getConstructor(Class<R> option) {
		try {
			Constructor<R> constructor = option.getDeclaredConstructor();
			constructor.setAccessible(true);
			return constructor;
		} catch (ReflectiveOperationException e) {
			e.printStackTrace();
		}
		return null;
	}
}