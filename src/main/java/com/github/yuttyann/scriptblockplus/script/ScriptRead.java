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

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.event.ScriptReadEndEvent;
import com.github.yuttyann.scriptblockplus.event.ScriptReadStartEvent;
import com.github.yuttyann.scriptblockplus.file.config.SBConfig;
import com.github.yuttyann.scriptblockplus.file.json.derived.BlockScriptJson;
import com.github.yuttyann.scriptblockplus.file.json.derived.PlayerCountJson;
import com.github.yuttyann.scriptblockplus.file.json.element.BlockScript;
import com.github.yuttyann.scriptblockplus.file.json.element.PlayerCount;
import com.github.yuttyann.scriptblockplus.hook.plugin.Placeholder;
import com.github.yuttyann.scriptblockplus.manager.EndProcessManager;
import com.github.yuttyann.scriptblockplus.manager.OptionManager;
import com.github.yuttyann.scriptblockplus.player.ObjectMap;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.script.option.Option;
import com.github.yuttyann.scriptblockplus.utils.StreamUtils;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.unmodifiable.UnmodifiableBlockCoords;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ScriptBlockPlus ScriptRead クラス
 * @author yuttyann44581
 */
public class ScriptRead extends ScriptMap implements SBRead {

    private boolean initialize;

    // 初期宣言
    protected final SBPlayer sbPlayer;
    protected final ScriptKey scriptKey;
    protected final BlockCoords blockCoords;
    protected final BlockScript blockScript;

    // ScriptRead#read(int) から
    protected int index;
    protected String value;
    protected List<String> scripts;

    public ScriptRead(@NotNull Player player, @NotNull BlockCoords blockCoords, @NotNull ScriptKey scriptKey) {
        this.initialize = true;
        this.sbPlayer = SBPlayer.fromPlayer(player);
        this.scriptKey = scriptKey;
        this.blockCoords = new UnmodifiableBlockCoords(blockCoords);
        this.blockScript = BlockScriptJson.get(scriptKey).load();
    }
    
    public final void setInitialize(boolean initialize) {
        this.initialize = initialize;
    }

    public final boolean isInitialize() {
        return initialize;
    }

    @Override
    @NotNull
    public SBPlayer getSBPlayer() {
        return sbPlayer;
    }

    @Override
    @NotNull
    public ScriptKey getScriptKey() {
        return scriptKey;
    }

    @Override
    @NotNull
    public Location getLocation() {
        return blockCoords.toLocation();
    }

    @Override
    @NotNull
    public BlockCoords getBlockCoords() {
        return blockCoords;
    }

    @Override
    @NotNull
    public List<String> getScripts() {
        return scripts;
    }

    @Override
    @NotNull
    public String getOptionValue() {
        return value;
    }

    @Override
    public int getScriptIndex() {
        return index;
    }

    @Override
    public boolean read(final int index) {
        if (!blockScript.has(blockCoords)) {
            SBConfig.ERROR_SCRIPT_FILE_CHECK.send(sbPlayer);
            return false;
        }
        if (!sortScripts(blockScript.get(blockCoords).getScript())) {
            SBConfig.ERROR_SCRIPT_EXECUTE.replace(scriptKey).send(sbPlayer);
            SBConfig.CONSOLE_ERROR_SCRIPT_EXECUTE.replace(blockCoords, scriptKey).console();
            return false;
        }
        Bukkit.getPluginManager().callEvent(new ScriptReadStartEvent(ramdomId, this));
        try {
            return perform(index);
        } finally {
            Bukkit.getPluginManager().callEvent(new ScriptReadEndEvent(ramdomId, this));
            StreamUtils.filter(this, ScriptRead::isInitialize, ObjectMap::clear);
        }
    }

    protected boolean perform(final int index) {
        if (!sbPlayer.isOnline()) {
            EndProcessManager.forEach(e -> e.failed(this));
            return false;
        }
        for (this.index = index; this.index < scripts.size(); this.index++) {
            var script = scripts.get(this.index);
            var option = OptionManager.newInstance(script);
            this.value = Placeholder.INSTANCE.replace(getPlayer(), option.getValue(script));
            if (!option.callOption(this) && isFailedIgnore(option)) {
                return false;
            }
        }
        EndProcessManager.forEach(e -> e.success(this));
        PlayerCountJson.get(sbPlayer).action(PlayerCount::add, scriptKey, blockCoords);
        SBConfig.CONSOLE_SUCCESS_SCRIPT_EXECUTE.replace(scriptKey, blockCoords).console();
        return true;
    }
    
    protected boolean isFailedIgnore(@NotNull Option option) {
        StreamUtils.ifAction(!option.isFailedIgnore(), () -> EndProcessManager.forEach(e -> e.failed(this)));
        return true;
    }

    protected boolean sortScripts(@NotNull List<String> scripts) {
        try {
            var parse = new ArrayList<String>();
            for (int i = 0, l = scripts.size(); i < l; i++) {
                parse.addAll(StringUtils.getScripts(scripts.get(i)));
            }
            SBConfig.SORT_SCRIPTS.ifPresentAndTrue(s -> OptionManager.sort(parse));
            this.scripts = Collections.unmodifiableList(parse);
            return true;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return false;
        }
    }
}