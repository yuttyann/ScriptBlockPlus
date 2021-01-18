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
package com.github.yuttyann.scriptblockplus.listener;

import java.util.HashSet;
import java.util.Set;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.enums.Filter;
import com.github.yuttyann.scriptblockplus.file.json.BlockScriptJson;
import com.github.yuttyann.scriptblockplus.script.ScriptKey;
import com.github.yuttyann.scriptblockplus.script.ScriptRead;
import com.github.yuttyann.scriptblockplus.utils.CommandSelector;
import com.github.yuttyann.scriptblockplus.utils.StreamUtils;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * ScriptBlockPlus BlockListener クラス
 * @author yuttyann44581
 */
public class BlockListener implements Listener {

    private static final int LENGTH = Filter.getPrefix().length();
    private static final Set<String> REDSTONE_FLAG = new HashSet<>();
    private static final FilterValue[] EMPTY_FILTER_ARRAY = new FilterValue[0];

    private class FilterSplit {

        private final String filters;
        private final String selector;

        private FilterSplit(@NotNull String source) {
            if (source.startsWith(Filter.getPrefix())) {
                int end = source.indexOf("}");
                this.filters = source.substring(LENGTH, end).trim();
                this.selector = source.substring(end + 1, source.length()).trim();
            } else {
                this.filters = null;
                this.selector = source;
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

    private class FilterValue {

        private final String value;
        private final Filter filter;

        private FilterValue(@NotNull String source) {
            for (var filter : Filter.values()) {
                if (source.startsWith(filter.getSyntax())) {
                    this.value = filter.getValue(source);
                    this.filter = filter;
                    return;
                }
            }
            this.value = null;
            this.filter = Filter.NONE;
        }

        @Nullable
        public String getValue() {
            return value;
        }

        @NotNull
        public Filter getFilter() {
            return filter;
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockPhysics(BlockPhysicsEvent event) {
        var location = event.getBlock().getLocation();
        var fullCoords = BlockCoords.getFullCoords(location);
        if (!event.getBlock().isBlockIndirectlyPowered()) {
            REDSTONE_FLAG.remove(fullCoords);
            return;
        }
        if (REDSTONE_FLAG.contains(fullCoords)) {
            return;
        }
        for (var scriptKey : ScriptKey.values()) {
            var scriptJson = new BlockScriptJson(scriptKey);
            if (!scriptJson.exists()) {
                continue;
            }
            var blockScript = scriptJson.load();
            if (!blockScript.has(location)) {
                continue;
            }
            var selector = blockScript.get(location).getSelector();
            if (StringUtils.isEmpty(selector) || !CommandSelector.has(selector)) {
                continue;
            }
            var index = new int[] { 0 };
            var filterSplit = new FilterSplit(selector);
            var filterValues = filterSplit.getFilterValues();
            for (var target : CommandSelector.getTargets(Bukkit.getConsoleSender(), location, filterSplit.getSelector())) {
                if (!(target instanceof Player)) {
                    continue;
                }
                var player = (Player) target;
                if (filterValues.length > 0 && !StreamUtils.allMatch(filterValues, t -> has(player, t, index[0]))) {
                    continue;
                }
                index[0]++;
                REDSTONE_FLAG.add(fullCoords);
                new ScriptRead(player, location, scriptKey).read(0);
            }
        }
    }

    public boolean has(@NotNull Player player, @NotNull FilterValue filterValue, int index) {
        var value = filterValue.getValue();
        if (StringUtils.isEmpty(value)) {
            return false;
        }
        switch (filterValue.getFilter()) {
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