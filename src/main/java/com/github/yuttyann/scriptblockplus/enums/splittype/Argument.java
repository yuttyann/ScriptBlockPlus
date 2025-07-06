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

import com.github.yuttyann.scriptblockplus.selector.split.SplitType;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * ScriptBlockPlus Argument 列挙型
 * <p>
 * Minecraft "1.12.2" までのセレクターの引数です。
 * @author yuttyann44581
 */
public enum Argument implements SplitType {
    X("x="),
    Y("y="),
    Z("z="),
    DX("dx="),
    DY("dy="),
    DZ("dz="),
    R("r="),
    RM("rm="),
    RX("rx="),
    RXM("rxm="),
    RY("ry="),
    RYM("rym="),
    C("c="),
    L("l="),
    LM("lm="),
    M("m="),
    TAG("tag="),
    TEAM("team="),
    TYPE("type="),
    NAME("name="),
    SCORE("score_<name>=", "score_", "="),
    SCORE_MIN("score_<name>_min=", "score_", "_min=");

    private final String syntax, prefix, suffix;

    Argument(@NotNull String syntax) {
        this(syntax, null, null);
    }

    Argument(@NotNull String syntax, @Nullable String prefix, @Nullable String suffix) {
        this.syntax = syntax;
        this.prefix = prefix;
        this.suffix = suffix;
    }

    @Override
    @NotNull
    public String getValue(@NotNull String argument) {
        switch (this) {
            case SCORE: case SCORE_MIN:
                var objective = argument.substring(prefix.length(), argument.lastIndexOf(suffix));
                return StringUtils.removeStart(argument, prefix + objective + suffix) + "*" + objective;
            default:
                return StringUtils.removeStart(argument, syntax);
        }
    }

    @Override
    public boolean match(@NotNull String argument) {
        switch (this) {
            case SCORE:
                return argument.startsWith(prefix) && argument.lastIndexOf(SCORE_MIN.suffix) == -1;
            case SCORE_MIN:
                return argument.startsWith(prefix) && argument.lastIndexOf(suffix) > 0;
            default:
                return argument.startsWith(syntax);
        }
    }
}