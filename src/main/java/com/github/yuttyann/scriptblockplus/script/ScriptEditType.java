package com.github.yuttyann.scriptblockplus.script;

import com.github.yuttyann.scriptblockplus.enums.ActionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * ScriptBlockPlus ScriptEditType クラス
 * @author yuttyann44581
 */
public class ScriptEditType {

    private final ActionType actionType;
    private final ScriptType scriptType;

    public ScriptEditType(@NotNull ActionType actionType, @NotNull ScriptType scriptType) {
        this.actionType = actionType;
        this.scriptType = scriptType;
    }

    @NotNull
    public ActionType getActionType() {
        return actionType;
    }

    @NotNull
    public ScriptType getScriptType() {
        return scriptType;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof ScriptEditType) {
            ScriptEditType scriptEditType = (ScriptEditType) obj;
            return actionType.equals(scriptEditType.actionType) && scriptType.equals(scriptEditType.scriptType);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 1;
        int prime = 31;
        hash = prime * hash + actionType.hashCode();
        hash = prime * hash + scriptType.hashCode();
        return hash;
    }
}