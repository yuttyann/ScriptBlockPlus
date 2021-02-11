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
package com.github.yuttyann.scriptblockplus.file.json;

import java.util.Collection;
import java.util.Collections;

import com.github.yuttyann.scriptblockplus.utils.collection.IntMap;
import com.google.common.base.Predicate;
import com.google.common.collect.ArrayListMultimap;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * ScriptBlockPlus SubElementMap クラス
 * <p>
 * ハッシュコードが重複した際のサブマップ
 * @param <E> エレメントの型
 * @author yuttyann44581
 */
public abstract class SubElementMap<E extends BaseElement> {
    
    private ArrayListMultimap<Integer, E> subMap;

    protected SubElementMap() { }

    protected final void subClear() {
        if (subMap != null) {
            subMap.clear();
        }
    }

    protected final int subSize() {
        return subMap == null ? 0 : subMap.size();
    }

    protected final boolean isSubEmpty() {
        return subMap == null || subMap.isEmpty();
    }

    protected final boolean isSubNotEmpty() {
        return !isSubEmpty();
    }

    protected final boolean subContainsKey(final Integer hash) {
        return isSubNotEmpty() && subMap.containsKey(hash);
    }

    protected final boolean subPut(final Integer hash, E element) {
        if (subMap == null) {
            this.subMap = ArrayListMultimap.create();
        }
        return subMap.put(hash, element);
    }

    protected final boolean subRemove(final Integer hash, @NotNull Predicate<E> filter) {
        if (!subContainsKey(hash)) {
            return false;
        }
        var list = subMap.get(hash);
        return list.isEmpty() ? false : list.removeIf(filter);
    }

    @Nullable
    protected final E subGet(final Integer hash, @NotNull Predicate<E> filter) {
        if (!subContainsKey(hash)) {
            return null;
        }
        var list = subMap.get(hash);
        if (list.size() == 1) {
            E element = list.get(0);
            return filter.test(element) ? element : null;
        }
        for (int i = 0, l = list.size(); i < l; i++) {
            E element = list.get(i);
            if (filter.test(element)) {
                return element;
            }
        }
        return null;
    }

    @NotNull
    protected final Collection<E> subValues() {
        return isSubEmpty() ? Collections.emptySet() : subMap.values();
    }

    protected final void subMapFirstShift(final Integer hash, @NotNull IntMap<E> elementMap) {
        if (!subContainsKey(hash)) {
            return;
        }
        var list = subMap.get(hash);
        if (!list.isEmpty()) {
            E element = list.get(0);
            elementMap.put(hash, element);
            list.remove(0);
        }
    }
}