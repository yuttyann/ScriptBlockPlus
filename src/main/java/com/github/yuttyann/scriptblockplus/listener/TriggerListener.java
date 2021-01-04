package com.github.yuttyann.scriptblockplus.listener;

import java.lang.reflect.ParameterizedType;
import java.util.Optional;

import com.github.yuttyann.scriptblockplus.enums.Permission;
import com.github.yuttyann.scriptblockplus.event.TriggerEvent;
import com.github.yuttyann.scriptblockplus.file.config.SBConfig;
import com.github.yuttyann.scriptblockplus.file.json.BlockScriptJson;
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
 * 
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
        var type = getClass().getGenericSuperclass();
        var types = ((ParameterizedType) type).getActualTypeArguments();
        var genericClass = (Class<E>) null;
        try {
            genericClass = (Class<E>) Class.forName(types[0].getTypeName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        this.eventClass = genericClass;
    }

    /**
     * コンストラクタ
     * @param plugin プラグイン
     * @param scriptType スクリプトの種類
     * @param eventPriority イベントの優先度
     */
    public TriggerListener(@NotNull Plugin plugin, @NotNull ScriptType scriptType, @NotNull EventPriority eventPriority) {
        this.plugin = plugin;
        this.scriptType = scriptType;
        this.eventPriority = eventPriority;
    }

    /**
     * トリガーを登録します。
     * @param listener {@link TriggerListener} を実装したクラスのインスタンス
     */
    public static final void register(@NotNull TriggerListener<? extends Event> listener) {
        var plugin = listener.getPlugin();
        var eventClass = listener.getEventClass();
        var eventPriority = listener.getEventPriority();
        Bukkit.getPluginManager().registerEvent(eventClass, listener, eventPriority, (l, e) -> listener.onTrigger(e), plugin);
    }

    /**
     * プラグインを取得します。
     * @return プラグイン
     */
    @NotNull
    public Plugin getPlugin() {
        return plugin;
    }

    /**
     * イベントのクラスを取得します。
     * 
     * @return イベントのクラス
     */
    @NotNull
    public final Class<E> getEventClass() {
        return eventClass;
    }

    /**
     * スクリプトの種類を取得します。
     * @return スクリプトの種類
     */
    @NotNull
    public final ScriptType getScriptType() {
        return scriptType;
    }

    /**
     * イベントの優先度を取得します。
     * @return イベントの優先度
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
     * @param event イベント
     * @return {@link Trigger} トリガー（null を返した場合はスルーする）
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
     * @param trigger トリガー
     * @return {@link Result}
     * 
     *         <pre>
     * Result.SUCCESS = 処理を中断しない, Result.FAILURE = 処理を中断する
     *         </pre>
     */
    @NotNull
    protected Result handle(@NotNull Trigger trigger) {
        return Result.SUCCESS;
    }

    /**
     * トリガーの処理
     * @param event イベント
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
        if (!call(Progress.PERM, trigger) || !Permission.has(player, scriptType, false)) {
            SBConfig.NOT_PERMISSION.send(player);
            return;
        }
        trigger.triggerEvent = new TriggerEvent(player, block, scriptType);
        if (!call(Progress.EVENT, trigger) || isCancelled(trigger.triggerEvent)) {
            return;
        }
        trigger.scriptRead = new ScriptRead(player, location, scriptType);
        StreamUtils.ifAction(call(Progress.READ, trigger), () -> trigger.scriptRead.read(0));
    }

    private boolean call(@NotNull Progress progress, @NotNull Trigger trigger) {
        trigger.progress = progress;
        return handle(trigger) == Result.SUCCESS;
    }
    
    private boolean isCancelled(@NotNull TriggerEvent triggerEvent) {
        Bukkit.getPluginManager().callEvent(triggerEvent);
        return triggerEvent.isCancelled();
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
         * @param player プレイヤー
         * @param block ブロック
         * @param event イベント
         */
        public Trigger(@NotNull Player player, @NotNull Block block, @NotNull E event) {
            this.player = player;
            this.block = block;
            this.event = event;
        }

        /**
         * {@link Player} を取得します。
         * @return {@link Player}
         */
        @NotNull
        public Player getPlayer() {
            return player;
        }

        /**
         * {@link Block} を取得します。
         * @return {@link Block}
         */
        @NotNull
        public Block getBlock() {
            return block;
        }

        /**
         * {@link Event} を取得します。
         * @return {@link Event}
         */
        @NotNull
        public E getEvent() {
            return event;
        }

        /**
         * {@link Progress} を取得します。
         * @return {@link Progress}
         */
        @NotNull
        public Progress getProgress() {
            return progress;
        }

        /**
         * {@link SBRead} を取得します。
         * @return {@link SBRead}（オプショナル）
         */
        @NotNull
        public Optional<SBRead> getSBRead() {
            return Optional.ofNullable(scriptRead);
        }

        /**
         * {@link TriggerEvent} を取得します。
         * @return {@link TriggerEvent}（オプショナル）
         */
        @NotNull
        public Optional<TriggerEvent> getTriggerEvent() {
            return Optional.ofNullable(triggerEvent);
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