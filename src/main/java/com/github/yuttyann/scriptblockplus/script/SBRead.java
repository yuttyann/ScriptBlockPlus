package com.github.yuttyann.scriptblockplus.script;

import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.player.ObjectMap;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * ScriptBlockPlus SBRead インターフェース
 * @author yuttyann44581
 */
public interface SBRead extends ObjectMap {

    /**
     * {@link ScriptBlock}の{@link SBPlayer}を取得します。
     * @return {@link SBPlayer} - プレイヤー
     */
    @NotNull
    SBPlayer getSBPlayer();

    /**
     * {@link Bukkit}の{@link Player}を取得します。
     * @return {@link Player} - プレイヤー
     */
    @NotNull
    default Player getPlayer() {
        return getSBPlayer().getPlayer();
    }

    /**
     * スクリプトの座標を取得します。
     * @return {@link Location} - スクリプトの座標
     */
    @NotNull
    Location getLocation();

    /**
     * スクリプトキーを取得します。
     * @return {@link ScriptKey} - スクリプトキー
     */
    @NotNull
    ScriptKey getScriptKey();

    /**
     * スクリプトの一覧を取得します。
     * @return {@link List}&lt;{@link String}&gt; - スクリプトの一覧
     */
    @NotNull
    List<String> getScripts();

    /**
     * オプションの値を取得します。
     * @return {@link String} - オプションの値
     */
    @NotNull
    String getOptionValue();

    /**
     * スクリプトを何番目まで実行したのか取得します。
     * @return {@link Integer} - 進行度
     */
    int getScriptIndex();

    /**
     * スクリプトを実行します。
     * @param index - 開始位置
     * @return {@link Boolean} - 実行に成功した場合はtrue
     */
    boolean read(final int index);
}