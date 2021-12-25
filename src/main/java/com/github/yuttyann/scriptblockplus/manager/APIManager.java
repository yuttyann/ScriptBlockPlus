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
package com.github.yuttyann.scriptblockplus.manager;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.ScriptBlockAPI;
import com.github.yuttyann.scriptblockplus.file.json.derived.BlockScriptJson;
import com.github.yuttyann.scriptblockplus.file.json.derived.element.BlockScript;
import com.github.yuttyann.scriptblockplus.file.json.derived.element.ValueHolder;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.script.ScriptRead;
import com.github.yuttyann.scriptblockplus.script.ScriptKey;
import com.github.yuttyann.scriptblockplus.script.endprocess.EndProcess;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.script.option.Option;
import com.github.yuttyann.scriptblockplus.script.option.OptionIndex;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * ScriptBlockPlus APIManager クラス
 * @author yuttyann44581
 */
public final class APIManager implements ScriptBlockAPI {

    @Override
    public boolean read(@NotNull Player player, @NotNull Location location, @NotNull ScriptKey scriptKey, int index) {
        return new ScriptRead(SBPlayer.fromPlayer(player), BlockCoords.of(location), scriptKey).read(index);
    }

    @Override
    public void registerOption(@NotNull OptionIndex optionIndex, @NotNull Supplier<Option> newInstance) {
        OptionManager.register(optionIndex, newInstance);
        OptionManager.update();
    }

    @Override
    public void unregisterOption(@NotNull Class<? extends BaseOption> optionClass) {
        OptionManager.unregister(optionClass);
        OptionManager.update();
    }

    @Override
    public void registerEndProcess(@NotNull Supplier<EndProcess> newInstance) {
        EndProcessManager.register(newInstance);
    }

    @Override
    @NotNull
    public SBEdit getSBEdit(@NotNull ScriptKey scriptKey) {
        return new SEdit(scriptKey);
    }

    private static class SEdit implements SBEdit {

        private final ScriptKey scriptKey;
        private final BlockScriptJson scriptJson;

        public SEdit(@NotNull ScriptKey scriptKey) {
            this.scriptKey = scriptKey;
            this.scriptJson = BlockScriptJson.get(scriptKey);
        }

        @Override
        @NotNull
        public ScriptKey getScriptKey() {
            return scriptKey;
        }

        @Override
        public void create(@NotNull OfflinePlayer player, @NotNull Location location, @NotNull String script) {
            var blockCoords = BlockCoords.of(location);
            var blockScript = scriptJson.load(blockCoords);
            blockScript.getAuthors().add(player.getUniqueId());
            blockScript.setScripts(Collections.singletonList(script));
            blockScript.setLastEdit(new Date());
            blockScript.setValues(null);
            scriptJson.init(blockCoords);
            scriptJson.saveJson();
        }

        @Override
        public boolean add(@NotNull OfflinePlayer player, @NotNull Location location, @NotNull String script) {
            var blockCoords = BlockCoords.of(location);
            if (!scriptJson.has(blockCoords)) {
                return false;
            }
            var blockScript = scriptJson.load(blockCoords);
            blockScript.getAuthors().add(player.getUniqueId());
            blockScript.getScripts().add(script);
            blockScript.setLastEdit(new Date());
            scriptJson.saveJson();
            return true;
        }

        @Override
        public boolean remove(@NotNull OfflinePlayer player, @NotNull Location location) {
            var blockCoords = BlockCoords.of(location);
            if (!scriptJson.has(blockCoords)) {
                return false;
            }
            scriptJson.init(blockCoords);
            scriptJson.remove(blockCoords);
            scriptJson.saveJson();
            return true;
        }

        @Override
        public boolean nametag(@NotNull OfflinePlayer player, @NotNull Location location, @Nullable String nametag) {            
            var blockCoords = BlockCoords.of(location);
            if (!scriptJson.has(blockCoords)) {
                return false;
            }
            var blockScript = scriptJson.load(blockCoords);
            blockScript.getAuthors().add(player.getUniqueId());
            blockScript.setLastEdit(new Date());
            blockScript.setValue(BlockScript.NAMETAG, nametag);
            scriptJson.saveJson();
            return true;
        }

        @Override
        public boolean redstone(@NotNull OfflinePlayer player, @NotNull Location location, @Nullable String selector) {
            var blockCoords = BlockCoords.of(location);
            if (!scriptJson.has(blockCoords)) {
                return false;
            }
            var blockScript = scriptJson.load(blockCoords);
            blockScript.getAuthors().add(player.getUniqueId());
            blockScript.setLastEdit(new Date());
            blockScript.setValue(BlockScript.SELECTOR, selector);
            scriptJson.saveJson();
            return true;
        }
    }

    @Override
    @NotNull
    public SBFile getSBFile(@NotNull ScriptKey scriptKey, @NotNull Location location) {
        return new SFile(scriptKey, location);
    }

    private static class SFile implements SBFile {

        private final ScriptKey scriptKey;
        private final BlockCoords blockCoords;
        private final BlockScript blockScript;
        private final BlockScriptJson scriptJson;

        public SFile(@NotNull ScriptKey scriptKey, @NotNull Location location) {
            this.scriptKey = scriptKey;
            this.scriptJson = BlockScriptJson.get(scriptKey);
            this.blockCoords = BlockCoords.of(location);
            this.blockScript = scriptJson.load(blockCoords);
        }

        @Override
        public void save() {
            if (!scriptJson.has(blockCoords)) {
                scriptJson.init(blockCoords);
            }
            scriptJson.saveJson();
        }

        @Override
        public void reload() {
            scriptJson.reload();
        }

        @Override
        public boolean has() {
            return scriptJson.has(blockCoords) && blockScript.getAuthors().size() > 0;
        }

        @Override
        @NotNull
        public ScriptKey getScriptKey() {
            return scriptKey;
        }

        @Override
        @Nullable
        public Location getLocation() {
            return blockCoords.toLocation();
        }

        @Override
        public void setAuthors(@NotNull Set<UUID> author) {
            blockScript.setAuthors(author);
        }

        @Override
        @NotNull
        public Set<UUID> getAuthors() {
            return blockScript.getAuthors();
        }

        @Override
        public void setScripts(@NotNull List<String> script) {
            blockScript.setScripts(script);
        }

        @Override
        @NotNull
        public List<String> getScripts() {
            return blockScript.getScripts();
        }

        public void setLastEdit(@NotNull Date date) {
            blockScript.setLastEdit(date);
        }

        @Override
        @Nullable
        public Date getLastEdit() {
            return blockScript.getLastEdit();
        }

        @Override
        @Nullable
        public ValueHolder setValue(@NotNull String key, @Nullable Object value) {
            return blockScript.setValue(key, value).orElse(null);
        }
    
        @Override
        @Nullable
        public ValueHolder getValue(@NotNull String key) {
            return blockScript.getValue(key).orElse(null);
        }

        @Override
        public void remove() {
            scriptJson.remove(blockCoords);
        }
    }
}