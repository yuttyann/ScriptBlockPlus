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

import com.github.yuttyann.scriptblockplus.enums.IndexType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * ScriptBlockPlus OptionIndex クラス
 * @author yuttyann44581
 */
public class OptionIndex {

    private final IndexType indexType;
    private final OptionTag optionTag;

    /**
     * コンストラクタ
     * @param indexType - スクリプトの追加位置
     * @param optionTag - オプションタグ
     */
    private OptionIndex(@NotNull IndexType indexType, @Nullable OptionTag optionTag) {
        this.indexType = indexType;
        this.optionTag = optionTag;
    }

    /**
     * オプションを先頭に追加します。
     * @return {@link OptionIndex} - オプションインデックス
     */
    @NotNull
    public static OptionIndex top() {
        return new OptionIndex(IndexType.TOP, null);
    }

    /**
     * オプションを最後尾に追加します。
     * @return {@link OptionIndex} - オプションインデックス
     */
    @NotNull
    public static OptionIndex last() {
        return new OptionIndex(IndexType.LAST, null);
    }

    /**
     * 指定したオプションより一つ前に追加します。
     * @param optionClass - オプションのクラス
     * @return {@link OptionIndex} - オプションインデックス
     */
    @NotNull
    public static OptionIndex before(@NotNull Class<? extends BaseOption> optionClass) {
        return new OptionIndex(IndexType.BEFORE, optionClass.getAnnotation(OptionTag.class));
    }

    /**
     * 指定したオプションより一つ後に追加。
     * @param optionClass - オプションのクラス
     * @return {@link OptionIndex} - オプションインデックス
     */
    @NotNull
    public static OptionIndex after(@NotNull Class<? extends BaseOption> optionClass) {
        return new OptionIndex(IndexType.AFTER, optionClass.getAnnotation(OptionTag.class));
    }

    /**
     * オプションタグを取得します。
     * @return {@link OptionTag} - オプションタグ
     */
    @Nullable
    public OptionTag getOptionTag() {
        return optionTag;
    }

    /**
     * オプションの追加位置を取得します。
     * @return {@link IndexType} - オプションの追加位置
     */
    @NotNull
    public IndexType getIndexType() {
        return indexType;
    }
}