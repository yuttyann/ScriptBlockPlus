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
import com.github.yuttyann.scriptblockplus.file.config.SBConfig;
import com.github.yuttyann.scriptblockplus.utils.StreamUtils;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * ScriptBlockPlus BaseCommand コマンドクラス
 * @author yuttyann44581
 */
public abstract class BaseCommand implements CommandExecutor, TabCompleter {

    private final List<SubCommand> COMMANDS = new ArrayList<SubCommand>();

    private String[] args = ArrayUtils.EMPTY_STRING_ARRAY;

    private Plugin plugin;
    private boolean isIgnoreUsage;

    public BaseCommand(@NotNull Plugin plugin) {
        this.plugin = plugin;
    }

    public BaseCommand(@NotNull Plugin plugin, @NotNull SubCommand... commands) {
        this.plugin = plugin;
        register(commands);
    }

    public static <T extends BaseCommand> void register(@NotNull String command, @NotNull T baseCommand) {
        var plugin = (JavaPlugin) baseCommand.getPlugin();
        var pluginCommand = Optional.ofNullable(plugin.getCommand(command));
        if (pluginCommand.isPresent()) {
            pluginCommand.get().setExecutor(baseCommand);
            pluginCommand.get().setTabCompleter(baseCommand);
        }
    }

    public final void register(@NotNull SubCommand... commands) {
        Collections.addAll(COMMANDS, commands);
    }

    @NotNull
    public final Plugin getPlugin() {
        return plugin;
    }

    public abstract boolean isAliases();

    public final boolean isIgnoreUsage() {
        return isIgnoreUsage;
    }

    @Override
    public final boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof ProxiedCommandSender) {
            var proxiedCommandSender = ((ProxiedCommandSender) sender).getCallee();
            if (proxiedCommandSender instanceof Player) {
                sender = proxiedCommandSender;
            }
        }
        try {
            if (!execute(sender, command, label, args) && !isIgnoreUsage) {
                sendUsage(sender, command, this);
            }
        } finally {
            isIgnoreUsage = false;
        }
        return true;
    }

    private boolean execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length > 0) {
            for (var subCommand : COMMANDS) {
                if (StreamUtils.anyMatch(subCommand.getNames(), s -> compare(s, args[0]))) {
                    this.args = args;
                    return subCommand.runCommand(sender, command, label);
                }
            }
        }
        return false;
    }

    @Override
    @NotNull
    public final List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        this.args = args;
        var completeList = new ArrayList<String>();
        tabComplete(sender, command, label, completeList);
        return completeList;
    }

    protected abstract void tabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull List<String> list);

    protected final boolean hasPermission(@NotNull CommandSender sender, @NotNull Permission permission) {
        return hasPermission(sender, permission, true);
    }

    protected final boolean hasPermission(@NotNull CommandSender sender, @NotNull Permission permission, boolean isPlayer) {
        if (isPlayer && !isPlayer(sender)) {
            return false;
        }
        var has = permission.has(sender);
        if (!has) {
            isIgnoreUsage = true;
            SBConfig.NOT_PERMISSION.send(sender);
        }
        return has;
    }

    protected final boolean isPlayer(@NotNull CommandSender sender) {
        if (sender instanceof Player) {
            return true;
        }
        SBConfig.SENDER_NO_PLAYER.send(sender);
        isIgnoreUsage = true;
        return false;
    }

    @NotNull
    protected final int length() {
        return args.length;
    }

    @NotNull
    protected final String[] args() {
        return args;
    }

    @NotNull
    protected final String args(final int index) {
        return index < 0 || index >= args.length ? "" : args[index];
    }

    protected final boolean compare(@NotNull String source, @NotNull String text) {
        return text.equalsIgnoreCase(source);
    }

    protected final boolean compare(@NotNull String source, @NotNull String... texts) {
        return Stream.of(texts).anyMatch(s -> compare(source, s));
    }

    protected final boolean compare(final int index, @NotNull String text) {
        return index < 0 || index >= args.length ? false : compare(args[index], text);
    }

    protected final boolean compare(final int index, @NotNull String... texts) {
        return index < 0 || index >= args.length ? false : compare(args[index], texts);
    }

    protected final boolean range(final int end) {
        return range(end, end);
    }

    protected final boolean range(final int start, final int end) {
        return start <= args.length && end >= args.length;
    }

    protected final void sendUsage(@NotNull CommandSender sender, @NotNull Command command, @NotNull BaseCommand baseCommand) {
        if (COMMANDS.isEmpty()) {
            return;
        }
        var list = new ArrayList<CommandUsage>(COMMANDS.size());
        COMMANDS.forEach(s -> StreamUtils.fForEach(s.getUsages(), c -> c.hasPermission(sender), list::add));
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
        StreamUtils.fForEach(list, CommandUsage::hasText, c -> sender.sendMessage(prefix + c.getText()));
    }
}