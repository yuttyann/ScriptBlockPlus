package com.github.yuttyann.scriptblockplus.listener;

import java.lang.reflect.ParameterizedType;
import java.util.Optional;
import java.util.UUID;

import com.github.yuttyann.scriptblockplus.enums.Permission;
import com.github.yuttyann.scriptblockplus.event.TriggerEvent;
import com.github.yuttyann.scriptblockplus.file.config.SBConfig;
import com.github.yuttyann.scriptblockplus.file.json.BlockScriptJson;
import com.github.yuttyann.scriptblockplus.player.ObjectMap;
import com.github.yuttyann.scriptblockplus.script.SBRead;
import com.github.yuttyann.scriptblockplus.script.ScriptRead;
import com.github.yuttyann.scriptblockplus.script.ScriptType;
import com.github.yuttyann.scriptblockplus.utils.StreamUtils;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
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

    private final Plugin plugin;
    private final Class<E> eventClass;
    private final ScriptType scriptType;
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
     * @param scriptType - スクリプトの種類
     * @param eventPriority - イベントの優先度
     */
    public TriggerListener(@NotNull Plugin plugin, @NotNull ScriptType scriptType, @NotNull EventPriority eventPriority) {
        this.plugin = plugin;
        this.scriptType = scriptType;
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
        Bukkit.getPluginManager().registerEvent(eventClass, listener, eventPriority, (l, e) -> listener.onTrigger(e), plugin);
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
     * スクリプトの種類を取得します。
     * @return {@link ScriptType} - スクリプトの種類
     */
    @NotNull
    public final ScriptType getScriptType() {
        return scriptType;
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
     * 
     * <pre>
     * 　
     * // 実装例
     * &#064;Override
     * &#064;Nullable
     * protected Trigger create(&#064;NotNull ****Event event) {
     *     return new Trigger(event.getPlayer(), event.getBlock(), event);
     * }
     * </pre>
     * 
     * @param event - イベント
     * @return {@link Trigger}（null を返した場合はスルーする）
     */
    @Nullable
    protected abstract Trigger create(@NotNull E event);

    /**
     * 各プロセスから呼び出されます。
     * <pre>
     * 　
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
     * 
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
        var block = trigger.getBlock();
        var location = block.getLocation();
        if (!BlockScriptJson.has(location, scriptType)) {
            return;
        }
        var player = trigger.getPlayer();
        if (!trigger.call(Progress.PERM) || !Permission.has(player, scriptType, false)) {
            SBConfig.NOT_PERMISSION.send(player);
            return;
        }
        trigger.triggerEvent = new TriggerEvent(player, block, scriptType);
        if (!trigger.call(Progress.EVENT) || trigger.isCancelled()) {
            return;
        }
        trigger.scriptRead = new ScriptRead(player, location, scriptType);
        StreamUtils.ifAction(trigger.call(Progress.READ), () -> trigger.scriptRead.read(0));
    }

    protected class Trigger {

        private final Player player;
        private final Block block;
        private final E event;

        private Progress progress;
        private ScriptRead scriptRead;
        private TriggerEvent triggerEvent;

        /**
         * コンストラクタ
         * @param player - プレイヤー
         * @param block - ブロック
         * @param event - イベント
         */
        public Trigger(@NotNull Player player, @NotNull Block block, @NotNull E event) {
            this.player = player;
            this.block = block;
            this.event = event;
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
         * @return {@link Block} - ブロック
         */
        @NotNull
        public Block getBlock() {
            return block;
        }

        /**
         * {@link Bukkit}のイベントを取得します。
         * @return {@link E} - イベント
         */
        @NotNull
        public E getEvent() {
            return event;
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