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
package com.github.yuttyann.scriptblockplus.script;

import com.github.yuttyann.scriptblockplus.BlockCoords;
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
     * スクリプトキーを取得します。
     * @return {@link ScriptKey} - スクリプトキー
     */
    @NotNull
    ScriptKey getScriptKey();

    /**
     * {@link Bukkit}の{@link Location}を取得します。
     * @return {@link Location} - スクリプトの座標
     */
    @NotNull
    Location getLocation();

    /**
     * {@link ScriptBlock}の{@link BlockCoords}を取得します。
     * @return {@link BlockCoords} - スクリプトの座標
     */
    @NotNull
    BlockCoords getBlockCoords();

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
     * @return {@link Boolean} - 実行に成功した場合は{@code true}
     */
    boolean read(final int index);
}