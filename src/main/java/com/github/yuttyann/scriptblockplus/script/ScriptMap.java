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
package com.github.yuttyann.scriptblockplus.script;

import com.github.yuttyann.scriptblockplus.player.ObjectMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * ScriptBlockPlus ScriptMap クラス
 * @author yuttyann44581
 */
public abstract class ScriptMap implements ObjectMap {

    private static final Map<UUID, Map<String, Object>> TEMP_MAP = new HashMap<>();

    protected final UUID ramdomId = UUID.randomUUID();

    @Override
    public void put(@NotNull String key, @Nullable Object value) {
        TEMP_MAP.computeIfAbsent(ramdomId, k -> new HashMap<>()).put(key, value);
    }

    @Override
    @Nullable
    @SuppressWarnings("unchecked")
    public <T> T get(@NotNull String key) {
        var map = TEMP_MAP.get(ramdomId);
        return map == null ? null : (T) map.get(key);
    }

    @Override
    public void remove(@NotNull String key) {
        TEMP_MAP.get(ramdomId).remove(key);
    }

    @Override
    public void clear() {
        TEMP_MAP.remove(ramdomId);
    }
}