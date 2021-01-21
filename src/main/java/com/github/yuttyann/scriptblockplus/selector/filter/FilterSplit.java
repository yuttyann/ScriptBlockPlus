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
import com.github.yuttyann.scriptblockplus.utils.StreamUtils;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * ScriptBlockPlus FilterSplit クラス
 * @author yuttyann44581
 */
public final class FilterSplit {

    private static final int LENGTH = Filter.getPrefix().length();
    private static final FilterValue[] EMPTY_FILTER_ARRAY = new FilterValue[0];
    
    private final String selector, filters;

    public FilterSplit(@NotNull String source) {
        if (source.startsWith(Filter.getPrefix()) && source.indexOf("}") != -1) {
            int end = source.indexOf("}");
            this.selector = source.substring(end + 1, source.length()).trim();
            this.filters = source.substring(LENGTH, end).trim();
        } else {
            this.selector = source;
            this.filters = null;
        }
    }

    @NotNull
    public String getSelector() {
        return selector;
    }

    @Nullable
    public FilterValue[] getFilterValues() {
        if (StringUtils.isEmpty(filters)) {
            return EMPTY_FILTER_ARRAY;
        }
        var array = StringUtils.split(filters, ',');
        return StreamUtils.toArray(array, FilterValue::new, FilterValue[]::new);
    }
}