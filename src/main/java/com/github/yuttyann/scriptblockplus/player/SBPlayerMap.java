package com.github.yuttyann.scriptblockplus.player;

import java.util.Optional;

import com.github.yuttyann.scriptblockplus.region.CuboidRegion;
import com.github.yuttyann.scriptblockplus.region.Region;
import com.github.yuttyann.scriptblockplus.script.SBClipboard;
import com.github.yuttyann.scriptblockplus.script.ScriptEdit;
import com.github.yuttyann.scriptblockplus.utils.collection.ObjectMap;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * ScriptBlockPlus SBPlayerMap インターフェース
 * @author yuttyann44581
 */
public interface SBPlayerMap {

    /**
     * プレイヤーのデータ構造を初期化します。
     */
    void clear();

    /**
     * プレイヤーのデータ構造を取得します。
     * @return {@link ObjectMap} - データ構造
     */
    @NotNull
    ObjectMap getObjectMap();

    /**
     * プレイヤーの選択範囲を取得します。
     * <p>
     * {@code WorldEdit}の{@code pos1}、{@code pos2}の様な機能です。
     * @return {@link Region} - 選択範囲
     */
    @NotNull
    CuboidRegion getCuboidRegion();

    /**
     * 編集情報を設定します。
     * @param scriptEdit - 編集情報
     */
    public void setScriptEdit(@Nullable ScriptEdit scriptEdit);

    /**
     * 編集情報を取得します。
     * @return {@link Optional}&lt;{@link ScriptEdit}&gt; - 編集情報
     */
    @NotNull
    public Optional<ScriptEdit> getScriptEdit();

    /**
     * クリップボードを設定します。
     * @param sbClipboard - クリップボード
     */
    public void setSBClipboard(@Nullable SBClipboard sbClipboard);

    /**
     * クリップボードを取得します。
     * @return {@link Optional}&lt;{@link SBClipboard}&gt; - クリップボード
     */
    @NotNull
    public Optional<SBClipboard> getSBClipboard();
}