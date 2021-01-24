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

import java.util.UUID;
import java.util.function.Consumer;

import org.jetbrains.annotations.NotNull;

/**
 * ScriptBlockPlus SingleJson クラス
 * @param <T> 値の型
 * @author yuttyann44581
 */
public abstract class SingleJson<T> extends BaseJson<T> {

	public SingleJson(@NotNull UUID uuid) {
        super(uuid);
    }

	public SingleJson(@NotNull String id) {
		super(id);
    }

    @NotNull
    protected abstract T newInstance();

    @NotNull
    public final T load() {
        if (list.isEmpty()) {
            list.add(newInstance());
        }
        return list.get(0);
    }

    public final boolean has() {
        return !list.isEmpty();
    }

    public final void action(@NotNull Consumer<T> action) {
        action.accept(load());
        saveFile();
    }
}