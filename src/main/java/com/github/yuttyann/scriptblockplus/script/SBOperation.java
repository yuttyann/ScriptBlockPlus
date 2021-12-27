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
import com.github.yuttyann.scriptblockplus.file.json.derived.element.BlockScript;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.utils.Utils;
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
        this.scriptJson = BlockScriptJson.get(scriptKey);
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

    public void create(@NotNull SBPlayer sbPlayer, @NotNull BlockCoords blockCoords, @NotNull String script) {
        var blockScript = scriptJson.load(blockCoords);
        blockScript.getAuthors().add(sbPlayer.getUniqueId());
        blockScript.setScripts(Collections.singletonList(script));
        blockScript.setLastEdit(new Date());
        blockScript.setValues(null);
        scriptJson.init(blockCoords);
        scriptJson.saveJson();
        SBConfig.SCRIPT_CREATE.replace(scriptKey).send(sbPlayer);
        SBConfig.CONSOLE_SCRIPT_EDIT.replace(scriptKey, blockCoords).console();
    }

    public void add(@NotNull SBPlayer sbPlayer, @NotNull BlockCoords blockCoords, @NotNull String script) {
        if (!scriptJson.has(blockCoords)) {
            SBConfig.ERROR_SCRIPT_FILE_CHECK.send(sbPlayer);
            return;
        }
        var blockScript = scriptJson.load(blockCoords);
        blockScript.getAuthors().add(sbPlayer.getUniqueId());
        blockScript.getScripts().add(script);
        blockScript.setLastEdit(new Date());
        scriptJson.saveJson();
        SBConfig.SCRIPT_ADD.replace(scriptKey).send(sbPlayer);
        SBConfig.CONSOLE_SCRIPT_EDIT.replace(scriptKey, blockCoords).console();
    }

    public void remove(@NotNull SBPlayer sbPlayer, @NotNull BlockCoords blockCoords) {
        if (!scriptJson.has(blockCoords)) {
            SBConfig.ERROR_SCRIPT_FILE_CHECK.send(sbPlayer);
            return;
        }
        scriptJson.init(blockCoords);
        scriptJson.remove(blockCoords);
        scriptJson.saveJson();
        SBConfig.SCRIPT_REMOVE.replace(scriptKey).send(sbPlayer);
        SBConfig.CONSOLE_SCRIPT_EDIT.replace(scriptKey, blockCoords).console();
    }

    public void view(@NotNull SBPlayer sbPlayer, @NotNull BlockCoords blockCoords) {
        if (!scriptJson.has(blockCoords)) {
            SBConfig.ERROR_SCRIPT_FILE_CHECK.send(sbPlayer);
            return;
        }
        sbPlayer.sendMessage("§6--------- [ Script Views ] ---------");
        getScriptInfos(sbPlayer, scriptKey, scriptJson, blockCoords).forEach(sbPlayer::sendMessage);
        sbPlayer.sendMessage("§6----------------------------------");
        SBConfig.CONSOLE_SCRIPT_VIEW.replace(scriptKey, blockCoords).console();
    }

    public void nameTag(@NotNull SBPlayer sbPlayer, @NotNull BlockCoords blockCoords, @Nullable String nametag) {
        if (!scriptJson.has(blockCoords)) {
            SBConfig.ERROR_SCRIPT_FILE_CHECK.send(sbPlayer);
            return;
        }
        var blockScript = scriptJson.load(blockCoords);
        blockScript.getAuthors().add(sbPlayer.getUniqueId());
        blockScript.setLastEdit(new Date());
        blockScript.setValue(BlockScript.NAMETAG, nametag);
        scriptJson.saveJson();
        SBConfig.SCRIPT_NAMETAG.replace(scriptKey).send(sbPlayer);
        SBConfig.CONSOLE_SCRIPT_EDIT.replace(scriptKey, blockCoords).console();
    }

    public void redstone(@NotNull SBPlayer sbPlayer, @NotNull BlockCoords blockCoords, @Nullable String selector) {
        if (!scriptJson.has(blockCoords)) {
            SBConfig.ERROR_SCRIPT_FILE_CHECK.send(sbPlayer);
            return;
        }
        var blockScript = scriptJson.load(blockCoords);
        blockScript.getAuthors().add(sbPlayer.getUniqueId());
        blockScript.setLastEdit(new Date());
        blockScript.setValue(BlockScript.SELECTOR, selector);
        scriptJson.saveJson();
        SBConfig.SCRIPT_REDSTONE.replace(scriptKey).send(sbPlayer);
        SBConfig.CONSOLE_SCRIPT_EDIT.replace(scriptKey, blockCoords).console();
    }

    @NotNull
    public static List<String> getScriptInfos(@NotNull SBPlayer sbPlayer, @NotNull ScriptKey scriptKey, @NotNull BlockScriptJson scriptJson, @NotNull BlockCoords blockCoords) {
        var info = new ArrayList<String>();
        var blockScript = scriptJson.load(blockCoords);
        info.add("§eAuthor: §a" + blockScript.getAuthors().stream().map(Utils::getName).collect(Collectors.joining(", ")));
        info.add("§eUpdate: §a" + Utils.DATE_FORMAT.format(blockScript.getLastEdit()));
        info.add("§eMyCount: §a" + PlayerCountJson.get(sbPlayer.getUniqueId()).load(scriptKey, blockCoords).getAmount());
        info.add("§eScripts:");
        blockScript.getScripts().forEach(s -> info.add("§6- §b" + s));

        if (blockScript.hasValues()) {
            info.add("§eValues:");
            blockScript.getValues().entrySet().forEach(e -> {
                var value = e.getValue().toString();
                var index = value.indexOf(':');
                info.add("§6- §a" + e.getKey() + "§d: §b" + value.substring(index + 1, value.length()));
            });
        }
        return info;
    }
}