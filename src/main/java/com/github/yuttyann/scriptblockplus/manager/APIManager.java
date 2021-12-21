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
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.script.ScriptRead;
import com.github.yuttyann.scriptblockplus.script.ScriptKey;
import com.github.yuttyann.scriptblockplus.script.endprocess.EndProcess;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.script.option.Option;
import com.github.yuttyann.scriptblockplus.script.option.OptionIndex;
import com.github.yuttyann.scriptblockplus.utils.Utils;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
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
            this.scriptJson = BlockScriptJson.newJson(scriptKey);
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
            blockScript.setLastEdit(Utils.getFormatTime(Utils.DATE_PATTERN));
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
            blockScript.setLastEdit(Utils.getFormatTime(Utils.DATE_PATTERN));
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
        public boolean nameTag(@NotNull OfflinePlayer player, @NotNull Location location, @Nullable String nametag) {            
            var blockCoords = BlockCoords.of(location);
            if (!scriptJson.has(blockCoords)) {
                return false;
            }
            var blockScript = scriptJson.load(blockCoords);
            blockScript.getAuthors().add(player.getUniqueId());
            blockScript.setNameTag(nametag);
            blockScript.setLastEdit(Utils.getFormatTime(Utils.DATE_PATTERN));
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
            blockScript.setSelector(selector);
            blockScript.setLastEdit(Utils.getFormatTime(Utils.DATE_PATTERN));
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
            this.scriptJson = BlockScriptJson.newJson(scriptKey);
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
        public void setAuthor(@NotNull Set<UUID> author) {
            blockScript.setAuthors(author);
        }

        @Override
        @NotNull
        public Set<UUID> getAuthor() {
            return blockScript.getAuthors();
        }

        @Override
        public void setScript(@NotNull List<String> script) {
            blockScript.setScripts(script);
        }

        @Override
        @NotNull
        public List<String> getScript() {
            return blockScript.getScripts();
        }

        @Override
        public void setLastEdit() {
            blockScript.setLastEdit(Utils.getFormatTime(Utils.DATE_PATTERN));
        }

        @Override
        @Nullable
        public String getLastEdit() {
            return blockScript.getLastEdit();
        }

        @Override
        public void setNameTag(@Nullable String nameTag) {
            blockScript.setNameTag(nameTag);
        }

        @Override
        public String getNameTag() {
            return blockScript.getNameTag();
        }

        @Override
        public void setSelector(@Nullable String selector) {
            blockScript.setSelector(selector);
        }

        @Override  
        @Nullable
        public String getSelector() {
            return blockScript.getSelector();
        }

        @Override
        public void setAmount(int amount) {
            blockScript.setAmount(amount);
        }

        @Override
        public void addAmount(int amount) {
            blockScript.addAmount(amount);
        }

        @Override
        public void subtractAmount(int amount) {
            blockScript.subtractAmount(amount);
        }

        @Override
        public int getAmount() {
            return blockScript.getAmount();
        }

        @Override
        public void remove() {
            scriptJson.remove(blockCoords);
        }
    }
}