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
package com.github.yuttyann.scriptblockplus.script.option;

import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.enums.Permission;
import com.github.yuttyann.scriptblockplus.file.config.SBConfig;
import com.github.yuttyann.scriptblockplus.player.ObjectMap;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.script.SBRead;
import com.github.yuttyann.scriptblockplus.script.ScriptKey;

import org.bukkit.Bukkit;
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
     * {@link ScriptBlock}の{@link SBPlayer}を取得します。
     * @return {@link SBPlayer} - プレイヤー
     */
    @NotNull
    protected final SBPlayer getSBPlayer() {
        return sbRead.getSBPlayer();
    }

    /**
     * {@link Bukkit}の{@link Player}を取得します。
     * @return {@link Player} - プレイヤー
     */
    @NotNull
    protected final Player getPlayer() {
        return sbRead.getPlayer();
    }

    /**
     * プレイヤーの{@link UUID}を取得します。
     * @return {@link UUID} - プレイヤーの{@link UUID}
     */
    @NotNull
    protected final UUID getUniqueId() {
        return getSBPlayer().getUniqueId();
    }

    /**
     * オプションの値を取得します。
     * @return {@link String} - オプションの値
     */
    @NotNull
    protected final String getOptionValue() {
        return sbRead.getOptionValue();
    }

    /**
     * スクリプトの座標を取得します。
     * @return {@link Location} - スクリプトの座標
     */
    @NotNull
    protected final Location getLocation() {
        return sbRead.getLocation();
    }

    /**
     * スクリプトの一覧を取得します。
     * @return {@link List}&lt;{@link String}&gt; - スクリプトの一覧
     */
    @NotNull
    protected final List<String> getScripts() {
        return sbRead.getScripts();
    }

    /**
     * スクリプトキーを取得します。
     * @return {@link ScriptKey} - スクリプトキー
     */
    @NotNull
    protected final ScriptKey getScriptKey() {
        return sbRead.getScriptKey();
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
    protected final ObjectMap getTempMap() {
        return sbRead;
    }

    /**
     * スクリプトを何番目まで実行したのか取得します。
     * @return {@link Integer} - 進行度
     */
    protected final int getScriptIndex() {
        return sbRead.getScriptIndex();
    }

    /**
     * オプションの処理を実行します。
     * @throws Exception オプションの処理内で例外が発生した時にスローされます。
     * @return {@link Boolean} - 有効な場合はtrue
     */
    protected abstract boolean isValid() throws Exception;

    /**
     * オプションの処理を実行します。
     * @param sbRead - {@link SBRead}
     * @return {@link Boolean} - 有効な場合はtrue
     */
    @Override
    @Deprecated
    public final boolean callOption(@NotNull SBRead sbRead) {
        var player = sbRead.getPlayer();
        if (SBConfig.OPTION_PERMISSION.getValue() && !Permission.has(player, PERMISSION_ALL, getPermissionNode())) {
            SBConfig.NOT_PERMISSION.send(player);
            return false;
        }
        this.sbRead = sbRead;
        try {
            return isValid();
        } catch (Exception e) {
            e.printStackTrace();
            SBConfig.OPTION_FAILED_TO_EXECUTE.replace(this, e).send(player);
        }
        return false;
    }
}