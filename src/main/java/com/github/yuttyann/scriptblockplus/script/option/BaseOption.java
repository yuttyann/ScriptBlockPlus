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

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.enums.Permission;
import com.github.yuttyann.scriptblockplus.file.config.ConfigKey;
import com.github.yuttyann.scriptblockplus.file.config.SBConfig;
import com.github.yuttyann.scriptblockplus.hook.plugin.Placeholder;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.script.ScriptRead;
import com.github.yuttyann.scriptblockplus.script.ScriptKey;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import com.github.yuttyann.scriptblockplus.utils.collection.ObjectMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;
import java.util.function.UnaryOperator;

/**
 * ScriptBlockPlus BaseOption オプションクラス
 * @author yuttyann44581
 */
public abstract class BaseOption extends Option {

    private static final UnaryOperator<String> ESCAPE = Placeholder.INSTANCE::escape;

    private ScriptRead scriptRead;

    /**
     * {@link ScriptRead}の{@link ObjectMap}を取得します。
     * <p>
     * {@link UUID}によって管理されているため、重複することはありません。
     * <p>
     * 一時的なデータなため、終了後に初期化されます。
     * @return {@link ObjectMap} - データ構造
     */
    @NotNull
    protected final ObjectMap getTempMap() {
        return scriptRead;
    }

    /**
     * {@code BukkitAPI}の{@code org.bukkit.entity.Player}を取得します。
     * @return {@link Player} - プレイヤー
     */
    @NotNull
    protected final Player getPlayer() {
        return scriptRead.getPlayer();
    }

    /**
     * プレイヤーを取得します。
     * @return {@link SBPlayer} - プレイヤー
     */
    @NotNull
    protected final SBPlayer getSBPlayer() {
        return scriptRead.getSBPlayer();
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
     * スクリプトキーを取得します。
     * @return {@link ScriptKey} - スクリプトキー
     */
    @NotNull
    protected final ScriptKey getScriptKey() {
        return scriptRead.getScriptKey();
    }

    /**
     * {@code BukkitAPI}の{@code org.bukkit.Location}を取得します。
     * @return {@link Location} - スクリプトの座標
     */
    @NotNull
    protected final Location getLocation() {
        return scriptRead.getLocation();
    }
    
    /**
     * スクリプトの座標を取得します。
     * @return {@link BlockCoords} - スクリプトの座標
     */
    @NotNull
    protected final BlockCoords getBlockCoords() {
        return scriptRead.getBlockCoords();
    }

    /**
     * スクリプトの一覧を取得します。
     * @return {@link List}&lt;{@link String}&gt; - スクリプトの一覧
     */
    @NotNull
    protected final List<String> getScripts() {
        return scriptRead.getScripts();
    }

    /**
     * オプションの値を取得します。
     * @return {@link String} - オプションの値
     */
    @NotNull
    protected final String getOptionValue() {
        return scriptRead.getOptionValue();
    }

    /**
     * スクリプトを何番目まで実行したのか取得します。
     * @return {@code int} - 進行度
     */
    protected final int getScriptIndex() {
        return scriptRead.getScriptIndex();
    }

    /**
     * オプションの結果を反転するのかどうか。
     * @return {@code boolean} - 反転する場合は{@code true}
     */
    protected final boolean isInverted() {
        return scriptRead.isInverted();
    }

    /**
     * オプションの結果が反転しない場合のみ、メッセージを送信します。
     * <p>
     * 結果を反転した場合に、メッセージを表示しないようにする際にご利用ください。
     * @param configKey - コンフィグキー
     */
    protected final void sendMessage(@NotNull ConfigKey<String> configKey) {
        if (!isInverted()) {
            Utils.sendColorMessage(getPlayer(), configKey.toString());
        }
    }

    /**
     * オプションの結果が反転しない場合のみ、コンソールにメッセージを送信します。
     * <p>
     * 結果を反転した場合に、メッセージを表示しないようにする際にご利用ください。
     * @param configKey - コンフィグキー
     */
    protected final void sendConsoleMessage(@NotNull ConfigKey<String> configKey) {
        if (!isInverted()) {
            Utils.sendColorMessage(Bukkit.getConsoleSender(), configKey.toString());
        }
    }

    /**
     * エスケープ文字を置換します。
     * @param source - 文字列
     * @return {@link String} - 置換後の文字列
     * @apiNote {@link Placeholder#escape(String)}
     */
    @NotNull
    protected final String escape(@NotNull String source) {
        return ESCAPE.apply(source);
    }

    /**
     * エスケープ文字を置換します。
     * @param list - リスト
     * @return {@link String} - 置換後のリスト
     * @apiNote {@link Placeholder#escape(String)}
     */
    @NotNull
    protected final List<String> escapes(@NotNull List<String> list) {
        if (!list.isEmpty()) {
            list.replaceAll(ESCAPE);
        }
        return list;
    }

    /**
     * カラーコードを置換します。
     * @param source - 文字列
     * @param escape - エスケープ文字を置換するのかどうか
     * @return {@link String} - 置換後の文字列
     * @apiNote {@link Placeholder#escape(String)}
     */
    @NotNull
    protected final String setColor(@Nullable String source, final boolean escape) {
        return ESCAPE.apply(StringUtils.setColor(source));
    }

    /**
     * 文字列を分割します。
     * @param source - 文字列
     * @param delimiter - 区切り
     * @param escape - エスケープ文字を置換するのかどうか
     * @return {@link String} - 分割された文字列
     * @apiNote {@link Placeholder#escape(String)}
     */
    @NotNull
    protected final List<String> split(@Nullable String source, @NotNull char delimiter, final boolean escape) {
        return escape ? escapes(StringUtils.split(source, delimiter)) : StringUtils.split(source, delimiter);
    }

    /**
     * オプションの処理を実行します。
     * @throws Exception オプションの処理内で例外が発生した時にスローされます。
     * @return {@code boolean} - 有効な場合は{@code true}
     */
    protected abstract boolean isValid() throws Exception;

    /**
     * オプションの処理を実行します。
     * @param scriptRead - {@link ScriptRead}
     * @return {@code boolean} - 有効な場合は{@code true}
     */
    @Override
    @Deprecated
    public final boolean callOption(@NotNull ScriptRead scriptRead) {
        var sbPlayer = scriptRead.getSBPlayer();
        if (!sbPlayer.isOnline()) {
            return false;
        }
        var player = sbPlayer.getPlayer();
        if (SBConfig.OPTION_PERMISSION.getValue() && !Permission.has(player, PERMISSION_ALL, getPermissionNode())) {
            SBConfig.NOT_PERMISSION.send(player);
            return false;
        }
        this.scriptRead = scriptRead;
        try {
            return isValid();
        } catch (Exception e) {
            e.printStackTrace();
            SBConfig.OPTION_FAILED_TO_EXECUTE.replace(this, e).send(player);
        }
        return false;
    }
}