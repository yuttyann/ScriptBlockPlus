package com.github.yuttyann.scriptblockplus.utils;

import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public final class StreamUtils {

	public static <T, R> R[] toArray(Collection<T> collection, Function<T, R> mapper, R[] array) {
		Objects.requireNonNull(collection);
		Objects.requireNonNull(array);
        Iterator<T> iterator = collection.iterator();
        for (int i = 0; i < collection.size(); i++) {
        	if (iterator.hasNext()) {
        		array[i] = mapper.apply(iterator.next());
        	}
        }
        return array;
	}

	public static <T> void forEach(T[] array, Consumer<T> action) {
		Objects.requireNonNull(array);
		for (T t : array) {
			action.accept(t);
		}
	}

	public static <T> void filterForEach(T[] array, Predicate<T> predicate, Consumer<T> action) {
		Objects.requireNonNull(array);
		for (T t : array) {
			if (predicate.test(t)) {
				action.accept(t);
			}
		}
	}

	public static <T> void filterForEach(Collection<T> collection, Predicate<T> predicate, Consumer<T> action) {
		Objects.requireNonNull(collection);
		for (T t : collection) {
			if (predicate.test(t)) {
				action.accept(t);
			}
		}
	}

	public static <T, R> void mapForEach(Collection<T> collection, Function<T, R> mapper, Consumer<R> action) {
		Objects.requireNonNull(collection);
		for (T t : collection) {
			action.accept(mapper.apply(t));
		}
	}

	public static <T> boolean allMatch(T[] array, Predicate<T> predicate) {
		Objects.requireNonNull(array);
		if (array.length == 0) {
			return false;
		}
		for (T t : array) {
			if (!predicate.test(t)) {
				return false;
			}
		}
		return true;
	}

	public static <T> boolean anyMatch(T[] array, Predicate<T> predicate) {
		Objects.requireNonNull(array);
		for (T t : array) {
			if (predicate.test(t)) {
				return true;
			}
		}
		return false;
	}

	public static <T> boolean anyMatch(Collection<T> collection, Predicate<T> predicate) {
		Objects.requireNonNull(collection);
		for (T t : collection) {
			if (predicate.test(t)) {
				return true;
			}
		}
		return false;
	}
}