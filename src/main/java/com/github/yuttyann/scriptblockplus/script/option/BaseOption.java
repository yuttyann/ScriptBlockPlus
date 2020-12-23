package com.github.yuttyann.scriptblockplus.script.option;

import com.github.yuttyann.scriptblockplus.file.config.SBConfig;
import com.github.yuttyann.scriptblockplus.player.ObjectMap;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.script.SBRead;
import com.github.yuttyann.scriptblockplus.script.ScriptType;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

/**
 * ScriptBlockPlus BaseOption オプションクラス
 * @author yuttyann44581
 */
public abstract class BaseOption extends Option {

    private SBRead sbRead;

    /**
     * ScriptBlockPlusの{@link SBPlayer}を取得します。
     * @return {@link SBPlayer}
     */
    @NotNull
    protected final SBPlayer getSBPlayer() {
        return sbRead.getSBPlayer();
    }

    /**
     * BukkitAPIの{@link Player}を取得します。
     * @return {@link Player}
     */
    @NotNull
    protected final Player getPlayer() {
        return sbRead.getSBPlayer().getPlayer();
    }

    /**
     * プレイヤーの{@link UUID}を取得します。
     * @return {@link UUID}
     */
    @NotNull
    protected final UUID getUniqueId() {
        return getSBPlayer().getUniqueId();
    }

    /**
     * オプションの値を取得します。
     * @return オプションの値
     */
    @NotNull
    protected final String getOptionValue() {
        return sbRead.getOptionValue();
    }

    /**
     * スクリプトの座標を取得します。
     * @return スクリプトの座標
     */
    @NotNull
    protected final Location getLocation() {
        return sbRead.getLocation();
    }

    /**
     * スクリプトのリストを取得します。
     * @return スクリプトのリスト
     */
    @NotNull
    protected final List<String> getScripts() {
        return sbRead.getScript();
    }

    /**
     * スクリプトの種類を取得します。
     * @return スクリプトの種類
     */
    @NotNull
    protected final ScriptType getScriptType() {
        return sbRead.getScriptType();
    }

    /**
     * スクリプトの一時データ構造を取得します。
     * @return 一時的なデータ構造（全ての実行が終了したら初期化されます。）
     */
    @NotNull
    protected final ObjectMap getTempMap() {
        return sbRead;
    }

    /**
     * スクリプトを何番目まで実行したのか取得します。
     * @return 進行度
     */
    protected final int getScriptIndex() {
        return sbRead.getScriptIndex();
    }

    /**
     * オプションの処理を実行します。
     * @throws Exception オプションの処理内で例外が発生した時にスローされます。
     * @return 有効な場合はtrue
     */
    protected abstract boolean isValid() throws Exception;

    /**
     * オプションを呼び出します。
     * @param sbRead {@link SBRead}
     * @return 有効な場合はtrue
     */
    @Override
    @Deprecated
    public final boolean callOption(@NotNull SBRead sbRead) {
        this.sbRead = sbRead;
        try {
            return isValid();
        } catch (Exception e) {
            e.printStackTrace();
            SBConfig.OPTION_FAILED_TO_EXECUTE.replace(this, e).send(getSBPlayer());
        }
        return false;
    }
}