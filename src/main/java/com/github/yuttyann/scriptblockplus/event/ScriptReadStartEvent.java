package com.github.yuttyann.scriptblockplus.event;

import java.util.UUID;

import com.github.yuttyann.scriptblockplus.script.SBRead;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * ScriptBlockPlus ScriptReadStartEvent イベントクラス
 * @author yuttyann44581
 */
public class ScriptReadStartEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final UUID uuid;
    private final SBRead sbRead;

    public ScriptReadStartEvent(@NotNull UUID uuid, @NotNull SBRead sbRead) {
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