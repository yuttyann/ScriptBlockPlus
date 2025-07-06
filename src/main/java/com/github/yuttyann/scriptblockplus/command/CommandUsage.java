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
package com.github.yuttyann.scriptblockplus.command;

import com.github.yuttyann.scriptblockplus.enums.Permission;
import com.github.yuttyann.scriptblockplus.utils.StreamUtils;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * ScriptBlockPlus CommandUsage クラス
 * @author yuttyann44581
 */
public class CommandUsage {

    private final List<String> NODES = new ArrayList<>();

    private String text;

    public CommandUsage(@Nullable String text) {
        this(text, (String) null);
    }

    public CommandUsage(@Nullable String text, @Nullable String... nodes) {
        setText(text);
        addNode(nodes);
    }

    public CommandUsage(@Nullable String text, @Nullable Permission... permissions) {
        setText(text);
        StreamUtils.forEach(permissions, p -> addNode(p.getNode()));
    }

    @NotNull
    public String getText() {
        return text;
    }

    @NotNull
    public List<String> getNodes() {
        return NODES;
    }

    public final boolean hasText() {
        return StringUtils.isNotEmpty(text);
    }

    public boolean hasPermission(@NotNull CommandSender sender) {
        if (NODES == null || NODES.size() == 0) {
            return true;
        }
        return NODES.stream().anyMatch(s -> Permission.has(sender, s));
    }

    @NotNull
    public CommandUsage setText(@Nullable String text) {
        this.text = text;
        return this;
    }

    @NotNull
    public CommandUsage addNode(@Nullable String... nodes) {
        var value = Optional.ofNullable(nodes);
        if (value.isPresent() && value.get().length > 0) {
            StreamUtils.forEach(value.get(), NODES::add);
        }
        return this;
    }
}