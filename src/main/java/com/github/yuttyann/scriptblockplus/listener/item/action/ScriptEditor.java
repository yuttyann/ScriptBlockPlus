package com.github.yuttyann.scriptblockplus.listener.item.action;

import com.github.yuttyann.scriptblockplus.enums.Permission;
import com.github.yuttyann.scriptblockplus.file.config.SBConfig;
import com.github.yuttyann.scriptblockplus.listener.item.ChangeSlot;
import com.github.yuttyann.scriptblockplus.listener.item.ItemAction;
import com.github.yuttyann.scriptblockplus.listener.item.RunItem;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.script.SBClipboard;
import com.github.yuttyann.scriptblockplus.script.SBOperation;
import com.github.yuttyann.scriptblockplus.script.ScriptType;
import com.github.yuttyann.scriptblockplus.script.option.chat.ActionBar;
import com.github.yuttyann.scriptblockplus.utils.ItemUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import org.bukkit.permissions.Permissible;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * ScriptBlockPlus ScriptEditor クラス
 * @author yuttyann44581
 */
public class ScriptEditor extends ItemAction {

    private static final String KEY = Utils.randomUUID();

    public ScriptEditor() {
        super(ItemUtils.getScriptEditor());
    }

    @Override
    public boolean hasPermission(@NotNull Permissible permissible) {
        return Permission.TOOL_SCRIPT_EDITOR.has(permissible);
    }

    @Override
    public void slot(@NotNull ChangeSlot changeSlot) {
        var sbPlayer = SBPlayer.fromPlayer(changeSlot.getPlayer());
        var scriptType = sbPlayer.getObjectMap().get(KEY, ScriptType.INTERACT);
        ActionBar.send(sbPlayer, "§6§lToolMode: §d§l" + scriptType);
    }

    @Override
    public void run(@NotNull RunItem runItem) {
        var sbPlayer = SBPlayer.fromPlayer(runItem.getPlayer());
        var location = Optional.ofNullable(runItem.getLocation());
        var scriptType = sbPlayer.getObjectMap().get(KEY, ScriptType.INTERACT);
        switch (runItem.getAction()) {
            case LEFT_CLICK_AIR:
            case LEFT_CLICK_BLOCK:
                if (runItem.isSneaking() && !runItem.isAIR() && location.isPresent()) {
                    new SBOperation(scriptType).remove(sbPlayer, location.get());
                } else if (!runItem.isSneaking()) {
                    sbPlayer.getObjectMap().put(KEY, scriptType = getNextType(scriptType));
                    ActionBar.send(sbPlayer, "§6§lToolMode: §d§l" + scriptType);
                }
                break;
            case RIGHT_CLICK_AIR:
            case RIGHT_CLICK_BLOCK:
                if (runItem.isSneaking() && !runItem.isAIR()) {
                    Optional<SBClipboard> clipboard = sbPlayer.getSBClipboard();
                    if (!location.isPresent() || !clipboard.isPresent() || !clipboard.get().paste(location.get(), true)) {
                        SBConfig.ERROR_SCRIPT_FILE_CHECK.send(sbPlayer);
                    }
                } else if (!runItem.isSneaking() && !runItem.isAIR() && location.isPresent()) {
                    new SBOperation(scriptType).clipboard(sbPlayer, location.get()).copy();
                }
                break;
            default:
        }
    }

    @NotNull
    private ScriptType getNextType(@NotNull ScriptType scriptType) {
        try {
            return ScriptType.valueOf(scriptType.ordinal() + 1);
        } catch (Exception e) {
            return ScriptType.INTERACT;
        }
    }
}