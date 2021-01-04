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

    private String script;

    public ScriptEdit(@NotNull ActionType actionType, @NotNull ScriptType scriptType) {
        this.actionType = actionType;
        this.scriptType = scriptType;
    }

    public void setScriptLine(@Nullable String script) {
        this.script = script;
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
        try {
            var player = sbPlayer.getPlayer();
            var sbOperation = new SBOperation(scriptType);
            switch (actionType) {
                case CREATE:
                    sbOperation.create(player, location, script);
                    break;
                case ADD:
                    sbOperation.add(player, location, script);
                    break;
                case REMOVE:
                    sbOperation.remove(player, location);
                    break;
                case VIEW:
                    sbOperation.view(player, location);
                    break;
            }
        } finally {
            sbPlayer.setScriptEdit(null);
        }
        return true;
    }
}