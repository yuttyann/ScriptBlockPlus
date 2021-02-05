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
import com.github.yuttyann.scriptblockplus.file.json.derived.PlayerCountJson;
import com.github.yuttyann.scriptblockplus.file.json.derived.PlayerTempJson;
import com.github.yuttyann.scriptblockplus.file.json.element.ScriptParam;
import com.github.yuttyann.scriptblockplus.script.SBOperation;
import com.github.yuttyann.scriptblockplus.script.ScriptRead;
import com.github.yuttyann.scriptblockplus.script.ScriptKey;
import com.github.yuttyann.scriptblockplus.script.endprocess.EndProcess;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.script.option.Option;
import com.github.yuttyann.scriptblockplus.script.option.OptionIndex;
import com.github.yuttyann.scriptblockplus.utils.Utils;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
        return new ScriptRead(player, BlockCoords.of(location), scriptKey).read(index);
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

        private final SBOperation sbOperation;

        public SEdit(@NotNull ScriptKey scriptKey) {
            this.sbOperation = new SBOperation(scriptKey);
        }

        @Override
        public void save() {
            sbOperation.save();
        }

        @Override
        @NotNull
        public ScriptKey getScriptKey() {
            return sbOperation.getScriptKey();
        }

        @Override
        public void create(@NotNull Player player, @NotNull Location location, @NotNull String script) {
            sbOperation.create(player, BlockCoords.of(location), script);
        }

        @Override
        public void add(@NotNull Player player, @NotNull Location location, @NotNull String script) {
            sbOperation.add(player, BlockCoords.of(location), script);
        }

        @Override
        public void remove(@NotNull Player player, @NotNull Location location) {
            sbOperation.remove(player, BlockCoords.of(location));
        }

        @Override
        public void view(@NotNull Player player, @NotNull Location location) {
            sbOperation.view(player, BlockCoords.of(location));
        }
    }

    @Override
    @NotNull
    public SBFile getSBFile(@NotNull ScriptKey scriptKey, @NotNull Location location) {
        return new SFile(scriptKey, location);
    }

    private static class SFile implements SBFile {

        private final ScriptKey scriptKey;
        private final ScriptParam scriptParam;
        private final BlockCoords blockCoords;
        private final BlockScriptJson scriptJson;

        public SFile(@NotNull ScriptKey scriptKey, @NotNull Location location) {
            this.scriptKey = scriptKey;
            this.scriptJson = BlockScriptJson.get(scriptKey);
            this.blockCoords = BlockCoords.of(location);
            this.scriptParam = scriptJson.load().get(blockCoords);
        }

        @Override
        public void save() {
            scriptJson.saveFile();
        }

        @Override
        public void reload() {
            scriptJson.reload();
        }

        @Override
        public boolean has() {
            return scriptJson.load().has(blockCoords);
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
            scriptParam.setAuthor(author);
        }

        @Override
        @NotNull
        public Set<UUID> getAuthor() {
            return scriptParam.getAuthor();
        }

        @Override
        public void setScript(@NotNull List<String> script) {
            scriptParam.setScript(script);
        }

        @Override
        @NotNull
        public List<String> getScript() {
            return scriptParam.getScript();
        }

        @Override
        public void setLastEdit() {
            scriptParam.setLastEdit(Utils.getFormatTime(Utils.DATE_PATTERN));
        }

        @Override
        @Nullable
        public String getLastEdit() {
            return scriptParam.getLastEdit();
        }

        @Override
        public void setSelector(@Nullable String selector) {
            scriptParam.setSelector(selector);
        }

        @Override  
        @Nullable
        public String getSelector() {
            return scriptParam.getSelector();
        }

        @Override
        public void setAmount(int amount) {
            scriptParam.setAmount(amount);
        }

        @Override
        public void addAmount(int amount) {
            scriptParam.addAmount(amount);
        }

        @Override
        public void subtractAmount(int amount) {
            scriptParam.subtractAmount(amount);
        }

        @Override
        public int getAmount() {
            return scriptParam.getAmount();
        }

        @Override
        public void remove() {
            PlayerTempJson.removeAll(scriptKey, blockCoords);
            PlayerCountJson.removeAll(scriptKey, blockCoords);
            scriptJson.load().remove(blockCoords);
        }
    }
}