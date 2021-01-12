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

import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * ScriptBlockPlus BaseCommand コマンドクラス
 * @author yuttyann44581
 */
public abstract class BaseCommand extends CommandUsage implements CommandExecutor, TabCompleter {

    private Plugin plugin;
    private boolean isIgnoreUsage;

    public BaseCommand(@NotNull Plugin plugin) {
        this.plugin = plugin;
        setUsage(getUsages());
    }

    public static <T extends BaseCommand> void register(@NotNull String command, @NotNull T baseCommand) {
        var plugin = (JavaPlugin) baseCommand.getPlugin();
        var pluginCommand = Optional.ofNullable(plugin.getCommand(command));
        if (pluginCommand.isPresent()) {
            pluginCommand.get().setExecutor(baseCommand);
            pluginCommand.get().setTabCompleter(baseCommand);
        }
    }

    @NotNull
    public final Plugin getPlugin() {
        return plugin;
    }

    @NotNull
    public abstract CommandData[] getUsages();

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
            if (!runCommand(sender, command, label, args) && !isIgnoreUsage) {
                sendUsage(sender, command, this);
            }
        } finally {
            isIgnoreUsage = false;
        }
        return true;
    }

    @Override
    @NotNull
    public final List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        var completeList = new ArrayList<String>();
        tabComplete(sender, command, label, args, completeList);
        return completeList;
    }

    protected abstract boolean runCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args);

    protected abstract void tabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args, @NotNull List<String> empty);

    protected final boolean hasPermission(@NotNull CommandSender sender, @NotNull Permission permission) {
        return hasPermission(sender, permission, true);
    }

    protected final boolean hasPermission(@NotNull CommandSender sender, @NotNull Permission permission, boolean isPlayer) {
        if (isPlayer && !isPlayer(sender)) {
            return false;
        }
        boolean has = permission.has(sender);
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

    protected final boolean equals(@NotNull String source, @NotNull String another) {
        return another.equalsIgnoreCase(source);
    }

    protected final boolean equals(@NotNull String source, @NotNull String... anothers) {
        return Stream.of(anothers).anyMatch(s -> equals(source, s));
    }
}