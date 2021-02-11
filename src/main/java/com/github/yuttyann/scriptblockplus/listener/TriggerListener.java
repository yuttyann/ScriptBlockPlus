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
package com.github.yuttyann.scriptblockplus.listener;

import java.lang.reflect.ParameterizedType;
import java.util.Optional;
import java.util.UUID;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.enums.Permission;
import com.github.yuttyann.scriptblockplus.event.TriggerEvent;
import com.github.yuttyann.scriptblockplus.file.config.SBConfig;
import com.github.yuttyann.scriptblockplus.file.json.derived.BlockScriptJson;
import com.github.yuttyann.scriptblockplus.player.ObjectMap;
import com.github.yuttyann.scriptblockplus.script.SBRead;
import com.github.yuttyann.scriptblockplus.script.ScriptRead;
import com.github.yuttyann.scriptblockplus.script.ScriptKey;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * ScriptBlockPlus TriggerListener クラス
 * @param <E> イベントの型
 * @author yuttyann44581
 */
@SuppressWarnings("unchecked")
public abstract class TriggerListener<E extends Event> implements Listener {

    // イベントが呼ばれた際にTriggerListener#onTrigger(Event)が呼ばれるようにする。
    private static final EventExecutor EXECUTE = (l, e) -> ((TriggerListener<?>) l).onTrigger(e);

    private final Plugin plugin;
    private final Class<E> eventClass;
    private final ScriptKey scriptKey;
    private final EventPriority eventPriority;

