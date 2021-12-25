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

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.command.subcommand.BackupCommand;
import com.github.yuttyann.scriptblockplus.command.subcommand.CheckverCommand;
import com.github.yuttyann.scriptblockplus.command.subcommand.DatamigrCommand;
import com.github.yuttyann.scriptblockplus.command.subcommand.ReloadCommand;
import com.github.yuttyann.scriptblockplus.command.subcommand.ScriptCommand;
import com.github.yuttyann.scriptblockplus.command.subcommand.SelectorCommand;
import com.github.yuttyann.scriptblockplus.command.subcommand.ToolCommand;
import com.github.yuttyann.scriptblockplus.enums.Permission;
import com.github.yuttyann.scriptblockplus.enums.splittype.Filter;
import com.github.yuttyann.scriptblockplus.enums.splittype.Repeat;
import com.github.yuttyann.scriptblockplus.file.config.SBConfig;
import com.github.yuttyann.scriptblockplus.file.json.derived.BlockScriptJson;
import com.github.yuttyann.scriptblockplus.manager.OptionManager;
import com.github.yuttyann.scriptblockplus.raytrace.RayTrace;
import com.github.yuttyann.scriptblockplus.script.ScriptKey;
import com.github.yuttyann.scriptblockplus.script.option.OptionTag;
import com.github.yuttyann.scriptblockplus.utils.*;
import com.google.common.collect.Lists;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;

/**
 * ScriptBlockPlus ScriptBlockPlusCommand コマンドクラス
 * @author yuttyann44581
 */
public final class ScriptBlockPlusCommand extends BaseCommand {

    public ScriptBlockPlusCommand(@NotNull ScriptBlock plugin) {
        super(plugin);
    
        // SubCommands
        register(
            new ToolCommand(this),
            new ReloadCommand(this),
            new BackupCommand(this),
            new CheckverCommand(this),
            new DatamigrCommand(this),
            new ScriptCommand(this),
            new SelectorCommand(this)
        );
    }

    @Override
    public boolean isAliases() {
        return true;
    }

    @Override
    public void tabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull List<String> list) {
        if (range(1)) {
            var prefix = args(0).toLowerCase(Locale.ROOT);
            var commands = setCommandPermissions(sender, new ArrayList<>());
            StreamUtils.fForEach(commands, s -> StringUtils.isNotEmpty(s) && s.startsWith(prefix), list::add);
        } else if (compare(0, "selector") && Permission.COMMAND_SELECTOR.has(sender)) {
            if (compare(1, "paste") && range(3, 4)) {
                var prefix = args(range(3) ? 2 : 3).toLowerCase(Locale.ROOT);
                StreamUtils.fForEach(new String[] { "true", "false" }, s -> s.startsWith(prefix), list::add);
            } else if (range(2)) {
                var prefix = args(1).toLowerCase(Locale.ROOT);
                StreamUtils.fForEach(new String[] { "paste", "remove" }, s -> s.startsWith(prefix), list::add);
            }
        } else if (compare(0, ScriptKey.types()) && Permission.has(sender, ScriptKey.valueOf(args(0)), true)) {
            if (compare(1, "run")) {
                if (range(3)) {
                    var prefix = args(2).toLowerCase(Locale.ROOT);
                    var answers = new ArrayList<String>(4);
                    Bukkit.getWorlds().forEach(w -> answers.add(w.getName()));
                    Bukkit.getOnlinePlayers().forEach(p -> answers.add(p.getName()));
                    StreamUtils.fForEach(answers, s -> s.startsWith(prefix), list::add);
                } else if (range(4, 5)) {
                    int i = range(4) ? 0 : 1;
                    var prefix = args(3 + i).toLowerCase(Locale.ROOT);
                    var answers = new ArrayList<String>(4);
                    if (sender instanceof Player) {
                        var player = (Player) sender;
                        var rayTrace = RayTrace.rayTraceBlocks(player, player.getGameMode() == GameMode.CREATIVE ? 5.0D : 4.5D);
                        if (rayTrace != null) {
                            var hitBlock = rayTrace.getHitBlock();
                            if (BlockScriptJson.get(ScriptKey.valueOf(args(0))).has(BlockCoords.of(hitBlock))) {
                                answers.add(hitBlock.getX() + " " + hitBlock.getY() + " " + hitBlock.getZ());
                            }
                        }
                    }
                    if (i == 0) {
                        Bukkit.getWorlds().forEach(w -> answers.add(w.getName()));
                    }
                    StreamUtils.fForEach(answers, s -> s.startsWith(prefix), list::add);
                }
            } else if (compare(1, "create", "add")) {
                var prefix = args(length() - 1).toLowerCase(Locale.ROOT);
                var answers = OptionManager.getTags();
                var optionHelp = SBConfig.OPTION_HELP.getValue();
                var description = (Function<OptionTag, String>) o -> optionHelp ? o.description() : "";
                StreamUtils.fForEach(answers, s -> s.syntax().startsWith(prefix), o -> list.add(o.syntax() + description.apply(o)));
            } else if (compare(1, "redstone")) {
                if (range(3, 5)) {
                    var prefix = args(length() - 1).toLowerCase(Locale.ROOT);
                    var answers = Lists.newArrayList("@a", "@e", "@p", "@r");
                    StreamUtils.forEach(Repeat.values(), f -> answers.add(Repeat.getPrefix() + f.getSyntax() + "}"));
                    StreamUtils.forEach(Filter.values(), f -> answers.add(Filter.getPrefix() + f.getSyntax() + "}"));
                    StreamUtils.fForEach(answers, s -> s.startsWith(prefix), list::add);
                }
            } else if (range(2)) {
                var prefix = args(1).toLowerCase(Locale.ROOT);
                StreamUtils.fForEach(new String[] { "create", "add", "remove", "redstone", "nametag", "view", "run" }, s -> s.startsWith(prefix), list::add);
            }
        }
    }

    @NotNull
    private List<String> setCommandPermissions(@NotNull CommandSender sender, @NotNull List<String> list) {
        StreamUtils.ifAction(Permission.COMMAND_TOOL.has(sender), () -> list.add("tool"));
        StreamUtils.ifAction(Permission.COMMAND_RELOAD.has(sender), () -> list.add("reload"));
        StreamUtils.ifAction(Permission.COMMAND_BACKUP.has(sender), () -> list.add("backup"));
        StreamUtils.ifAction(Permission.COMMAND_CHECKVER.has(sender), () -> list.add("checkver"));
        StreamUtils.ifAction(Permission.COMMAND_DATAMIGR.has(sender), () -> list.add("datamigr"));
        StreamUtils.ifAction(Permission.COMMAND_SELECTOR.has(sender), () -> list.add("selector"));
        StreamUtils.fForEach(ScriptKey.values(), s -> Permission.has(sender, s, true), s -> list.add(s.getName()));
        return list;
    }
}