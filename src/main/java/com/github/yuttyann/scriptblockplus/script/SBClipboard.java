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

import com.github.yuttyann.scriptblockplus.file.config.SBConfig;
import com.github.yuttyann.scriptblockplus.file.json.BlockScriptJson;
import com.github.yuttyann.scriptblockplus.file.json.PlayerCountJson;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.script.option.time.TimerOption;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import com.github.yuttyann.scriptblockplus.utils.unmodifiable.UnmodifiableLocation;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * ScriptBlockPlus ScriptClipboard クラス
 * @author yuttyann44581
 */
public class SBClipboard {

    private final SBPlayer sbPlayer;
    private final Location location;
    private final ScriptKey scriptKey;
    private final BlockScriptJson scriptJson;

    private final Set<UUID> author;
    private final List<String> script;
    private final String selector;
    private final int amount;

    public SBClipboard(@NotNull SBPlayer sbPlayer, @NotNull Location location, @NotNull BlockScriptJson scriptJson) {
        this.sbPlayer = sbPlayer;
        this.location = new UnmodifiableLocation(location);
        this.scriptKey = scriptJson.getScriptKey();
        this.scriptJson = scriptJson;

        var scriptParam = scriptJson.load().get(location);
        this.author = new LinkedHashSet<>(scriptParam.getAuthor());
        this.script = new ArrayList<>(scriptParam.getScript());
        this.selector = scriptParam.getSelector();
        this.amount = scriptParam.getAmount();
    }

    @NotNull
    public BlockScriptJson getBlockScriptJson() {
        return scriptJson;
    }

    @NotNull
    public SBPlayer getSBPlayer() {
        return sbPlayer;
    }

    @NotNull
    public Location getLocation() {
        return location;
    }

    @NotNull
    public Set<UUID> getAuthor() {
        return author;
    }

    @NotNull
    public List<String> getScript() {
        return script;
    }

    @Nullable
    public String getSelector() {
        return selector;
    }

    public int getAmount() {
        return amount;
    }

    public void save() {
        scriptJson.saveFile();
    }

    public boolean copy() {
        if (!BlockScriptJson.has(location, scriptJson)) {
            SBConfig.ERROR_SCRIPT_FILE_CHECK.send(sbPlayer);
            return false;
        }
        try {
            sbPlayer.setSBClipboard(this);
            SBConfig.SCRIPT_COPY.replace(scriptKey).send(sbPlayer);
            SBConfig.CONSOLE_SCRIPT_EDIT.replace(location, scriptKey).console();
        } finally {
            sbPlayer.setScriptEdit(null);
        }
        return true;
    }

    public boolean paste(@NotNull Location location, boolean overwrite) {
        if (BlockScriptJson.has(location, scriptJson) && !overwrite) {
            return false;
        }
        try {
            var scriptParam = scriptJson.load().get(location);
            scriptParam.setAuthor(author);
            scriptParam.getAuthor().add(sbPlayer.getUniqueId());
            scriptParam.setScript(script);
            scriptParam.setLastEdit(Utils.getFormatTime(Utils.DATE_PATTERN));
            scriptParam.setSelector(selector);
            scriptParam.setAmount(amount);
            scriptJson.saveFile();
            TimerOption.removeAll(location, scriptKey);
            PlayerCountJson.clear(location, scriptKey);
            SBConfig.SCRIPT_PASTE.replace(scriptKey).send(sbPlayer);
            SBConfig.CONSOLE_SCRIPT_EDIT.replace(sbPlayer.getName(), location, scriptKey).console();
        } finally {
            sbPlayer.setScriptEdit(null);
            sbPlayer.setSBClipboard(null);
        }
        return true;
    }
}