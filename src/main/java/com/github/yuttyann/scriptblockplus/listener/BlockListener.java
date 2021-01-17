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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.file.json.BlockScriptJson;
import com.github.yuttyann.scriptblockplus.hook.CommandSelector;
import com.github.yuttyann.scriptblockplus.script.ScriptKey;
import com.github.yuttyann.scriptblockplus.script.ScriptRead;
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

public class BlockListener implements Listener {

    private static final int LENGTH = "tag{".length();
    private static final Set<String> REDSTONE_FLAG = new HashSet<>();

    private enum Tag {
        OP("op="),
        PERM("perm="),
        NONE("");

        private final String prefix;

        private Tag(@NotNull String prefix) {
            this.prefix = prefix;
        }

        @NotNull
        public String getPrefix() {
            return prefix;
        }

        @NotNull
        public String substring(@NotNull String source) {
            return source.substring(prefix.length(), source.length());
        }
    }

    private class TagAndSelector {

        private final String tags;
        private final String selector;

        private TagAndSelector(@NotNull String source) {
            if (source.startsWith("tag{")) {
                int end = source.indexOf("}");
                this.tags = source.substring(LENGTH, end).trim();
                this.selector = source.substring(end + 1, source.length()).trim();
            } else {
                this.tags = null;
                this.selector = source;
            }
        }

        @Nullable
        public String getTags() {
            return tags;
        }

        @NotNull
        public String getSelector() {
            return selector;
        }
    }

    private class TagValue {

        private final Tag tag;
        private final String value;

        public TagValue(@NotNull String source) {
            for (var tag : Tag.values()) {
                if (source.startsWith(tag.getPrefix())) {
                    this.tag = tag;
                    this.value = tag.substring(source);
                    return;
                }
            }
            this.tag = Tag.NONE;
            this.value = "null";
        }

        @NotNull
        public Tag getTag() {
            return tag;
        }

        @Nullable
        public String getValue() {
            return value;
        }

        public boolean has(@NotNull Player player) {
            if (StringUtils.isEmpty(value)) {
                return false;
            }
            switch (tag) {
                case OP:
                    return Boolean.parseBoolean(value) ? player.isOp() : !player.isOp();
                case PERM:
                    return player.hasPermission(value);
                default:
                    return false;
            }
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
            if (StringUtils.isEmpty(selector) || !CommandSelector.INSTANCE.has(selector)) {
                continue;
            }
            var tagAndSelector = new TagAndSelector(selector);
            var tags = getTags(tagAndSelector.getTags());
            for (var target : CommandSelector.INSTANCE.getTargets(Bukkit.getConsoleSender(), location, tagAndSelector.getSelector())) {
                if (!(target instanceof Player)) {
                    continue;
                }
                var player = (Player) target;
                if (tags.size() > 0 && !StreamUtils.allMatch(tags, t -> t.has(player))) {
                    continue;
                }
                REDSTONE_FLAG.add(fullCoords);
                new ScriptRead(player, location, scriptKey).read(0);
            }
        }
    }

    @NotNull
    private List<TagValue> getTags(@Nullable String tags) {
        if (StringUtils.isEmpty(tags)) {
            return Collections.emptyList();
        }
        var list = new ArrayList<TagValue>(2);
        for (var tag : StringUtils.split(tags, ',')) {
            var tagValue = new TagValue(tag);
            if (tagValue.getTag() != Tag.NONE) {
                list.add(tagValue);
            }
        }
        return list;
    }
}