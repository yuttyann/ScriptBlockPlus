package com.github.yuttyann.scriptblockplus.listener.item.action;

import com.github.yuttyann.scriptblockplus.enums.Permission;
import com.github.yuttyann.scriptblockplus.file.config.SBConfig;
import com.github.yuttyann.scriptblockplus.listener.item.ItemAction;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.script.ScriptEdit;
import com.github.yuttyann.scriptblockplus.script.ScriptType;
import com.github.yuttyann.scriptblockplus.utils.ItemUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permissible;
import org.jetbrains.annotations.NotNull;

/**
 * ScriptBlockPlus ScriptEditor クラス
 * @author yuttyann44581
 */
public class ScriptEditor extends ItemAction {

    private final ScriptType scriptType;

    public ScriptEditor(@NotNull ScriptType scriptType) {
        super(ItemUtils.getScriptEditor(scriptType));
        this.scriptType = scriptType;
    }

    @Override
    public boolean hasPermission(@NotNull Permissible permissible) {
        return Permission.TOOL_SCRIPT_EDITOR.has(permissible);
    }

    @Override
    public boolean run() {
        SBPlayer sbPlayer = SBPlayer.fromPlayer(player);
        switch (action) {
            case LEFT_CLICK_AIR:
            case LEFT_CLICK_BLOCK:
                if (isSneaking && !isAIR) {
                    new ScriptEdit(scriptType).remove(sbPlayer, location);
                } else if (!isSneaking) {
                    player.getInventory().setItemInMainHand(ItemUtils.getScriptEditor(getNextScriptType(item)));
                    Utils.updateInventory(player);
                }
                break;
            case RIGHT_CLICK_AIR:
            case RIGHT_CLICK_BLOCK:
                if (isSneaking && !isAIR) {
                    if (!sbPlayer.getClipboard().isPresent() || !sbPlayer.getClipboard().get().paste(location, true)) {
                        SBConfig.ERROR_SCRIPT_FILE_CHECK.send(sbPlayer);
                    }
                } else if (!isSneaking && !isAIR) {
                    new ScriptEdit(ItemUtils.getScriptType(item)).clipboard(sbPlayer, location).copy();
                }
                break;
            default:
        }
        return true;
    }

    @NotNull
    private ScriptType getNextScriptType(@NotNull ItemStack item) {
        try {
            return ScriptType.valueOf(ItemUtils.getScriptType(item).ordinal() + 1);
        } catch (Exception e) {
            return ScriptType.valueOf(0);
        }
    }
}