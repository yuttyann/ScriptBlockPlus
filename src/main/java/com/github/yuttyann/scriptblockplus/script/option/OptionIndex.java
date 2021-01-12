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

/**
 * ScriptBlockPlus OptionIndex クラス
 * @author yuttyann44581
 */
public class OptionIndex {

    private final String syntax;
    private final IndexType indexType;

    /**
     * コンストラクタ
     * @param indexType - スクリプトの追加位置
     * @param optionClass - オプションのクラス
     */
    public OptionIndex(@NotNull IndexType indexType, @NotNull Class<? extends BaseOption> optionClass) {
        this.syntax = optionClass.getAnnotation(OptionTag.class).syntax();
        this.indexType = indexType;
    }

    /**
     * オプションの構文を取得します。
     * @return {@link String} - オプションの構文
     */
    @NotNull
    public String getSyntax() {
        return syntax;
    }

    /**
     * スクリプトの追加方法を取得します。
     * @return {@link IndexType} - スクリプトの追加方法
     */
    @NotNull
    public IndexType getIndexType() {
        return indexType;
    }
}