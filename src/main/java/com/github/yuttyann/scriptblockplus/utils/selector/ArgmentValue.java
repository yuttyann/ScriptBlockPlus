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

import com.github.yuttyann.scriptblockplus.enums.Argment;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * ScriptBlockPlus ArgmentValue クラス
 * @author yuttyann44581
 */
public final class ArgmentValue {

    private final String value;
    private final Argment argment;

    private String cacheValue;
    private Boolean cacheInverted;

    public ArgmentValue(@NotNull String source) {
        for (var tag : Argment.values()) {
            if (tag.has(source)) {
                this.value = tag.getValue(source);
                this.argment = tag;
                return;
            }
        }
        this.value = null;
        this.argment = null;
    }

    @NotNull
    public Argment getArgment() {
        return argment;
    }

    @Nullable
    public String getValue() {
        if (cacheValue == null && StringUtils.isNotEmpty(value)) {
            cacheValue = isInverted() ? value.substring(1) : value;
        }
        return cacheValue;
    }

    public boolean isInverted() {
        if (cacheInverted == null && StringUtils.isNotEmpty(value)) {
            cacheInverted = value.indexOf("!") == 0;
        }
        return cacheInverted;
    }
}