package com.github.yuttyann.scriptblockplus.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * ScriptBlockPlus FileReloadEvent イベントクラス
 * @author yuttyann44581
 */
public class FileReloadEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}