    {
        var genericClass = (Class<E>) null;
        try {
            var type = getClass().getGenericSuperclass();
            var args = ((ParameterizedType) type).getActualTypeArguments();
            genericClass = (Class<E>) Class.forName(args[0].getTypeName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        this.eventClass = genericClass;
    }

    /**
     * コンストラクタ
     * @param plugin - プラグイン
     * @param scriptKey - スクリプトキー
     * @param eventPriority - イベントの優先度
     */
    public TriggerListener(@NotNull Plugin plugin, @NotNull ScriptKey scriptKey, @NotNull EventPriority eventPriority) {
        this.plugin = plugin;
        this.scriptKey = scriptKey;
        this.eventPriority = eventPriority;
    }

    /**
     * トリガーを登録します。
     * @param listener - {@link TriggerListener}を実装したクラスのインスタンス
     */
    public static void register(@NotNull TriggerListener<? extends Event> listener) {
        var plugin = listener.getPlugin();
        var eventClass = listener.getEventClass();
        var eventPriority = listener.getEventPriority();
        Bukkit.getPluginManager().registerEvent(eventClass, listener, eventPriority, EXECUTE, plugin);
    }

    /**
     * プラグインを取得します。
     * @return {@link Plugin} - プラグイン
     */
    @NotNull
    public Plugin getPlugin() {
        return plugin;
    }

    /**
     * {@link Bukkit}のイベントのクラスを取得します。
     * @return Class&lt;{@link E} extends {@link Event}&gt; - イベントのクラス
     */
    @NotNull
    public final Class<E> getEventClass() {
        return eventClass;
    }

    /**
     * スクリプトキーを取得します。
     * @return {@link ScriptKey} - スクリプトキー
     */
    @NotNull
    public final ScriptKey getScriptKey() {
        return scriptKey;
    }

    /**
     * イベントの優先度を取得します。
     * @return {@link EventPriority} - イベントの優先度
     */
    @NotNull
    public EventPriority getEventPriority() {
        return eventPriority;
    }

    /**
     * トリガーを生成します。
     * <p>
     * また、{@code null}を返した場合は処理を行わずに終了します。
     * @apiNote
     * <pre>
     * 実装例です。
     * &#064;Override
     * &#064;Nullable
     * protected Trigger create(&#064;NotNull ExampleEvent event) {
     *     return new Trigger(event.getPlayer(), event.getBlock(), event);
     * }
     * </pre>
     * @param event - イベント
     * @return {@link Trigger} - トリガー
     */
    @Nullable
    protected abstract Trigger create(@NotNull E event);

    /**
     * 各プロセスから呼び出されます。
     * @apiNote
     * <pre>
     * 実装例です。
     * // プロセスの進行度
     * Progress.PERM  = パーミッションの判定
     * Progress.EVENT = イベントの生成
     * Progress.READ  = スクリプトの実行
     *
     * // 進行度を取得します。
     * Progress progress = trigger.getProgress();
     *
     * // 実装例（イベントの作成時に絶対に処理が中断される）
     * &#064;Override
     * &#064;NotNull
     * protected Result handle(&#064;NotNull Trigger trigger) {
     *     switch (trigger.getProgress()) {
     *         case EVENT:
     *             return Result.FAILURE;
     *         default:
     *             return super.handle(trigger); // Progress.EVENT 以外はスルー
     *     }
     * }
     * </pre>
     * @param trigger - トリガー
     * @return {@link Result} - {@link Result#FAILURE}の場合は処理を中断します。
     */
    @NotNull
    protected Result handle(@NotNull Trigger trigger) {
        return Result.SUCCESS;
    }

    /**
     * トリガーの処理です。
     * @param event - イベント
     */
    private void onTrigger(@NotNull Event event) {
        if (!eventClass.equals(event.getClass())) {
            return;
        }
        var trigger = create((E) event);
        if (trigger == null) {
            return;
        }
        var blockCoords = trigger.getBlockCoords();
        if (!BlockScriptJson.contains(scriptKey, blockCoords)) {
            return;
        }
        var player = trigger.getPlayer();
        trigger.block = blockCoords.getBlock();
        if (!trigger.call(Progress.PERM) || !Permission.has(player, scriptKey, false)) {
            SBConfig.NOT_PERMISSION.send(player);
            return;
        }
        trigger.triggerEvent = new TriggerEvent(player, trigger.getBlock(), scriptKey);
        if (!trigger.call(Progress.EVENT) || trigger.isCancelled()) {
            return;
        }
        trigger.scriptRead = new ScriptRead(player, blockCoords, scriptKey);
        if (trigger.call(Progress.READ)) {
            trigger.scriptRead.read(0);
        }
    }

    protected final class Trigger {

        private final E event;
        private final Player player;
        private final BlockCoords blockCoords;

        private Block block;
        private Progress progress;
        private ScriptRead scriptRead;
        private TriggerEvent triggerEvent;

        /**
         * コンストラクタ
         * @param event - イベント
         * @param player - プレイヤー
         * @param blockCoords - ブロック
         */
        public Trigger(@NotNull E event, @NotNull Player player, @NotNull BlockCoords blockCoords) {
            this.event = event;
            this.player = player;
            this.blockCoords = blockCoords;
        }

        /**
         * {@link Bukkit}のイベントを取得します。
         * @return {@link Event} - イベント
         */
        @NotNull
        public E getEvent() {
            return event;
        }

        /**
         * {@link Bukkit}の{@link Player}を取得します。
         * @return {@link Player} - プレイヤー
         */
        @NotNull
        public Player getPlayer() {
            return player;
        }

        /**
         * ブロックを取得します。
         * @return {@link Block} - ブロックを取得します。
         */
        @NotNull
        public Block getBlock() {
            return block;
        }

        /**
         * ブロックの座標を取得します。
         * @return {@link Block} - ブロックの座標を取得します。
         */
        @NotNull
        public BlockCoords getBlockCoords() {
            return blockCoords;
        }

        /**
         * プロセスの進行度を取得します。
         * @return {@link Progress} - 進行度
         */
        @NotNull
        public Progress getProgress() {
            return progress;
        }

        /**
         * {@link SBRead}の{@link ObjectMap}を取得します。
         * <p>
         * {@link UUID}によって管理されているため、重複することはありません。
         * <p>
         * 一時的なデータなため、終了後に初期化されます。
         * @return {@link ObjectMap} - データ構造
         */
        @NotNull
        public Optional<ObjectMap> getTempMap() {
            return Optional.ofNullable(scriptRead);
        }

        /**
         * トリガーイベントを取得します。
         * @return {@link Optional}&lt;{@link TriggerEvent}&gt; - トリガーイベント
         */
        @NotNull
        public Optional<TriggerEvent> getTriggerEvent() {
            return Optional.ofNullable(triggerEvent);
        }

        private boolean call(@NotNull Progress progress) {
            this.progress = progress;
            return handle(this) == Result.SUCCESS;
        }
        
        private boolean isCancelled() {
            Bukkit.getPluginManager().callEvent(triggerEvent);
            return triggerEvent.isCancelled();
        }
    }

    protected enum Progress {

        /**
         * パーミッションの判定
         */
        PERM,

        /**
         * イベントの生成
         */
        EVENT,

        /**
         * スクリプトの実行
         */
        READ;
    }

    protected enum Result {

        /**
         * 成功
         */
        SUCCESS,

        /**
         * 失敗
         */
        FAILURE;
    }
}