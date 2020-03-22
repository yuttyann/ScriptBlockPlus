package com.github.yuttyann.scriptblockplus.manager.auxiliary;

import com.github.yuttyann.scriptblockplus.enums.InstanceType;
import com.github.yuttyann.scriptblockplus.script.SBInstance;
import com.github.yuttyann.scriptblockplus.utils.StreamUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public abstract class AbstractConstructor<T> {

	private final List<SBConstructor<? extends T>> list = newList();

	@NotNull
	protected abstract List<SBConstructor<? extends T>> newList();

	@NotNull
	protected final List<SBConstructor<? extends T>> getConstructors() {
		return list;
	}

	public abstract void registerDefaults();

	public int size() {
		return getConstructors().size();
	}

	public boolean isEmpty() {
		return getConstructors().isEmpty();
	}

	public int indexOf(@NotNull Class<? extends T> clazz) {
		List<SBConstructor<? extends T>> constructors = getConstructors();
		for (int i = 0; i < constructors.size(); i++) {
			if (constructors.get(i).getDeclaringClass().equals(clazz)) {
				return i;
			}
		}
		return -1;
	}

	public final boolean add(@NotNull Class<? extends T> clazz) {
		return add(new SBConstructor<>(clazz));
	}

	public final boolean add(@NotNull SBInstance<? extends T> sbInstance) {
		return add(new SBConstructor<>(sbInstance));
	}

	public boolean add(@NotNull SBConstructor<? extends T> constructor) {
		if (indexOf(constructor.getDeclaringClass()) >= 0) {
			return false;
		}
		getConstructors().add(constructor);
		return true;
	}

	public final boolean add(int index, @NotNull Class<? extends T> clazz) {
		return add(index, new SBConstructor<>(clazz));
	}

	public final boolean add(int index, @NotNull SBInstance<? extends T> sbInstance) {
		return add(index, new SBConstructor<>(sbInstance));
	}

	public boolean add(int index, @NotNull SBConstructor<? extends T> constructor) {
		if (index > getConstructors().size() || indexOf(constructor.getDeclaringClass()) >= 0) {
			return false;
		}
		getConstructors().add(index, constructor);
		return true;
	}

	public final boolean remove(@NotNull Class<? extends T> clazz) {
		return remove(indexOf(clazz));
	}

	@SuppressWarnings("unchecked")
	public final boolean remove(@NotNull SBInstance<? extends T> sbInstance) {
		return remove(indexOf((Class<? extends T>) sbInstance.getClass()));
	}

	public final boolean remove(@NotNull SBConstructor<? extends T> constructor) {
		return remove(indexOf(constructor.getDeclaringClass()));
	}

	public boolean remove(int index) {
		if (index < 0 || index > getConstructors().size()) {
			return false;
		}
		getConstructors().remove(index);
		return true;
	}

	public abstract T[] newInstances();

	@NotNull
	public T[] newInstances(@NotNull T[] array, @NotNull InstanceType instanceType) {
		int i = 0;
		for (SBConstructor<? extends T> constructor : getConstructors()) {
			array[i++] = Objects.requireNonNull(constructor.newInstance(instanceType));
		}
		return array;
	}

	@NotNull
	public T newInstance(@NotNull T t, @NotNull InstanceType instanceType) {
		return newInstance(t.getClass(), instanceType);
	}

	@NotNull
	public T newInstance(@NotNull Class<?> clazz, @NotNull InstanceType instanceType) {
		for (SBConstructor<? extends T> constructor : getConstructors()) {
			if (constructor.getDeclaringClass().equals(clazz)) {
				return Objects.requireNonNull(constructor.newInstance(instanceType));
			}
		}
		throw new NullPointerException(clazz.getName() + " does not exist");
	}

	public final void forEach(@NotNull Consumer<? super T> action) {
		StreamUtils.forEach(newInstances(), action);
	}
}