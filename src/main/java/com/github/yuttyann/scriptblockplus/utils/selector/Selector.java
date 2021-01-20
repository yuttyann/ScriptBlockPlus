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
package com.github.yuttyann.scriptblockplus.utils.selector;

import java.util.Locale;

import com.github.yuttyann.scriptblockplus.utils.StreamUtils;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * ScriptBlockPlus Selector クラス
 * @author yuttyann44581
 */
public final class Selector {

    private static final ArgmentValue[] EMPTY_ARGMENTS_ARRAY = new ArgmentValue[0];

    private final String argments, selector;

    public Selector(@NotNull String source) {
        if (source.startsWith("@") && source.indexOf("[") > 0) {
            int start = source.indexOf("[");
            this.selector = source.substring(0, start).trim();
            this.argments = source.substring(start + 1, source.length() - 1).trim();
        } else {
            this.selector = source;
            this.argments = null;
        }
    }

    @NotNull
    public String getSelector() {
        return selector.toLowerCase(Locale.ROOT);
    }

    @Nullable
    public ArgmentValue[] getArgments() {
        if (StringUtils.isEmpty(argments)) {
            return EMPTY_ARGMENTS_ARRAY;
        }
        return StreamUtils.toArray(StringUtils.split(argments, ','), ArgmentValue::new, ArgmentValue[]::new);
    }
}