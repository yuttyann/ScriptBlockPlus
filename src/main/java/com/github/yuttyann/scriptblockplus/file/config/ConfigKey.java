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