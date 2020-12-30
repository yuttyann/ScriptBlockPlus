package com.github.yuttyann.scriptblockplus.script;

import com.github.yuttyann.scriptblockplus.enums.ActionType;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * ScriptBlockPlus ScriptEdit クラス
 * @author yuttyann44581
 */
public class ScriptEdit {

    private final ActionType actionType;
    private final ScriptType scriptType;

    public ScriptEdit(@NotNull ActionType actionType, @NotNull ScriptType scriptType) {
        this.actionType = actionType;
        this.scriptType = scriptType;
    }

    @NotNull
    public ScriptType getScriptType() {
        return scriptType;
    }

    @NotNull
    public ActionType getActionType() {
        return actionType;
    }

    public boolean perform(@NotNull SBPlayer sbPlayer, @Nullable Location location) {
        if (location == null) {
            return false;
        }
        var sbOperation = new SBOperation(scriptType);
        switch (actionType) {
            case CREATE:
                sbOperation.create(sbPlayer, location);
                break;
            case ADD:
                sbOperation.add(sbPlayer, location);
                break;
            case REMOVE:
                sbOperation.remove(sbPlayer, location);
                break;
            case VIEW:
                sbOperation.view(sbPlayer, location);
                break;
        }
        return true;
    }
}