/**
 * ScriptBlockPlus - Allow you to add script to any blocks.
 * Copyright (C) 2021 yuttyann44581
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <https://www.gnu.org/licenses/>.
 */
package com.github.yuttyann.scriptblockplus.utils.collection;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.RandomAccess;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * ScriptBlockPlus SingleList クラス
 * @param <E> エレメントの型
 * @author yuttyann44581
 */
public final class SingleList<E> extends AbstractList<E> implements RandomAccess, Serializable {

    private static final long serialVersionUID = -850266659953923568L;

    private int size;
    private E element;

    @Override
    public int size() {
        return size;
    }

    @NotNull
    public E get(int index) {
        Objects.checkIndex(index, size);
        return element;
    }

    private void setElement(int size, E element) {
        this.size = size;
        this.element = element;
    }

    @Override
    public boolean add(@Nullable E element) {
        setElement(1, element);
        return true;
    }

    @Override
    public void add(int index, E element) {
        add(element);
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends E> collection) {
        if (collection.isEmpty()) {
            return false;
        }
        if (collection instanceof List) {
            setElement(1, ((List<? extends E>) collection).get(0));
        } else {
            setElement(1, collection.iterator().next());
        }
        return true;
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> collection) {
        return addAll(collection);
    }

    @Override
    public E remove(int index) {
        if (index != 0) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: 1");
        }
        var old = element;
        setElement(0, null);
        return old;
    }

    @Override
    public boolean remove(@Nullable Object obj) {
        if (Objects.equals(obj, element)) {
            setElement(0, null);
            return true;
        }
        return false;
    }

    @Override
    public void forEach(@NotNull Consumer<? super E> action) {
        action.accept(element);
    }

    @Override
    public boolean removeIf(@NotNull Predicate<? super E> filter) {
        if (filter.test(element)) {
            setElement(0, null);
            return true;
        }
        return false;
    }

    @Override
    public void replaceAll(@NotNull UnaryOperator<E> operator) {
        setElement(1, operator.apply(element));
    }

    @Override
    public void sort(@NotNull Comparator<? super E> comparator) { }

    @Override
    public boolean contains(@Nullable Object obj) {
        return Objects.equals(obj, element);
    }

    @Override
    public int hashCode() {
        return 31 + Objects.hashCode(element);
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {

            private boolean hasNext = true;

            @Override
            public boolean hasNext() {
                return hasNext;
            }

            @Override
            public E next() {
                if (hasNext) {
                    hasNext = false;
                    return element;
                }
                throw new NoSuchElementException();
            }
        };
    }

    @Override
    public Spliterator<E> spliterator() {
        return new Spliterator<E>() {

            long estimate = 1;

            @Override
            public Spliterator<E> trySplit() {
                return null;
            }

            @Override
            public boolean tryAdvance(Consumer<? super E> consumer) {
                Objects.requireNonNull(consumer);
                if (estimate > 0) {
                    estimate--;
                    consumer.accept(element);
                    return true;
                }
                return false;
            }

            @Override
            public void forEachRemaining(Consumer<? super E> consumer) {
                tryAdvance(consumer);
            }

            @Override
            public long estimateSize() {
                return estimate;
            }

            @Override
            public int characteristics() {
                int value = element == null ? 0 : Spliterator.NONNULL;
                return value | Spliterator.SIZED | Spliterator.SUBSIZED | Spliterator.IMMUTABLE | Spliterator.DISTINCT | Spliterator.ORDERED;
            }
        };
    }
}