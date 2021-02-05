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
package com.github.yuttyann.scriptblockplus.utils;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.IntFunction;

import org.jetbrains.annotations.NotNull;

/**
 * ScriptBlockPlus ReuseIterator クラス
 * @param <T> 値の型
 * @author yuttyann44581
 */
public class ReuseIterator<T> implements Iterator<T> {

    private final T[] array;
    private final int length;

    private int cursor;
    private boolean hasNext;

    public ReuseIterator(@NotNull Collection<T> collection, @NotNull IntFunction<T[]> generator) {
        this.array = collection.toArray(generator);
        this.length = array.length;
        hasNext();
    }

    public void reset() {
        this.cursor = 0;
        hasNext();
    }

    @Override
    public boolean hasNext() {
        return hasNext = cursor < length;
    }

    @Override
    public T next() {
        if (!hasNext) {
            throw new NoSuchElementException();
        }
        return array[cursor++];
    }
}