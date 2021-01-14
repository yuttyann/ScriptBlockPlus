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

import java.util.Objects;

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

    private final ScriptKey scriptKey;
    private final ActionType actionType;

    private String value;

    public ScriptEdit(@NotNull ScriptKey scriptKey, @NotNull ActionType actionType) {
        this.scriptKey = scriptKey;
        this.actionType = actionType;
    }

    public void setValue(@Nullable String value) {
        this.value = value;
    }

    @NotNull
    public ScriptKey getScriptKey() {
        return scriptKey;
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
            var sbOperation = new SBOperation(scriptKey);
            switch (actionType) {
                case CREATE:
                    sbOperation.create(player, location, value);
                    break;
                case ADD:
                    sbOperation.add(player, location, value);
                    break;
                case REMOVE:
                    sbOperation.remove(player, location);
                    break;
                case VIEW:
                    sbOperation.view(player, location);
                    break;
                case REDSTONE:
                    sbOperation.redstone(player, location, value);
                    break;
            }
        } finally {
            sbPlayer.setScriptEdit(null);
        }
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(scriptKey, actionType);
    }
}