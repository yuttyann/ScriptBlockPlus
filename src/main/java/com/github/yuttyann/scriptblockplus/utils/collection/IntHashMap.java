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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import org.jetbrains.annotations.NotNull;

import io.netty.util.Version;
import io.netty.util.collection.IntObjectHashMap;

/**
 * ScriptBlockPlus IntHashMap クラス
 * <p>
 * Nettyがv4.1.0未満の場合は内部に{@link HashMap}を保持する{@link IntMap}が返されます。
 * @param <V> 値の型
 * @author yuttyann44581
 */
public final class IntHashMap<V> extends IntObjectHashMap<V> implements IntMap<V> {

    private static final int DEFAULT_CAPACITY = 8;
    private static final float DEFAULT_LOAD_FACTOR = 0.5F;

    private static final boolean LEGACY_NETTY;
    
    static {
        var version = Version.identify().get("netty-common").artifactVersion();
        LEGACY_NETTY = !Utils.isUpperVersion(StringUtils.removeEnd(version, ".Final"), "4.1.0");
    }

    private final Iterable<IntEntry<V>> ENTRIES = () -> new IntIterator<V>(entries().iterator());

    /**
     * コンストラクタ
     * @param initialCapacity - 初期容量
     * @param loadFactor - 負荷率
     */
    private IntHashMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    @Override
    @NotNull
    public Iterable<IntEntry<V>> iterable() {
        return ENTRIES;
    }

    /**
     * {@link IntMap}を生成します。
     * @param <V> 値の型
     * @return {@link IntMap}&lt;{@link V}&gt;
     */
    @NotNull
    public static <V> IntMap<V> create() {
        return create(DEFAULT_CAPACITY, DEFAULT_LOAD_FACTOR);
    }

    /**
     * {@link IntMap}を生成します。
     * @param <V> 値の型
     * @param initialCapacity - 初期容量
     * @return {@link IntMap}&lt;{@link V}&gt;
     */
    @NotNull
    public static <V> IntMap<V> create(int initialCapacity) {
        return create(initialCapacity, DEFAULT_LOAD_FACTOR);
    }

    /**
     * {@link IntMap}を生成します。
     * @param <V> 値の型
     * @param initialCapacity - 初期容量
     * @param loadFactor - 負荷率
     * @return {@link IntMap}&lt;{@link V}&gt;
     */
    @NotNull
    public static <V> IntMap<V> create(int initialCapacity, float loadFactor) {
        if (LEGACY_NETTY) {
            return new LegacyMap<V>(initialCapacity, loadFactor);
        }
        return new IntHashMap<V>(initialCapacity, loadFactor);
    }

    /**
    * ScriptBlockPlus LegacyMap クラス
    * Nettyのバージョンが古い場合は、これが呼ばれる。
    * @param <V> 値の型
    * @author yuttyann44581
    */
    private static final class LegacyMap<V> implements IntMap<V> {

        private final Map<Integer, V> legacyMap;
        private final Iterable<IntEntry<V>> ENTRIES = () -> new MapIterator<V>(entrySet().iterator());

        private LegacyMap(int initialCapacity, float loadFactor) {
            this.legacyMap = new HashMap<>(initialCapacity, loadFactor);
        }

        @Override
        public int size() {
            return legacyMap.size();
        }

        @Override
        public boolean isEmpty() {
            return legacyMap.isEmpty();
        }

        @Override
        public boolean containsKey(Object key) {
            return legacyMap.containsKey(key);
        }

        @Override
        public boolean containsValue(Object value) {
            return legacyMap.containsValue(value);
        }

        @Override
        public V get(Object key) {
            return legacyMap.get(key);
        }

        @Override
        public V put(Integer key, V value) {
            return legacyMap.put(key, value);
        }

        @Override
        public V remove(Object key) {
            return legacyMap.remove(key);
        }

        @Override
        public void putAll(Map<? extends Integer, ? extends V> m) {
            legacyMap.putAll(m);
        }

        @Override
        public void clear() {
            legacyMap.clear();
        }

        @Override
        public Set<Integer> keySet() {
            return legacyMap.keySet();
        }

        @Override
        public Collection<V> values() {
            return legacyMap.values();
        }

        @Override
        public Set<Entry<Integer, V>> entrySet() {
            return legacyMap.entrySet();
        }

        @Override
        public Iterable<IntEntry<V>> iterable() {
            return ENTRIES;
        }

        @Override
        public boolean containsKey(int key) {
            return legacyMap.containsKey(key);
        }

        @Override
        public V get(int key) {
            return legacyMap.get(key);
        }

        @Override
        public V put(int key, V value) {
            return legacyMap.put(key, value);
        }

        @Override
        public V remove(int key) {
            return legacyMap.remove(key);
        }
    }

    /**
    * ScriptBlockPlus IntIterator クラス
    * @param <V> 値の型
    * @author yuttyann44581
    */
    private static final class IntIterator<V> implements Iterator<IntEntry<V>> {

        private final IntEntry<V> INT_ENTRY = new IntEntry<>() {

            @Override
            public int key() {
                return entry.key();
            }

            @Override
            public V value() {
                return entry.value();
            }

            @Override
            public void setValue(V value) {
                entry.setValue(value);
            }
        };

        private final Iterator<PrimitiveEntry<V>> iterator;

        private PrimitiveEntry<V> entry;

        public IntIterator(Iterator<PrimitiveEntry<V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        @NotNull
        public IntEntry<V> next() {
            this.entry = iterator.next();
            return INT_ENTRY;
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }

    /**
    * ScriptBlockPlus MapIterator クラス
    * Nettyのバージョンが古い場合はこれが呼ばれる。
    * @param <V> 値の型
    * @author yuttyann44581
    */
    private static final class MapIterator<V> implements Iterator<IntEntry<V>> {

        private final IntEntry<V> INT_ENTRY = new IntEntry<>() {

            @Override
            public int key() {
                return entry.getKey();
            }

            @Override
            public V value() {
                return entry.getValue();
            }

            @Override
            public void setValue(V value) {
                entry.setValue(value);
            }
        };

        private final Iterator<Entry<Integer, V>> iterator;

        private Entry<Integer, V> entry;

        public MapIterator(Iterator<Entry<Integer, V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        @NotNull
        public IntEntry<V> next() {
            this.entry = iterator.next();
            return INT_ENTRY;
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }
}