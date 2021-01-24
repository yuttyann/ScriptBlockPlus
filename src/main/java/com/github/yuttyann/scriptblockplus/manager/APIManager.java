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

import com.github.yuttyann.scriptblockplus.ScriptBlockAPI;
import com.github.yuttyann.scriptblockplus.file.json.derived.BlockScriptJson;
import com.github.yuttyann.scriptblockplus.file.json.derived.PlayerCountJson;
import com.github.yuttyann.scriptblockplus.file.json.element.ScriptParam;
import com.github.yuttyann.scriptblockplus.script.SBOperation;
import com.github.yuttyann.scriptblockplus.script.ScriptRead;
import com.github.yuttyann.scriptblockplus.script.ScriptKey;
import com.github.yuttyann.scriptblockplus.script.endprocess.EndProcess;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.script.option.OptionIndex;
import com.github.yuttyann.scriptblockplus.script.option.time.TimerOption;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * ScriptBlockPlus APIManager クラス
 * @author yuttyann44581
 */
public final class APIManager implements ScriptBlockAPI {

    @Override
    public boolean read(@NotNull Player player, @NotNull Location location, @NotNull ScriptKey scriptKey, int index) {
        if (!BlockScriptJson.has(location, scriptKey)) {
            return false;
        }
        return new ScriptRead(player, location, scriptKey).read(index);
    }

    @Override
    public void registerOption(@NotNull OptionIndex optionIndex, @NotNull Class<? extends BaseOption> optionClass) {
        OptionManager.register(optionIndex, optionClass);
    }

    @Override
    public void registerEndProcess(@NotNull Class<? extends EndProcess> endProcessClass) {
        EndProcessManager.register(new SBConstructor<>(endProcessClass));
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
            sbOperation.create(player, location, script);
        }

        @Override
        public void add(@NotNull Player player, @NotNull Location location, @NotNull String script) {
            sbOperation.add(player, location, script);
        }

        @Override
        public void remove(@NotNull Player player, @NotNull Location location) {
            sbOperation.remove(player, location);
        }

        @Override
        public void view(@NotNull Player player, @NotNull Location location) {
            sbOperation.view(player, location);
        }
    }

    @Override
    @NotNull
    public SBFile getSBFile(@NotNull Location location, @NotNull ScriptKey scriptKey) {
        return new SFile(location, scriptKey);
    }

    private static class SFile implements SBFile {

        private final Location location;
        private final ScriptKey scriptKey;
        private final ScriptParam scriptParam;
        private final BlockScriptJson scriptJson;

        public SFile(@NotNull Location location, @NotNull ScriptKey scriptKey) {
            this.location = location;
            this.scriptKey = scriptKey;
            this.scriptJson = new BlockScriptJson(scriptKey);
            this.scriptParam = scriptJson.load().get(location);
        }

        @Override
        public void save() {
            scriptJson.saveFile();
        }

        @Override
        public boolean has() {
            return scriptJson.load().has(location);
        }

        @Override
        @Nullable
        public Location getLocation() {
            return location;
        }

        @Override
        @NotNull
        public ScriptKey getScriptKey() {
            return scriptKey;
        }

        @Override
        @NotNull
        public Set<UUID> getAuthor() {
            return scriptParam.getAuthor();
        }

        @Override
        public void setAuthor(@NotNull Set<UUID> author) {
            scriptParam.setAuthor(author);
        }

        @Override
        @NotNull
        public List<String> getScript() {
            return scriptParam.getScript();
        }

        @Override
        public void setScript(@NotNull List<String> script) {
            scriptParam.setScript(script);
        }

        @Override
        @Nullable
        public String getLastEdit() {
            return scriptParam.getLastEdit();
        }

        @Override
        public void setLastEdit() {
            scriptParam.setLastEdit(Utils.getFormatTime(Utils.DATE_PATTERN));
        }

        @Override
        public int getAmount() {
            return scriptParam.getAmount();
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
        public void remove() {
            TimerOption.removeAll(location, scriptKey);
            PlayerCountJson.clear(location, scriptKey);
            scriptJson.load().remove(location);
        }
    }
}