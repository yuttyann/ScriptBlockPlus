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
import java.util.function.Predicate;

import com.github.yuttyann.scriptblockplus.utils.collection.IntMap;
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

    /**
     * コンストラクタ
     */
    protected SubElementMap() { }

    /**
     * {@link IntMap}&lt;{@link E}&gt;を取得します。
     * @return {@link IntMap}&lt;{@link E}&gt; - エレメントのマップ
     */
    protected abstract IntMap<E> getElementMap();

    /**
     * {@link ArrayListMultimap}&lt;{@link Integer}, {@link E}&gt;を取得します。
     * @return {@link ArrayListMultimap}&lt;{@link Integer}, {@link E}&gt; - サブエレメントのマップ
     */
    @NotNull
    protected final ArrayListMultimap<Integer, E> getSubElementMap() {
        return subMap == null ? this.subMap = ArrayListMultimap.create() : subMap;
    }

    /**
     * 全てのサブ要素を削除します。
     */
    protected final void subClear() {
        if (subMap != null) {
            subMap.clear();
        }
    }

    /**
     * サブマップの要素数を取得します。
     * @return {@link int} - 要素数
     */
    protected final int subSize() {
        return subMap == null ? 0 : subMap.size();
    }

    /**
     * サブマップに要素が存在しない場合に{@code true}を返します。
     * @return {@link boolean} - 要素が存在しない場合は{@code true}
     */
    protected final boolean isSubEmpty() {
        return subMap == null || subMap.isEmpty();
    }

    /**
     * サブマップに要素が存在する場合に{@code true}を返します。
     * @return {@link boolean} - 要素が存在する場合は{@code true}
     */
    protected final boolean isSubNotEmpty() {
        return !isSubEmpty();
    }

    /**
     * サブマップにキーが存在する場合に{@code true}を返します。
     * @param hash - ハッシュコード
     * @return {@link boolean} - キーが存在する場合は{@code true}
     */
    protected final boolean subContainsKey(final Integer hash) {
        return isSubNotEmpty() && subMap.containsKey(hash);
    }

    /**
     * サブマップに要素を追加します。
     * @param hash - ハッシュコード
     * @param element - エレメント
     */
    protected final void subPut(final Integer hash, E element) {
        getSubElementMap().put(hash, element);
    }

    /**
     * サブマップから要素を削除します。
     * @param hash - ハッシュコード
     * @param filter - フィルター
     * @return {@link boolean} - 削除に成功した場合は{@code true}
     */
    protected final boolean subRemove(final Integer hash, @NotNull Predicate<E> filter) {
        if (!subContainsKey(hash)) {
            return false;
        }
        var list = getSubElementMap().get(hash);
        return list.isEmpty() ? false : list.removeIf(filter);
    }

    /**
     * サブマップから要素を取得します。
     * @param hash - ハッシュコード
     * @param filter - フィルター
     * @return {@link E} - エレメント
     */
    @Nullable
    protected final E subGet(final Integer hash, @NotNull Predicate<E> filter) {
        if (!subContainsKey(hash)) {
            return null;
        }
        var list = getSubElementMap().get(hash);
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

    /**
     * サブマップの要素の一覧を取得します。
     * @return {@link Collection}&lt;{@link E}&gt; - 要素の一覧
     */
    @NotNull
    protected final Collection<E> subValues() {
        return isSubEmpty() ? Collections.emptySet() : getSubElementMap().values();
    }

    /**
     * サブマップの一番最初の要素を、メインマップに移行します。
     * @param hash - ハッシュコード
     */
    protected final void subMapFirstShift(final Integer hash) {
        if (!subContainsKey(hash)) {
            return;
        }
        var list = getSubElementMap().get(hash);
        if (!list.isEmpty()) {
            E element = list.get(0);
            getElementMap().put(hash, element);
            list.remove(0);
        }
    }
}