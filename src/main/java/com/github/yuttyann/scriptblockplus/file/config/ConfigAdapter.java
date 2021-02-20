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
package com.github.yuttyann.scriptblockplus.file.config;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * ScriptBlockPlus ConfigAdapter クラス
 * @author yuttyann44581
 */
@SuppressWarnings("unchecked")
public final class ConfigAdapter {

    private final Map<String, Object> map;

    public ConfigAdapter(@NotNull Map<String, Object> map) {
        this.map = map;
    }

    @Nullable
    public <T> T get(@NotNull String key) {
        return (T) map.get(key);
    }

    @NotNull
    public <T> T get(@NotNull String key, @NotNull T def) {
        T value = get(key);
        return value == null ? def : value;
    }
}