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

import com.github.yuttyann.scriptblockplus.script.ScriptRead;

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
    private final com.github.yuttyann.scriptblockplus.script.option.Option.Result result;
    private final ScriptRead scriptRead;

    /**
     * コンストラクタ
     * @param uuid - スクリプトの{@link UUID}
     * @param scriptRead - {@link ScriptRead}のインスタンス
     */
    public ScriptReadEndEvent(@NotNull UUID uuid, @NotNull com.github.yuttyann.scriptblockplus.script.option.Option.Result result, @NotNull ScriptRead scriptRead) {
        super(scriptRead.isAsynchronous());
        this.uuid = uuid;
        this.result = result;
        this.scriptRead = scriptRead;
    }

    /**
     * スクリプトの{@link UUID}を取得します。
     * @return {@link UUID} - スクリプトの{@link UUID}
     */
    @NotNull
    public UUID getUniqueId() {
        return uuid;
    }

    /**
     * スクリプトの実行結果を取得します。
     * @return {@code Result} - 成功した場合は{@code Result#SUCCESS}
     */
    @NotNull
    public com.github.yuttyann.scriptblockplus.script.option.Option.Result getResult() {
        return result;
    }

    /**
     * {@link ScriptRead}のインスタンスを取得します。
     * @return {@link ScriptRead} - インスタンス
     */
    @NotNull
    public ScriptRead getScriptRead() {
        return scriptRead;
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