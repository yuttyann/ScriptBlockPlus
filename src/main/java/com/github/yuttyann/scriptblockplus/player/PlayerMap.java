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
package com.github.yuttyann.scriptblockplus.player;

import com.github.yuttyann.scriptblockplus.region.CuboidRegion;
import com.github.yuttyann.scriptblockplus.script.SBClipboard;
import com.github.yuttyann.scriptblockplus.script.ScriptEdit;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import com.github.yuttyann.scriptblockplus.utils.collection.ObjectMap;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * ScriptBlockPlus PlayerMap クラス
 * @author yuttyann44581
 */
public abstract class PlayerMap implements SBPlayer {

    private static final String KEY_REGION = Utils.randomUUID(), KEY_SCRIPT_EDIT = Utils.randomUUID(), KEY_SB_CLIPBOARD = Utils.randomUUID();

    private ObjectMap objectMap;

    @Override
    public final void clear() {
        this.objectMap = null;
    }

    @Override
    @NotNull
    public final ObjectMap getObjectMap() {
        return objectMap == null ? this.objectMap = new ObjMap() : objectMap;
    }

    @Override
    @NotNull
    public CuboidRegion getCuboidRegion() {
        var region = (CuboidRegion) getObjectMap().get(KEY_REGION);
        if (region == null) {
            getObjectMap().put(KEY_REGION, region = new CuboidRegion());
        }
        return region;
    }

    @Override
    public void setScriptEdit(@Nullable ScriptEdit scriptEdit) {
        getObjectMap().put(KEY_SCRIPT_EDIT, scriptEdit);
    }

    @Override
    @NotNull
    public Optional<ScriptEdit> getScriptEdit() {
        return Optional.ofNullable(getObjectMap().get(KEY_SCRIPT_EDIT));
    }

    @Override
    public void setSBClipboard(@Nullable SBClipboard sbClipboard) {
        getObjectMap().put(KEY_SB_CLIPBOARD, sbClipboard);
    }

    @Override
    @NotNull
    public Optional<SBClipboard> getSBClipboard() {
        return Optional.ofNullable(getObjectMap().get(KEY_SB_CLIPBOARD));
    }

    @SuppressWarnings("unchecked")
    private static class ObjMap implements ObjectMap {

        private final Map<String, Object> objectMap;

        private ObjMap() {
            this.objectMap = new HashMap<>();
        }

        @Override
        public void put(@NotNull String key, @Nullable Object value) {
            objectMap.put(key, value);
        }

        @Override
        @Nullable
        public <T> T get(@NotNull String key) {
            return (T) objectMap.get(key);
        }

        @Override
        public void remove(@NotNull String key) {
            objectMap.remove(key);
        }

        @Override
        public void clear() {
            objectMap.clear();
        }
    }
}