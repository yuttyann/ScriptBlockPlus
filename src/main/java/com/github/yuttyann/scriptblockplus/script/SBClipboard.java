package com.github.yuttyann.scriptblockplus.script;

import com.github.yuttyann.scriptblockplus.file.config.SBConfig;
import com.github.yuttyann.scriptblockplus.file.json.BlockScriptJson;
import com.github.yuttyann.scriptblockplus.file.json.PlayerCountJson;
import com.github.yuttyann.scriptblockplus.file.json.element.ScriptParam;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

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
    private final ScriptType scriptType;
    private final BlockScriptJson blockScriptJson;

    private final Set<UUID> author;
    private final List<String> script;
    private final int amount;

    public SBClipboard(@NotNull SBPlayer sbPlayer, @NotNull Location location, @NotNull BlockScriptJson blockScriptJson) {
        this.sbPlayer = sbPlayer;
        this.location = location;
        this.scriptType = blockScriptJson.getScriptType();
        this.blockScriptJson = blockScriptJson;

        ScriptParam scriptParam = blockScriptJson.load().get(location);
        this.author = scriptParam.getAuthor();
        this.script = scriptParam.getScript();
        this.amount = scriptParam.getAmount();
    }

    @NotNull
    public Location getLocation() {
        return location;
    }

    @NotNull
    public ScriptType getScriptType() {
        return scriptType;
    }

    public void save() {
        blockScriptJson.saveFile();
    }

    public boolean copy() {
        try {
            if (!BlockScriptJson.has(location, blockScriptJson)) {
                SBConfig.ERROR_SCRIPT_FILE_CHECK.send(sbPlayer);
                return false;
            }
            sbPlayer.setSBClipboard(this);
            SBConfig.SCRIPT_COPY.replace(scriptType).send(sbPlayer);
            SBConfig.CONSOLE_SCRIPT_COPY.replace(sbPlayer.getName(), location, scriptType).console();
        } finally {
            sbPlayer.setScriptLine(null);
            sbPlayer.setScriptEdit(null);
        }
        return true;
    }

    public boolean paste(@NotNull Location location, boolean overwrite) {
        try {
            if (BlockScriptJson.has(location, blockScriptJson) && !overwrite) {
                return false;
            }
            PlayerCountJson.clear(location, scriptType);
            ScriptParam scriptParam = blockScriptJson.load().get(location);
            scriptParam.setAuthor(author);
            scriptParam.getAuthor().add(sbPlayer.getUniqueId());
            scriptParam.setScript(script);
            scriptParam.setLastEdit(Utils.getFormatTime());
            scriptParam.setAmount(amount);
            blockScriptJson.saveFile();
            SBConfig.SCRIPT_PASTE.replace(scriptType).send(sbPlayer);
            SBConfig.CONSOLE_SCRIPT_PASTE.replace(sbPlayer.getName(), location, scriptType).console();
        } finally {
            sbPlayer.setSBClipboard(null);
            sbPlayer.setScriptLine(null);
            sbPlayer.setScriptEdit(null);
        }
        return true;
    }

    public boolean lightPaste(@NotNull Location location, boolean overwrite) {
        if (BlockScriptJson.has(location, blockScriptJson) && !overwrite) {
            return false;
        }
        PlayerCountJson.clear(location, scriptType);
        ScriptParam scriptParam = blockScriptJson.load().get(location);
        scriptParam.setAuthor(author);
        scriptParam.getAuthor().add(sbPlayer.getUniqueId());
        scriptParam.setScript(script);
        scriptParam.setLastEdit(Utils.getFormatTime());
        scriptParam.setAmount(amount);
        return true;
    }
}