package com.github.yuttyann.scriptblockplus.file.config;

import org.jetbrains.annotations.NotNull;

public interface ConfigValue<T> {

    @NotNull
    T value(@NotNull T value);
}