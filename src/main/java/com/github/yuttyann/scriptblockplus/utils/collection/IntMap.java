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

import java.util.Iterator;
import java.util.Map;

/**
 * ScriptBlockPlus IntMap インターフェース
 * @param <V> 値の型
 * @author yuttyann44581
 */
public interface IntMap<V> extends Map<Integer, V> {

    /**
     * ScriptBlockPlus IntEntry インターフェース
     * @param <V> 値の型
     * @author yuttyann44581
     */
    public interface IntEntry<V> {

        /**
         * Gets the key for this entry.
         * @return the key corresponding to this entry.
         */
        int key();

        /**
         * Gets the value for this entry.
         * @return the value corresponding to this entry.
         */
        V value();

        /**
         * Sets the value for this entry.
         * @param value new value to be stored in this entry.
         */
        void setValue(V value);
    }

    /**
     * Gets the value in the map with the specified key.
     * @param key the key whose associated value is to be returned.
     * @return the value or {@code null} if the key was not found in the map.
     */
    V get(int key);

    /**
     * Puts the given entry into the map.
     * @param key the key of the entry.
     * @param value the value of the entry.
     * @return the previous value for this key or {@code null} if there was no previous mapping.
     */
    V put(int key, V value);
 
    /**
     * Removes the entry with the specified key.
     * @param key the key for the entry to be removed from this map.
     * @return the previous value for the key, or {@code null} if there was no mapping.
     */
    V remove(int key);
 
    /**
     * Indicates whether or not this map contains a value for the specified key.
     * @param key key whose presence in this map is to be tested.
     * @return {@code true} if this map contains a mapping for the specified key.
     */
    boolean containsKey(int key);

    /**
     * Gets an iterable to traverse over the int entries contained in this map. As an optimization,
     * the {@link IntEntry}s returned by the {@link Iterator} may change as the {@link Iterator}
     * progresses. The caller should not rely on {@link IntEntry} key/value stability.
     * @return iterable object of the mapping contained in this map.
     */
    Iterable<IntEntry<V>> iterable();
}