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
package com.github.yuttyann.scriptblockplus;

import com.github.yuttyann.scriptblockplus.script.ScriptKey;
import com.github.yuttyann.scriptblockplus.script.endprocess.EndProcess;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.script.option.Option;
import com.github.yuttyann.scriptblockplus.script.option.OptionIndex;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * ScriptBlockPlus ScriptBlockAPI インターフェース
 * @author yuttyann44581
 */
public interface ScriptBlockAPI {

    /**
     * 指定した位置からスクリプトを実行します。
     * @param player - プレイヤー
     * @param location - スクリプトの座標
     * @param scriptKey - スクリプトキー
     * @param index - 開始位置
     * @return {@link Boolean} - 正常に終了した場合は{@code true}
     */
    boolean read(@NotNull Player player, @NotNull Location location, @NotNull ScriptKey scriptKey, int index);

    /**
     * 指定したオプションを登録します。
     * @param optionIndex - オプションの追加位置
     * @param newInstance - インスタンスを生成する処理
     */
    void registerOption(@NotNull OptionIndex optionIndex, @NotNull Supplier<Option> newInstance);

    /**
     * 指定したオプションを削除します。
     * @param optionClass - オプションのクラス
     */
    void unregisterOption(@NotNull Class<? extends BaseOption> optionClass);

    /**
     * 指定したエンドプロセスを登録します。
     * @param newInstance - インスタンスを生成する処理
     */
    void registerEndProcess(@NotNull Supplier<EndProcess> newInstance);

    /**
     * {@link SBEdit}を取得します。
     * @param scriptKey - スクリプトキー
     * @return {@link SBEdit}
     */
    @NotNull
    SBEdit getSBEdit(@NotNull ScriptKey scriptKey);

    /**
     * ScriptBlockPlus SBEdit インターフェース
     * @author yuttyann44581
     */
    interface SBEdit {

        /**
         * スクリプトキーを取得します。
         * @return {@link ScriptKey} - スクリプトキー
         */
        @NotNull
        ScriptKey getScriptKey();

        /**
         * 指定した座標にスクリプトを作成します。
         * @param player - プレイヤー
         * @param location - スクリプトの座標
         * @param script - スクリプト
         */
        void create(@NotNull OfflinePlayer player, @NotNull Location location, @NotNull String script);

        /**
         * 指定した座標にスクリプトを追加します。
         * @param player - プレイヤー
         * @param location - スクリプトの座標
         * @param script - スクリプト
         * @return {@code boolean} - 設定に成功した場合は{@code true}
         */
        boolean add(@NotNull OfflinePlayer player, @NotNull Location location, @NotNull String script);

        /**
         * 指定した座標のスクリプトを削除します。
         * @param player - プレイヤー
         * @param location - スクリプトの座標
         * @return {@code boolean} - 設定に成功した場合は{@code true}
         */
        boolean remove(@NotNull OfflinePlayer player, @NotNull Location location);

        /**
         * 指定した座標のスクリプトにネームタグを設定します。
         * @param player - プレイヤー
         * @param location - スクリプトの座標
         * @param nametag - ネームタグ
         * @return {@code boolean} - 設定に成功した場合は{@code true}
         */
        boolean nameTag(@NotNull OfflinePlayer player, @NotNull Location location, @Nullable String nametag);
    
        /**
         * 指定した座標のスクリプトにターゲットセレクターを設定します。
         * @param player - プレイヤー
         * @param location - スクリプトの座標
         * @param selector - ターゲットセレクター
         * @return {@code boolean} - 設定に成功した場合は{@code true}
         */
        boolean redstone(@NotNull OfflinePlayer player, @NotNull Location location, @Nullable String selector);
    }

    /**
     * {@link SBFile}を取得します。
     * @param location - スクリプトの座標
     * @param scriptKey - スクリプトキー
     * @return {@link SBFile}
     */
    SBFile getSBFile(@NotNull ScriptKey scriptKey, @NotNull Location location);

    /**
     * ScriptBlockPlus SBFile インターフェース
     * @author yuttyann44581
     */
    interface SBFile {

        /**
         * 変更を保存します。
         */
        void save();

        /**
         * 再読み込みを行います。
         */
        void reload();

        /**
         * スクリプトが存在するのか確認します。
         * @return {@link Boolean} - スクリプトが存在する場合は{@code true}
         */
        boolean has();

        /**
         * スクリプトキーを取得します。
         * @return {@link ScriptKey} - スクリプトキー
         */
        @NotNull
        ScriptKey getScriptKey();

        /**
         * スクリプトの座標を取得します。
         * @return {@link Location} - スクリプトの座標
         */
        @Nullable
        Location getLocation();

        /**
         * スクリプトの作者を設定します。
         * @param author - 作者の一覧
         */
        void setAuthor(@NotNull Set<UUID> author);

        /**
         * スクリプトの作者の一覧を取得します。
         * @return {@link Set}&lt;{@link UUID}&gt; - スクリプトの作者の一覧
         */
        @NotNull
        Set<UUID> getAuthor();

        /**
         * スクリプトを設定します。
         * @param script - スクリプトの一覧
         */
        void setScript(@NotNull List<String> script);

        /**
         * スクリプトの一覧を取得します。
         * @return {@link List}&lt;{@link String}&gt; - スクリプトの一覧
         */
        @NotNull
        List<String> getScript();

        /**
         * スクリプトの編集時刻を現在の時刻に設定します。
         */
        void setLastEdit();

        /**
         * スクリプトの編集時刻を取得します。
         * @return {@link String} - 時刻
         */
        @Nullable
        String getLastEdit();

        /**
         * ネームタグを設定します。
         * @param nameTag - ネームタグ
         */
        void setNameTag(@Nullable String nameTag);

        /**
         * ネームタグを取得します。
         * @return {@link String} - ネームタグ
         */
        String getNameTag();

        /**
         * ターゲットセレクターを設定します。
         * @param selector - ターゲットセレクター
         */
        void setSelector(@Nullable String selector);

        /**
         * ターゲットセレクターを取得します。
         * @return {@link String} - ターゲットセレクター
         */
        String getSelector();

        /**
         * スクリプトの実行可能な回数を設定します。
         * @param amount - 実行可能な回数
         */
        void setAmount(int amount);

        /**
         * スクリプトの実行可能な回数を増します。
         * @param amount - 増やす回数
         */
        void addAmount(int amount);

        /**
         * スクリプトの実行可能な回数を減します。
         * @param amount - 減らす回数
         */
        void subtractAmount(int amount);

        /**
         * スクリプトの実行可能な回数を取得します。
         * @return {@link Integer} - 実行可能な回数
         */
        int getAmount();

        /**
         * 全ての設定を削除します。
         */
        void remove();
    }
}