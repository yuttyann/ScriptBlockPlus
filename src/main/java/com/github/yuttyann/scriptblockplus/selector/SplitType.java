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
package com.github.yuttyann.scriptblockplus.selector;

import org.jetbrains.annotations.NotNull;

/**
 * ScriptBlockPlus SplitType クラス
 * @author yuttyann44581
 */
public interface SplitType {

    /**
     * 値を取得します。
     * @param argment - 引数
     * @return {@link String} - 値
     */
    @NotNull
    public String getValue(@NotNull String argment);

    /**
     * 引数が一致するのかどうか。
     * @param argment - 引数
     * @return {@link boolean} - 一致する場合は{@code true}
     */
    public boolean match(@NotNull String argment);
}