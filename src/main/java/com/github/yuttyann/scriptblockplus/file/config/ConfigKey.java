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
package com.github.yuttyann.scriptblockplus.file.config;

import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * ScriptBlockPlus ConfigKey インターフェース
 * @param <T> 値の型
 * @author yuttyann44581
 */
public interface ConfigKey<T> {

    /**
     * 値を取得します。
     * @return {@link Optional}&lt;{@link T}&gt; - 値
     */
    @NotNull
    Optional<T> get();

    /**
     * 値を取得します。
     * @return {@link T} - 値
     */
    @NotNull
    default T getValue() {
        return get().orElseThrow(NullPointerException::new);
    }

    /**
     * カラーコードを置換した文字列を取得します。
     * @return {@link String} - 文字列
     */
    @NotNull
    default String setColor() {
        return StringUtils.setColor(toString());
    }

    /**
     * カラーコードを置換した文字列のリストを取得します。
     * @return {@link List}&lt;{@link String}&gt; - 文字列のリスト
     */
    @NotNull
    default List<String> setListColor() {
        T value = getValue();
        if (value instanceof List) {
            @SuppressWarnings("unchecked")
            var list = (List<String>) value;
            return StringUtils.setListColor(list);
        }
        return Collections.emptyList();
    }

    /**
     * 値が{@code null}ではない、又は{@code boolean}の値が{@code true}の場合に処理を実行します。
     * @param action - 処理
     */
    default void ifPresentAndTrue(@NotNull Consumer<T> action) {
        T value = get().orElse(null);
        if (value instanceof Boolean && !((Boolean) value)) {
            return;
        }
        if (value != null) {
            action.accept(value);
        }
    }

    /**
     * コンソールにメッセージを送信します。
     */
    default void send() {
        send(Bukkit.getConsoleSender());
    }

    /**
     * プレイヤーにメッセージを送信します。
     * @param sbPlayer - プレイヤー
     */
    default void send(@NotNull SBPlayer sbPlayer) {
        send(sbPlayer.getPlayer());
    }

    /**
     * 送信者にメッセージを送信します。
     * @param sender - 送信者
     */
    default void send(@NotNull CommandSender sender) {
        Utils.sendColorMessage(sender, toString());
    }

    /**
     * コンソールログが有効な場合に、コンソールにメッセージを送信します。
     */
    default void console() {
        SBConfig.CONSOLE_LOG.ifPresentAndTrue(s -> send());
    }
}