package com.github.yuttyann.scriptblockplus.script;

import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * ScriptBlockPlus ScriptEdit クラス
 * @author yuttyann44581
 */
public class ScriptEdit {

    private final SBPlayer sbPlayer;

    public ScriptEdit(@NotNull SBPlayer sbPlayer) {
        this.sbPlayer = sbPlayer;
    }

    public boolean perform(@Nullable Location location) {
        if (location == null || !sbPlayer.getScriptEditType().isPresent()) {
            return false;
        }
        ScriptEditType scriptEditType = sbPlayer.getScriptEditType().get();
        ScriptAction scriptAction = new ScriptAction(scriptEditType.getScriptType());
        switch (scriptEditType.getActionType()) {
            case CREATE:
                scriptAction.create(sbPlayer, location);
                break;
            case ADD:
                scriptAction.add(sbPlayer, location);
                break;
            case REMOVE:
                scriptAction.remove(sbPlayer, location);
                break;
            case VIEW:
                scriptAction.view(sbPlayer, location);
                break;
        }
        return true;
    }
}