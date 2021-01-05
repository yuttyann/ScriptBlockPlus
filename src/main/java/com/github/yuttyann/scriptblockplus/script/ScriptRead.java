package com.github.yuttyann.scriptblockplus.script;

import com.github.yuttyann.scriptblockplus.event.ScriptReadEndEvent;
import com.github.yuttyann.scriptblockplus.event.ScriptReadStartEvent;
import com.github.yuttyann.scriptblockplus.file.config.SBConfig;
import com.github.yuttyann.scriptblockplus.file.json.BlockScriptJson;
import com.github.yuttyann.scriptblockplus.file.json.PlayerCountJson;
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
import com.github.yuttyann.scriptblockplus.utils.unmodifiable.UnmodifiableLocation;

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
    protected SBPlayer sbPlayer;
    protected Location location;
    protected ScriptType scriptType;
    protected BlockScript blockScript;

    // ScriptRead#read(int) から
    protected int index;
    protected String value;
    protected List<String> scripts;

    public ScriptRead(@NotNull Player player, @NotNull Location location, @NotNull ScriptType scriptType) {
        location.setX(location.getBlockX() + 0.5D);
        location.setY(location.getBlockY() + 0.5D);
        location.setZ(location.getBlockZ() + 0.5D);
        this.initialize = true;
        this.sbPlayer = SBPlayer.fromPlayer(player);
        this.location = new UnmodifiableLocation(location);
        this.scriptType = scriptType;
        this.blockScript = new BlockScriptJson(scriptType).load();
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
    public Location getLocation() {
        return location;
    }

    @Override
    @NotNull
    public ScriptType getScriptType() {
        return scriptType;
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
        if (!blockScript.has(location)) {
            SBConfig.ERROR_SCRIPT_FILE_CHECK.send(sbPlayer);
            return false;
        }
        if (!sortScripts(blockScript.get(location).getScript())) {
            SBConfig.ERROR_SCRIPT_EXECUTE.replace(scriptType).send(sbPlayer);
            SBConfig.CONSOLE_ERROR_SCRIPT_EXECUTE.replace(sbPlayer.getName(), location, scriptType).console();
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
        for (this.index = index; this.index < scripts.size(); this.index++) {
            if (!sbPlayer.isOnline()) {
                EndProcessManager.forEach(e -> e.failed(this));
                return false;
            }
            var script = scripts.get(this.index);
            var option = OptionManager.newInstance(script);
            this.value = Placeholder.INSTANCE.replace(getPlayer(), option.getValue(script));
            if (!option.callOption(this) && isFailedIgnore(option)) {
                return false;
            }
        }
        EndProcessManager.forEach(e -> e.success(this));
        new PlayerCountJson(sbPlayer.getUniqueId()).action(PlayerCount::add, location, scriptType);
        SBConfig.CONSOLE_SUCCESS_SCRIPT_EXECUTE.replace(sbPlayer.getName(), location, scriptType).console();
        return true;
    }
    
    protected boolean isFailedIgnore(@NotNull Option option) {
        StreamUtils.ifAction(!option.isFailedIgnore(), () -> EndProcessManager.forEach(e -> e.failed(this)));
        return true;
    }

    protected boolean sortScripts(@NotNull List<String> scripts) {
        try {
            var parse = new ArrayList<String>();
            for (var script : scripts) {
                parse.addAll(StringUtils.getScripts(script));
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