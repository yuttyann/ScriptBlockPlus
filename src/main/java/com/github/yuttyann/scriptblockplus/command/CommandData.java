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
 * ScriptBlockPlus CommandData クラス
 * @author yuttyann44581
 */
public class CommandData {

    private String message;
    private boolean isPrefix;

    private List<String> permissions = new ArrayList<>();

    public CommandData(@Nullable String message, boolean isPrefix) {
        this(message, isPrefix, (String) null);
    }

    public CommandData(@Nullable String message, @Nullable String... permission) {
        this(message, true, permission);
    }

    public CommandData(@Nullable String message, boolean isPrefix, @Nullable String... permission) {
        setMessage(message);
        setPrefix(isPrefix);
        addPermission(permission);
    }

    @NotNull
    public CommandData setMessage(@Nullable String message) {
        this.message = message;
        return this;
    }

    @NotNull
    public CommandData setPrefix(boolean isPrefix) {
        this.isPrefix = isPrefix;
        return this;
    }

    @NotNull
    public CommandData addPermission(@Nullable String... permission) {
        var value = Optional.ofNullable(permission);
        if (value.isPresent() && value.get().length > 0) {
            StreamUtils.forEach(value.get(), permissions::add);
        }
        return this;
    }

    @NotNull
    public String getMessage() {
        return message;
    }

    @NotNull
    public List<String> getPermissions() {
        return permissions;
    }

    public boolean isPrefix() {
        return isPrefix;
    }

    public final boolean hasMessage() {
        return StringUtils.isNotEmpty(message);
    }

    public boolean hasPermission(@NotNull CommandSender sender) {
        if (permissions == null || permissions.size() == 0) {
            return true;
        }
        return permissions.stream().anyMatch(s -> Permission.has(sender, s));
    }
}