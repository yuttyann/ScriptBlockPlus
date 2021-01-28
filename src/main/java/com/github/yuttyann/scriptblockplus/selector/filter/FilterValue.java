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
package com.github.yuttyann.scriptblockplus.selector.filter;

import com.github.yuttyann.scriptblockplus.enums.Filter;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * ScriptBlockPlus FilterValue クラス
 * @author yuttyann44581
 */
public final class FilterValue {

    private final Filter filter;
    private final String value;

    public FilterValue(@NotNull String source) {
        for (var filter : Filter.values()) {
            if (source.startsWith(filter.getSyntax())) {
                this.filter = filter;
                this.value = filter.getValue(source);
                return;
            }
        }
        throw new NullPointerException("Filter[" + source + "] not found");
    }

    @NotNull
    public Filter getFilter() {
        return filter;
    }

    @NotNull
    public String getValue() {
        return value;
    }

    public boolean has(@NotNull Player player, int index) {
        if (StringUtils.isEmpty(value)) {
            return false;
        }
        switch (filter) {
            case OP:
                return Boolean.parseBoolean(value) ? player.isOp() : !player.isOp();
            case PERM:
                return player.hasPermission(value);
            case LIMIT:
                return index < Integer.parseInt(value);
            default:
                return false;
        }
    }
}