package com.github.yuttyann.scriptblockplus.script;

import com.github.yuttyann.scriptblockplus.file.config.SBConfig;
import com.github.yuttyann.scriptblockplus.file.json.BlockScriptJson;
import com.github.yuttyann.scriptblockplus.file.json.PlayerCountJson;
import com.github.yuttyann.scriptblockplus.file.json.element.ScriptParam;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.script.option.time.TimerOption;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

/**
 * ScriptBlockPlus SBOperation クラス
 * @author yuttyann44581
 */
public final class SBOperation {

    private final ScriptType scriptType;
    private final BlockScriptJson scriptJson;

    public SBOperation(@NotNull ScriptType scriptType) {
        this.scriptType = scriptType;
        this.scriptJson = new BlockScriptJson(scriptType);
    }

    @NotNull
    public BlockScriptJson getBlockScriptJson() {
        return scriptJson;
    }

    @NotNull
    public ScriptType getScriptType() {
        return scriptType;
    }

    public boolean exists() {
        return scriptJson.exists();
    }

    public void save() {
        scriptJson.saveFile();
    }

    @NotNull
    public String getAuthors(@NotNull ScriptParam scriptParam) {
        return scriptParam.getAuthor().stream().map(Utils::getName).collect(Collectors.joining(", "));
    }

    public void create(@NotNull Player player, @NotNull Location location, @NotNull String script) {
        var scriptParam = scriptJson.load().get(location);
        scriptParam.getAuthor().add(player.getUniqueId());
        scriptParam.setScript(Collections.singletonList(script));
        scriptParam.setLastEdit(Utils.getFormatTime(Utils.DATE_PATTERN));
        scriptJson.saveFile();
        TimerOption.removeAll(location, scriptType);
        PlayerCountJson.clear(location, scriptType);
        SBConfig.SCRIPT_CREATE.replace(scriptType).send(player);
        SBConfig.CONSOLE_SCRIPT_CREATE.replace(player.getName(), location, scriptType).console();
    }

    public void add(@NotNull Player player, @NotNull Location location, @NotNull String script) {
        if (!BlockScriptJson.has(location, scriptJson)) {
            SBConfig.ERROR_SCRIPT_FILE_CHECK.send(player);
            return;
        }
        var scriptParam = scriptJson.load().get(location);
        scriptParam.getAuthor().add(player.getUniqueId());
        scriptParam.getScript().add(script);
        scriptParam.setLastEdit(Utils.getFormatTime(Utils.DATE_PATTERN));
        scriptJson.saveFile();
        TimerOption.removeAll(location, scriptType);
        SBConfig.SCRIPT_ADD.replace(scriptType).send(player);
        SBConfig.CONSOLE_SCRIPT_ADD.replace(player.getName(), location, scriptType).console();
    }

    public void remove(@NotNull Player player, @NotNull Location location) {
        if (!BlockScriptJson.has(location, scriptJson)) {
            SBConfig.ERROR_SCRIPT_FILE_CHECK.send(player);
            return;
        }
        scriptJson.load().remove(location);
        scriptJson.saveFile();
        TimerOption.removeAll(location, scriptType);
        PlayerCountJson.clear(location, scriptType);
        SBConfig.SCRIPT_REMOVE.replace(scriptType).send(player);
        SBConfig.CONSOLE_SCRIPT_REMOVE.replace(player.getName(), location, scriptType).console();
    }

    public void view(@NotNull Player player, @NotNull Location location) {
        if (!BlockScriptJson.has(location, scriptJson)) {
            SBConfig.ERROR_SCRIPT_FILE_CHECK.send(player);
            return;
        }
        var scriptParam = scriptJson.load().get(location);
        var playerCount = new PlayerCountJson(player.getUniqueId()).load(location, scriptType);
        player.sendMessage("Author: " + getAuthors(scriptParam));
        player.sendMessage("LastEdit: " + scriptParam.getLastEdit());
        player.sendMessage("PlayerCount: " + playerCount.getAmount());
        player.sendMessage("Scripts:");
        scriptParam.getScript().forEach(s -> player.sendMessage("- " + s));
        SBConfig.CONSOLE_SCRIPT_VIEW.replace(player.getName(), location, scriptType).console();
    }

    @NotNull
    public SBClipboard clipboard(@NotNull SBPlayer sbPlayer, @NotNull Location location) {
        return new SBClipboard(sbPlayer, location, scriptJson);
    }
}