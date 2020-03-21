package com.github.yuttyann.scriptblockplus.file.config;

import com.github.yuttyann.scriptblockplus.file.SBConfig;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public interface ConfigKey<T> {

    default void send() {
        send(Bukkit.getConsoleSender());
    }

    default void send(@NotNull CommandSender sender) {
        Utils.sendColorMessage(sender, toString());
    }

    default void console() {
        SBConfig.CONSOLE_LOG.action(s -> send());
    }

    default void action(@NotNull Consumer<T> action) {
        action.accept(get());
    }

    @NotNull
    T get();
}