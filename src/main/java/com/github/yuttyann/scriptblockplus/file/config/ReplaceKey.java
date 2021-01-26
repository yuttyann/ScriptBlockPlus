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

import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * ScriptBlockPlus ReplaceKey クラス
 * @author yuttyann44581
 */
public class ReplaceKey implements ConfigKey<String> {

    private final ConfigKey<String> configKey;
    private final String[] replaceKeys;

    protected Object[] args = {};
    protected String result = null;

    public ReplaceKey(@NotNull ConfigKey<String> configKey, @NotNull String... replaceKeys) {
        this.configKey = configKey;
        this.replaceKeys = replaceKeys;
    }

    @NotNull
    protected final ConfigKey<String> getConfigKey() {
        return configKey;
    }

    @Override
    @NotNull
    public Optional<String> get() {
        return configKey.get();
    }

    @NotNull
    public final <T> T getArgment(final int index, @NotNull Class<T> classOf) {
        return classOf.cast(args[index]);
    }

    @NotNull
    public ReplaceKey replace(@NotNull Object... replaces) {
        if (replaces.length != replaceKeys.length) {
            throw new IllegalArgumentException("Size are not equal.");
        }
        var replaceKey = new ReplaceKey(configKey, replaceKeys);
        replaceKey.args = replaces;
        replaceKey.result = replaceKey.configKey.getValue();
        for (int i = 0; i < replaces.length; i++) {
            replaceKey.result = StringUtils.replace(replaceKey.result, replaceKey.replaceKeys[i], replaceKey.args[i]);
        }
        return replaceKey;
    }

    @Override
    public String toString() {
        return result == null ? configKey.toString() : result;
    }
}