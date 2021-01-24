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

import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

import com.github.yuttyann.scriptblockplus.utils.StreamUtils;

import org.jetbrains.annotations.NotNull;

/**
 * ScriptBlockPlus MultiJson クラス
 * @param <T> 値の型
 * @author yuttyann44581
 */
public abstract class MultiJson<T> extends BaseJson<T> {

    public MultiJson(@NotNull UUID uuid) {
        super(uuid);
    }
    
    public MultiJson(@NotNull String id) {
        super(id);
    }

    @NotNull
    protected abstract T newInstance(@NotNull Object... args);

    @NotNull
    protected T load(@NotNull Object... args) {
        int hash = Objects.hash(args);
        var value = StreamUtils.filterFirst(list, t -> t.hashCode() == hash);
        if (!value.isPresent()) {
            T instance = newInstance(args);
            list.add(instance);
            return instance;
        }
        return value.get();
    }

    protected boolean has(@NotNull Object... args) {
        int hash = Objects.hash(args);
        for (T t : list) {
            if (t.hashCode() == hash) {
                return true;
            }
        }
        return false;
    }

    protected void action(@NotNull Consumer<T> action, @NotNull Object... args) {
        action.accept(load(args));
        saveFile();
    }
}