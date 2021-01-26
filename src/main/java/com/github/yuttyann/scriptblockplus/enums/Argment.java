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
package com.github.yuttyann.scriptblockplus.enums;

import com.github.yuttyann.scriptblockplus.utils.StringUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * ScriptBlockPlus Argment 列挙型
 * <p>
 * Minecraft "1.12.2" までのセレクターの引数です。
 * @author yuttyann44581
 */
public enum Argment {
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

    Argment(@NotNull String syntax) {
        this(syntax, null, null);
    }

    Argment(@NotNull String syntax, @Nullable String prefix, @Nullable String suffix) {
        this.syntax = syntax;
        this.prefix = prefix;
        this.suffix = suffix;
    }

    @NotNull
    public String getValue(@NotNull String source) {
        switch (this) {
            case SCORE:
            case SCORE_MIN:
                var objective = source.substring(prefix.length(), source.lastIndexOf(suffix));
                return StringUtils.removeStart(source, prefix + objective + suffix) + "*" + objective;
            default:
                return StringUtils.removeStart(source, syntax);
        }
    }

    public boolean has(@NotNull String source) {
        switch (this) {
            case SCORE:
                return source.startsWith(prefix) && source.lastIndexOf(SCORE_MIN.suffix) == -1;
            case SCORE_MIN:
                return source.startsWith(prefix) && source.lastIndexOf(suffix) > 0;
            default:
                return source.startsWith(syntax);
        }
    }  
}