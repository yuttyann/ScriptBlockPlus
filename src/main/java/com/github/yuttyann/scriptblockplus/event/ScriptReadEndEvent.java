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
package com.github.yuttyann.scriptblockplus.event;

import java.util.UUID;

import com.github.yuttyann.scriptblockplus.script.SBRead;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * ScriptBlockPlus ScriptReadEndEvent イベントクラス
 * @author yuttyann44581
 */
public class ScriptReadEndEvent extends Event {
    
    private static final HandlerList HANDLERS = new HandlerList();

    private final UUID uuid;
    private final SBRead sbRead;

    public ScriptReadEndEvent(@NotNull UUID uuid, @NotNull SBRead sbRead) {
        this.uuid = uuid;
        this.sbRead = sbRead;
    }

    @NotNull
    public UUID getUniqueId() {
        return uuid;
    }
    
    @NotNull
    public SBRead getSBRead() {
        return sbRead;
    }

    @Override
    @NotNull
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}