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

    /**
     * コンストラクタ
     * @param configKey - コンフィグのキー
     * @param replaceKeys - 検索する文字列
     */
    public ReplaceKey(@NotNull ConfigKey<String> configKey, @NotNull String... replaceKeys) {
        this.configKey = configKey;
        this.replaceKeys = replaceKeys;
    }

    /**
     * コンフィグのキーを取得します。
     * @return {@link ConfigKey}&lt;{@link String}&gt; - コンフィグのキー
     */
    @NotNull
    protected final ConfigKey<String> getConfigKey() {
        return configKey;
    }

    /**
     * 値を取得します。
     * @return {@link Optional}&lt;{@link String}&gt; - 値
     */
    @Override
    @NotNull
    public Optional<String> get() {
        return configKey.get();
    }

    /**
     * 要素を取得します。
     * @param <T> - 要素の型
     * @param index - 位置
     * @param classOf - 要素のクラス
     * @return
     */
    @NotNull
    public final <T> T getArgument(final int index, @NotNull Class<T> classOf) {
        return classOf.cast(args[index]);
    }

    /**
     * 置換を行います。
     * @param replaces - 置換する要素
     * @return {@link ReplaceKey} - 置換後のキー
     */
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