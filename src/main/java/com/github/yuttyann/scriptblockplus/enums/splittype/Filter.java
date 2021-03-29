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
package com.github.yuttyann.scriptblockplus.enums.splittype;

import com.github.yuttyann.scriptblockplus.selector.SplitType;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;

import org.jetbrains.annotations.NotNull;

/**
 * ScriptBlockPlus Filter 列挙型
 * @author yuttyann44581
 */
public enum Filter implements SplitType {
    OP("op="),
    PERM("perm="),
    LIMIT("limit=");

    private final String syntax;

    Filter(@NotNull String syntax) {
        this.syntax = syntax;
    }

    @NotNull
    public String getSyntax() {
        return syntax;
    }

    @NotNull
    public static String getPrefix() {
        return "filter{";
    }

    @Override
    @NotNull
    public String getValue(@NotNull String argment) {
        return StringUtils.removeStart(argment, syntax);
    }

    @Override
    public boolean match(@NotNull String argment) {
        return argment.startsWith(syntax);
    }
}