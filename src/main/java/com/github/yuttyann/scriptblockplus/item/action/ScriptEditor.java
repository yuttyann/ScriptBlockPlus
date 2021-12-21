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
package com.github.yuttyann.scriptblockplus.item.action;

import com.github.yuttyann.scriptblockplus.enums.Permission;
import com.github.yuttyann.scriptblockplus.file.config.SBConfig;
import com.github.yuttyann.scriptblockplus.item.ChangeSlot;
import com.github.yuttyann.scriptblockplus.item.ItemAction;
import com.github.yuttyann.scriptblockplus.item.RunItem;
import com.github.yuttyann.scriptblockplus.script.SBClipboard;
import com.github.yuttyann.scriptblockplus.script.SBOperation;
import com.github.yuttyann.scriptblockplus.script.ScriptKey;
import com.github.yuttyann.scriptblockplus.script.option.chat.ActionBar;
import com.github.yuttyann.scriptblockplus.utils.Utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.permissions.Permissible;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * ScriptBlockPlus ScriptEditor クラス
 * @author yuttyann44581
 */
public final class ScriptEditor extends ItemAction {

    private static final String KEY = Utils.randomUUID();
    private static final String PREFIX = "§6§lToolMode: §d§l";

    public ScriptEditor() {
        super(Material.BLAZE_ROD, () -> "§dScript Editor", SBConfig.SCRIPT_EDITOR::setListColor);
        setItemFlags(ItemFlag.HIDE_ATTRIBUTES);
    }

    @Override
    public void run(@NotNull RunItem runItem) {
        var sbPlayer = runItem.getSBPlayer();
        var scriptKey = sbPlayer.getObjectMap().get(KEY, ScriptKey.INTERACT);
        var blockCoords = Optional.ofNullable(runItem.getBlockCoords());
        switch (runItem.getAction()) {
            case LEFT_CLICK_AIR: case LEFT_CLICK_BLOCK:
                if (runItem.isSneaking() && !runItem.isAIR() && blockCoords.isPresent()) {
                    new SBOperation(scriptKey).remove(sbPlayer, blockCoords.get());
                } else if (!runItem.isSneaking()) {
                    sbPlayer.getObjectMap().put(KEY, scriptKey = getNextType(scriptKey));
                    ActionBar.send(sbPlayer, PREFIX + scriptKey);
                }
                break;
            case RIGHT_CLICK_AIR: case RIGHT_CLICK_BLOCK:
                if (runItem.isSneaking() && !runItem.isAIR()) {
                    var sbClipboard = sbPlayer.getSBClipboard();
                    if (!blockCoords.isPresent() || !sbClipboard.isPresent() || !sbClipboard.get().paste(blockCoords.get(), true)) {
                        SBConfig.ERROR_SCRIPT_FILE_CHECK.send(sbPlayer);
                    }
                } else if (!runItem.isSneaking() && !runItem.isAIR() && blockCoords.isPresent()) {
                    new SBClipboard(sbPlayer, scriptKey, blockCoords.get()).copy();
                }
                break;
            default:
        }
    }

    @Override
    public void slot(@NotNull ChangeSlot changeSlot) {
        var sbPlayer = changeSlot.getSBPlayer();
        var scriptKey = sbPlayer.getObjectMap().get(KEY, ScriptKey.INTERACT);
        ActionBar.send(sbPlayer, PREFIX + scriptKey);
    }

    @Override
    public boolean hasPermission(@NotNull Permissible permissible) {
        return Permission.TOOL_SCRIPT_EDITOR.has(permissible);
    }

    @NotNull
    private ScriptKey getNextType(@NotNull ScriptKey scriptKey) {
        try {
            return ScriptKey.valueOf(scriptKey.ordinal() + 1);
        } catch (Exception e) {
            return ScriptKey.INTERACT;
        }
    }
}