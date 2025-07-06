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

import com.github.yuttyann.scriptblockplus.script.ScriptKey;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

/**
 * ScriptBlockPlus TriggerEvent イベントクラス
 * @author yuttyann44581
 */
public class TriggerEvent extends ScriptBlockEvent {

    private final Event event;
    private final ScriptKey scriptKey;

    private boolean cancelled;

    /**
     * コンストラクタ
     * @param player - プレイヤー
     * @param block - ブロック
     * @param event - 呼び出されたイベント
     * @param scriptKey - スクリプトキー
     */
    public TriggerEvent(@NotNull final Player player, @NotNull final Block block, @NotNull final Event event, @NotNull final ScriptKey scriptKey) {
        super(player, block);
        this.event = event;
        this.scriptKey = scriptKey;
    }

    /**
     * 呼び出されたイベントを取得します。
     * @return {@link Event} - イベント
     */
    @NotNull
    public Event getEvent() {
        return event;
    }

    /**
     * スクリプトキーを取得します。
     * @return {@link ScriptKey} - スクリプトキー
     */
    @NotNull
    public ScriptKey getScriptKey() {
        return scriptKey;
    }

    /**
     * アイテムの種類を取得します。
     * @param isMainHand - メインハンドの場合は{@code true}
     * @return {@link Material} - アイテムの種類
     */
    @NotNull
    public Material getMaterial(boolean isMainHand) {
        if (!hasItem(isMainHand)) {
            return Material.AIR;
        }
        return getItem(isMainHand).getType();
    }

    /**
     * アイテムを持っている場合は{@code true}を返します。
     * @param isMainHand - メインハンドの場合は{@code true}
     * @return {@code boolean} - アイテムを持っている場合は{@code true}
     */
    public boolean hasItem(boolean isMainHand) {
        return getItem(isMainHand) != null;
    }

    /**
     * ブロックを持っている場合は{@code true}を返します。
     * @param isMainHand - メインハンドの場合は{@code true}
     * @return {@code boolean} - ブロックを持っている場合は{@code true}
     */
    public boolean isBlockInHand(boolean isMainHand) {
        return hasItem(isMainHand) && getMaterial(isMainHand).isBlock();
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }
}