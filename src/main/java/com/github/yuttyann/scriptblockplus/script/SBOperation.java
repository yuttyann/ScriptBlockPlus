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
import com.github.yuttyann.scriptblockplus.file.config.SBConfig;
import com.github.yuttyann.scriptblockplus.file.json.derived.BlockScriptJson;
import com.github.yuttyann.scriptblockplus.file.json.derived.PlayerCountJson;
import com.github.yuttyann.scriptblockplus.file.json.element.BlockScript;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * ScriptBlockPlus SBOperation クラス
 * @author yuttyann44581
 */
public final class SBOperation {

    private final ScriptKey scriptKey;
    private final BlockScriptJson scriptJson;

    public SBOperation(@NotNull ScriptKey scriptKey) {
        this.scriptKey = scriptKey;
        this.scriptJson = BlockScriptJson.newJson(scriptKey);
    }

    @NotNull
    public BlockScriptJson getBlockScriptJson() {
        return scriptJson;
    }

    @NotNull
    public ScriptKey getScriptKey() {
        return scriptKey;
    }

    public boolean exists() {
        return scriptJson.exists();
    }

    public void save() {
        scriptJson.saveJson();
    }

    @NotNull
    public String getAuthors(@NotNull BlockScript blockScript) {
        return blockScript.getAuthors().stream().map(Utils::getName).collect(Collectors.joining(", "));
    }

    public void create(@NotNull Player player, @NotNull BlockCoords blockCoords, @NotNull String script) {
        var blockScript = scriptJson.load(blockCoords);
        blockScript.getAuthors().add(player.getUniqueId());
        blockScript.setScripts(Collections.singletonList(script));
        blockScript.setLastEdit(Utils.getFormatTime(Utils.DATE_PATTERN));
        scriptJson.init(blockCoords);
        scriptJson.saveJson();
        SBConfig.SCRIPT_CREATE.replace(scriptKey).send(player);
        SBConfig.CONSOLE_SCRIPT_EDIT.replace(scriptKey, blockCoords).console();
    }

    public void add(@NotNull Player player, @NotNull BlockCoords blockCoords, @NotNull String script) {
        if (!scriptJson.has(blockCoords)) {
            SBConfig.ERROR_SCRIPT_FILE_CHECK.send(player);
            return;
        }
        var blockScript = scriptJson.load(blockCoords);
        blockScript.getAuthors().add(player.getUniqueId());
        blockScript.getScripts().add(script);
        blockScript.setLastEdit(Utils.getFormatTime(Utils.DATE_PATTERN));
        scriptJson.saveJson();
        SBConfig.SCRIPT_ADD.replace(scriptKey).send(player);
        SBConfig.CONSOLE_SCRIPT_EDIT.replace(scriptKey, blockCoords).console();
    }

    public void remove(@NotNull Player player, @NotNull BlockCoords blockCoords) {
        if (!scriptJson.has(blockCoords)) {
            SBConfig.ERROR_SCRIPT_FILE_CHECK.send(player);
            return;
        }
        scriptJson.init(blockCoords);
        scriptJson.remove(blockCoords);
        scriptJson.saveJson();
        SBConfig.SCRIPT_REMOVE.replace(scriptKey).send(player);
        SBConfig.CONSOLE_SCRIPT_EDIT.replace(scriptKey, blockCoords).console();
    }

    public void view(@NotNull Player player, @NotNull BlockCoords blockCoords) {
        if (!scriptJson.has(blockCoords)) {
            SBConfig.ERROR_SCRIPT_FILE_CHECK.send(player);
            return;
        }
        var blockScript = scriptJson.load(blockCoords);
        var playerCount = PlayerCountJson.newJson(player.getUniqueId()).load(scriptKey, blockCoords);
        var selector = blockScript.getSelector();
        player.sendMessage("--------- [ Script Views ] ---------");
        player.sendMessage("§eAuthor: §a" + getAuthors(blockScript));
        player.sendMessage("§eUpdate: §a" + blockScript.getLastEdit());
        player.sendMessage("§eMyCount: §a" + playerCount.getAmount());
        player.sendMessage("§eRedstone: §" + (selector == null ? "cfalse" : "atrue §d: §a" + selector));
        player.sendMessage("§eScripts:");
        blockScript.getScripts().forEach(s -> player.sendMessage("§6- §b" + s));
        player.sendMessage("--------------------------------");
        SBConfig.CONSOLE_SCRIPT_VIEW.replace(scriptKey, blockCoords).console();
    }

    public void redstone(@NotNull Player player, @NotNull BlockCoords blockCoords, @Nullable String selector) {
        if (!scriptJson.has(blockCoords)) {
            SBConfig.ERROR_SCRIPT_FILE_CHECK.send(player);
            return;
        }
        var blockScript = scriptJson.load(blockCoords);
        blockScript.getAuthors().add(player.getUniqueId());
        blockScript.setSelector(selector);
        blockScript.setLastEdit(Utils.getFormatTime(Utils.DATE_PATTERN));
        scriptJson.saveJson();
        if (StringUtils.isEmpty(selector)) {
            SBConfig.SCRIPT_REDSTONE_DISABLE.replace(scriptKey).send(player);
        } else {
            SBConfig.SCRIPT_REDSTONE_ENABLE.replace(scriptKey).send(player);
        }
        SBConfig.CONSOLE_SCRIPT_EDIT.replace(scriptKey, blockCoords).console();
    }
}