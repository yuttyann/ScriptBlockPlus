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

import com.github.yuttyann.scriptblockplus.file.config.SBConfig;
import com.github.yuttyann.scriptblockplus.utils.StreamUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * ScriptBlockPlus CommandUsage クラス
 * @author yuttyann44581
 */
public abstract class CommandUsage {

    private final List<CommandData> usages = new ArrayList<CommandData>();

    public final void setUsage(@NotNull CommandData... args) {
        usages.clear();
        StreamUtils.forEach(args, usages::add);
    }

    public final void addUsage(@NotNull CommandData... args) {
        StreamUtils.forEach(args, usages::add);
    }

    protected final void sendUsage(@NotNull CommandSender sender, @NotNull Command command, @NotNull BaseCommand baseCommand) {
        if (usages.isEmpty()) {
            return;
        }
        var list = new ArrayList<CommandData>(usages.size());
        StreamUtils.fForEach(usages, c -> c.hasPermission(sender), list::add);
        if (list.isEmpty()) {
            SBConfig.NOT_PERMISSION.send(sender);
            return;
        }
        var name = command.getName();
        if (baseCommand.isAliases() && command.getAliases().size() > 0) {
            name = command.getAliases().get(0).toLowerCase(Locale.ROOT);
        }
        var prefix = "§b/" + name + " ";
        sender.sendMessage("§d========== " + command.getName().toUpperCase(Locale.ROOT) + " Commands ==========");
        StreamUtils.fForEach(list, CommandData::hasMessage, c -> sender.sendMessage(text(c, prefix)));
    }

    private String text(@NotNull CommandData commandData, @NotNull String prefix) {
        return commandData.isPrefix() ? prefix + commandData.getMessage() : commandData.getMessage();
    }
}