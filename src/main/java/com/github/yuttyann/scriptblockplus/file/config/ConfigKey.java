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

import com.github.yuttyann.scriptblockplus.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * ScriptBlockPlus ConfigKey インターフェース
 * @param <T> 値の型
 * @author yuttyann44581
 */
public interface ConfigKey<T> {

    @NotNull
    Optional<T> get();

    @NotNull
    default T getValue() {
        return get().orElseThrow(NullPointerException::new);
    }

    default void ifPresentAndTrue(@NotNull Consumer<T> action) {
        T value = get().orElse(null);
        if (value instanceof Boolean && !((Boolean) value)) {
            return;
        }
        if (value != null) {
            action.accept(value);
        }
    }

    default void send() {
        send(Bukkit.getConsoleSender());
    }

    default void send(@NotNull CommandSender sender) {
        Utils.sendColorMessage(sender, toString());
    }

    default void console() {
        SBConfig.CONSOLE_LOG.ifPresentAndTrue(s -> send());
    }
}