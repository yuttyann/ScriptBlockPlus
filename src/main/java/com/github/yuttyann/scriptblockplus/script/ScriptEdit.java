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
package com.github.yuttyann.scriptblockplus.script;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.enums.ActionKey;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * ScriptBlockPlus ScriptEdit クラス
 * @author yuttyann44581
 */
public final class ScriptEdit {

    private final ScriptKey scriptKey;
    private final ActionKey actionKey;

    private String value;

    public ScriptEdit(@NotNull ScriptKey scriptKey, @NotNull ActionKey actionKey) {
        this.scriptKey = scriptKey;
        this.actionKey = actionKey;
    }

    public void setValue(@Nullable String value) {
        this.value = value;
    }

    @NotNull
    public ScriptKey getScriptKey() {
        return scriptKey;
    }

    @NotNull
    public ActionKey getActionKey() {
        return actionKey;
    }

    public boolean perform(@NotNull SBPlayer sbPlayer, @Nullable BlockCoords blockCoords) {
        if (blockCoords == null) {
            return false;
        }
        try {
            var player = sbPlayer.getPlayer();
            var sbOperation = new SBOperation(scriptKey);
            switch (actionKey) {
                case CREATE:
                    sbOperation.create(player, blockCoords, value);
                    break;
                case ADD:
                    sbOperation.add(player, blockCoords, value);
                    break;
                case REMOVE:
                    sbOperation.remove(player, blockCoords);
                    break;
                case VIEW:
                    sbOperation.view(player, blockCoords);
                    break;
                case NAMETAG:
                    sbOperation.nameTag(player, blockCoords, value);
                    break;
                case REDSTONE:
                    sbOperation.redstone(player, blockCoords, value);
                    break;
            }
        } finally {
            sbPlayer.setScriptEdit(null);
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 1;
        int prime = 31;
        hash = prime * hash + scriptKey.hashCode();
        hash = prime * hash + actionKey.hashCode();
        return hash;
    }
}