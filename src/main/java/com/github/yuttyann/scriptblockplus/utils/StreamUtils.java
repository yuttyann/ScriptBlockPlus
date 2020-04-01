package com.github.yuttyann.scriptblockplus.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * ScriptBlockPlus StreamUtils クラス
 * @author yuttyann44581
 */
public final class StreamUtils {

	@NotNull
	public static <T, R> R[] toArray(@NotNull Collection<T> collection, @NotNull Function<T, R> mapper, @NotNull R[] array) {
		Objects.requireNonNull(array);
		Objects.requireNonNull(collection);
		Iterator<T> iterator = collection.iterator();
		for (int i = 0; iterator.hasNext(); i++) {
			array[i] = mapper.apply(iterator.next());
		}
		return array;
	}

	public static <T> void forEach(@NotNull T[] array, @NotNull Consumer<T> action) {
		for (T t : Objects.requireNonNull(array)) {
			action.accept(t);
		}
	}

	public static <T> void fForEach(@NotNull T[] array, @NotNull Predicate<T> filter, @NotNull Consumer<T> action) {
		forEach(array, t -> filter(t, filter, action));
	}

	public static <T> void fForEach(@NotNull Collection<T> collection, @NotNull Predicate<T> filter, @NotNull Consumer<T> action) {
		collection.forEach(t -> filter(t, filter, action));
	}

	@Nullable
	public static <T> T fOrElse(@NotNull T[] array, @NotNull Predicate<T> filter, @Nullable T other) {
		for (T t : array) {
			if (filter.test(t)) {
				return t;
			}
		}
		return other;
	}

	@Nullable
	public static <T> T fOrElse(@NotNull Collection<T> collection, @NotNull Predicate<T> filter, @Nullable T other) {
		for (T t : collection) {
			if (filter.test(t)) {
				return t;
			}
		}
		return other;
	}

	public static <T, R> void mForEach(@NotNull T[] array, @NotNull Function<T, R> mapper, @NotNull Consumer<R> action) {
		forEach(array, t -> action.accept(mapper.apply(t)));
	}

	public static <T, R> void mForEach(@NotNull Collection<T> collection, @NotNull Function<T, R> mapper, @NotNull Consumer<R> action) {
		collection.forEach(t -> action.accept(mapper.apply(t)));
	}

	public static <T> boolean allMatch(@NotNull T[] array, @NotNull Predicate<T> filter) {
		return match(array, filter, false);
	}

	public static <T> boolean allMatch(@NotNull Collection<T> collection, @NotNull Predicate<T> filter) {
		return match(collection, filter, false);
	}

	public static <T> boolean anyMatch(@NotNull T[] array, @NotNull Predicate<T> filter) {
		return match(array, filter, true);
	}

	public static <T> boolean anyMatch(@NotNull Collection<T> collection, @NotNull Predicate<T> filter) {
		return match(collection, filter, true);
	}

	public static <T> void filter(@NotNull T t, Predicate<T> filter, @NotNull Consumer<T> action) {
		if (filter.test(t)) {
			action.accept(t);
		}
	}

	private static <T> boolean match(@NotNull T[] array, @NotNull Predicate<T> filter, boolean anyMatch) {
		for (T t : array) {
			if (anyMatch == filter.test(t)) {
				return anyMatch;
			}
		}
		return !anyMatch;
	}

	private static <T> boolean match(@NotNull Collection<T> collection, @NotNull Predicate<T> filter, boolean anyMatch) {
		for (T t : collection) {
			if (anyMatch == filter.test(t)) {
				return anyMatch;
			}
		}
		return !anyMatch;
	}
}