package com.github.yuttyann.scriptblockplus.listener;

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
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * ScriptBlockPlus TriggerListener クラス
 * @param <E> イベントの型
 * @author yuttyann44581
 */
public abstract class TriggerListener<E extends Event> implements Listener {
    
    private final Plugin plugin;
    private final ScriptType scriptType;

    /**
     * コンストラクタ
     * @param plugin プラグイン
     * @param scriptType スクリプトの種類
     */
    public TriggerListener(@NotNull Plugin plugin, @NotNull ScriptType scriptType) {
        this.plugin = plugin;
        this.scriptType = scriptType;
    }

    /**
     * トリガーを登録します。
     * @param listener {@link TriggerListener} を実装したクラスのインスタンス
     */
    public static final <T extends TriggerListener<? extends Event>> void register(T listener) {
        Bukkit.getPluginManager().registerEvents(listener, listener.getPlugin());
    }
    
    /**
     * プラグインを取得します。
     * @return プラグイン
     */
    @NotNull
    public final Plugin getPlugin() {
        return plugin;
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
     * プロセスの途中で呼び出されます。
     * <pre>
     * 　
     * // 処理の進行度を取得します。
     * Progress progress = trigger.getProgress();
     * 
     * // 呼ばれるタイミング
     * Progress.PERM(権限の確認したタイミング)
     * Progress.EVENT(イベントを作成したタイミング)
     * Progress.READ(スクリプトを読み込んだタイミング)
     * 
     * // 使用例（イベントの作成時に絶対に処理が中断される）
     * switch (trigger.getProgress()) {
     *     case EVENT:
     *         return Result.FAILURE;
     *     default:
     *         return super.handle(trigger); // イベント以外はスルー
     * </pre>
     * @param trigger トリガー
     * @return {@link Result} <pre>SUCCESS(処理を中断しない), FAILURE(処理を中断する)</pre>
     */
    @NotNull
    protected Result interrupt(@NotNull Trigger trigger) {
        return Result.SUCCESS;
    }

    /**
     * トリガーを生成します。
     * <pre>
     * 　
     * // 実装例
     * &#064;EventHandler // BukkitAPI から呼ばれるようにする。
     * protected Trigger create(@NotNull Event event) {
     *     // トリガーは return の内部で生成する。
     *     return new Trigger(event.getPlayer(), event.getBlock(), event); 
     * }
     * </pre>
     * @param <E> イベント
     * @return {@link Trigger} トリガー（nullを返した場合はスルーする）
     */
    @Nullable
    protected abstract Trigger create(@NotNull E event);

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
            perform(event);
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

        private void perform(@NotNull E event) {
            var location = block.getLocation();
            if (!BlockScriptJson.has(location, scriptType)) {
                return;
            }
            if (!call(Progress.PERM, this) || !Permission.has(player, scriptType, false)) {
                SBConfig.NOT_PERMISSION.send(player);
                return;
            }
            this.triggerEvent = new TriggerEvent(player, block, scriptType);
            if (!call(Progress.EVENT, this) || isCancelled(triggerEvent)) {
                return;
            }
            this.scriptRead = new ScriptRead(player, location, scriptType);
            StreamUtils.ifAction(call(Progress.READ, this), () -> scriptRead.read(0));
        }

        private boolean call(@NotNull Progress progress, @NotNull Trigger trigger) {
            this.progress = progress;
            return interrupt(this).value;
        }
        
        private boolean isCancelled(@NotNull TriggerEvent triggerEvent) {
            Bukkit.getPluginManager().callEvent(triggerEvent);
            return triggerEvent.isCancelled();
        }
    }

    protected enum Progress {
        /**
         * パーミッションの処理
         */
        PERM,

        /**
         * イベントの生成処理
         */
        EVENT,

        /**
         * スクリプトの読み込み処理
         */
        READ;
    }

    protected enum Result {
        /**
         * 成功
         */
        SUCCESS(true),

        /**
         * 失敗
         */
        FAILURE(false);

        private final boolean value;

        Result(boolean value) {
            this.value = value;
        }
    }
}