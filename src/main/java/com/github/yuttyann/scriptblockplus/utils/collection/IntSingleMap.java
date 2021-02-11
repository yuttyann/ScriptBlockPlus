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

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * ScriptBlockPlus IntSingleMap クラス
 * @param <V> 値の型
 * @author yuttyann44581
 */
public final class IntSingleMap<V> implements IntMap<V> {

    private V value;

    @Override
    public int size() {
        return value == null ? 0 : 1;
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean containsKey(@Nullable Object key) {
        return true;
    }

    @Override
    public boolean containsValue(@Nullable Object value) {
        return Objects.equals(this.value, value);
    }

    @Override
    @Nullable
    public V get(@Nullable Object key) {
        return value;
    }

    @Override
    @Nullable
    public V put(@Nullable Integer key, @Nullable V value) {
        V old = this.value;
        this.value = value;
        return old;
    }

    @Override
    @Nullable
    public V remove(@Nullable Object key) {
        V old = this.value;
        this.value = null;
        return old;
    }

    @Override
    public void putAll(@NotNull Map<? extends Integer, ? extends V> m) {
        if (m.isEmpty()) {
            return;
        }
        this.value = m.values().iterator().next();
    }

    @Override
    public void clear() {
        this.value = null;
    }

    @Override
    @NotNull
    public Set<Integer> keySet() {
        return Collections.emptySet();
    }

    @Override
    @NotNull
    public Collection<V> values() {
        return Collections.singleton(value);
    }

    @Override
    @NotNull
    public Set<Entry<Integer, V>> entrySet() {
        var entry = new Entry<Integer, V>() {

            @Override
            public Integer getKey() {
                return 0;
            }

            @Override
            public V getValue() {
                return value;
            }

            @Override
            public V setValue(V value) {
                V old = IntSingleMap.this.value;
                IntSingleMap.this.value = value;
                return old;
            }
            
        };
        return Collections.singleton(entry);
    }

    @Override
    @Nullable
    public V get(int key) {
        return value;
    }

    @Override
    @Nullable
    public V put(int key, V value) {
        V old = this.value;
        this.value = value;
        return old;
    }

    @Override
    @Nullable
    public V remove(int key) {
        V old = this.value;
        this.value = null;
        return old;
    }

    @Override
    public boolean containsKey(int key) {
        return true;
    }

    @Override
    @NotNull
    public Iterable<IntEntry<V>> iterable() {
        var entry = new IntEntry<V>() {

            @Override
            public int key() {
                return 0;
            }

            @Override
            public V value() {
                return value;
            }

            @Override
            public void setValue(V value) {
                IntSingleMap.this.value = value;
            }
        };
        return Collections.singleton(entry);
    }
}
