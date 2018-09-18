package com.github.yuttyann.scriptblockplus.utils;

import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public final class StreamUtils {

	public static <T, R> R[] toArray(Collection<T> collection, Function<T, R> mapper, R[] array) {
		Objects.requireNonNull(array);
		Objects.requireNonNull(collection);
        Iterator<T> iterator = collection.iterator();
        for (int i = 0; iterator.hasNext(); i++) {
        	array[i] = mapper.apply(iterator.next());
        }
        return array;
	}

	public static <T> void forEach(T[] array, Consumer<T> action) {
        for (T t : Objects.requireNonNull(array)) {
        	action.accept(t);
        }
	}

	public static <T> void fForEach(T[] array, Predicate<T> filter, Consumer<T> action) {
		forEach(array, t -> filter(t, filter, action));
	}

	public static <T> void fForEach(Collection<T> collection, Predicate<T> filter, Consumer<T> action) {
		collection.forEach(t -> filter(t, filter, action));
	}

	public static <T, R> void mForEach(T[] array, Function<T, R> mapper, Consumer<R> action) {
		forEach(array, t -> action.accept(mapper.apply(t)));
	}

	public static <T, R> void mForEach(Collection<T> collection, Function<T, R> mapper, Consumer<R> action) {
		collection.forEach(t -> action.accept(mapper.apply(t)));
	}

	public static <T> boolean allMatch(T[] array, Predicate<T> filter) {
		return match(array, filter, false);
	}

	public static <T> boolean allMatch(Collection<T> collection, Predicate<T> filter) {
		return match(collection, filter, false);
	}

	public static <T> boolean anyMatch(T[] array, Predicate<T> filter) {
		return match(array, filter, true);
	}

	public static <T> boolean anyMatch(Collection<T> collection, Predicate<T> filter) {
		return match(collection, filter, true);
	}

	public static <T> void filter(T t, Predicate<T> filter, Consumer<T> action) {
		if (filter.test(t)) {
			action.accept(t);
		}
	}

	private static <T> boolean match(T[] array, Predicate<T> filter, boolean result) {
		for (T t : array) {
			if (filter.test(t)) {
				return result;
			}
		}
		return !result;
	}

	private static <T> boolean match(Collection<T> collection, Predicate<T> filter, boolean result) {
		for (T t : collection) {
			if (filter.test(t)) {
				return result;
			}
		}
		return !result;
	}
}