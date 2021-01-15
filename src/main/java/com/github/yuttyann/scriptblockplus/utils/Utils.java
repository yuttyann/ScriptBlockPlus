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
package com.github.yuttyann.scriptblockplus.utils;

import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.enums.CommandLog;
import com.github.yuttyann.scriptblockplus.enums.Permission;
import com.github.yuttyann.scriptblockplus.hook.CommandSelector;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.google.common.base.Splitter;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Supplier;

import static com.github.yuttyann.scriptblockplus.utils.StringUtils.*;

/**
 * ScriptBlockPlus Utils クラス
 * @author yuttyann44581
 */
public final class Utils {

    public static final String MINECRAFT = "minecraft:";
    public static final String DATE_PATTERN = "yyyy/MM/dd HH:mm:ss";

    private static final String SERVER_VERSION = getServerVersion();
    private static final Map<String, Boolean> VC_CACHE = new HashMap<>();

    @NotNull
    public static String randomUUID() {
        return UUID.randomUUID().toString();
    }

    @NotNull
    public static String getPluginName(@NotNull Plugin plugin) {
        return plugin.getName() + " v" + plugin.getDescription().getVersion();
    }

    @NotNull
    public static String getServerVersion() {
        if (SERVER_VERSION == null) {
            String version = Bukkit.getBukkitVersion();
            return version.substring(0, version.indexOf("-"));
        }
        return SERVER_VERSION;
    }

    @NotNull
    public static String getPackageVersion() {
        var name = Bukkit.getServer().getClass().getPackage().getName();
        return name.substring(name.lastIndexOf('.') + 1);
    }

    public static boolean isCBXXXorLater(@NotNull String version) {
        Boolean result = VC_CACHE.get(version);
        if (result == null) {
            VC_CACHE.put(version, result = isUpperVersion(getServerVersion(), version));
        }
        return result;
    }

    public static boolean isUpperVersion(@NotNull String source, @NotNull String target) {
        if (isNotEmpty(source) && isNotEmpty(target)) {
            return getVersionInt(source) >= getVersionInt(target);
        }
        return false;
    }

    public static int getVersionInt(@NotNull String source) {
        var version = split(source, '.');
        if (version.length < 1) {
            return -1;
        }
        int result = (Integer.parseInt(version[0]) * 100000) + (Integer.parseInt(version[1]) * 1000);
        if (version.length == 3) {
            result += Integer.parseInt(version[2]);
        }
        return result;
    }

    @NotNull
    public static String getFormatTime(@NotNull String pattern) {
        return new SimpleDateFormat(pattern).format(new Date());
    }

    public static void sendColorMessage(@NotNull CommandSender sender, @Nullable String message) {
        var color = "";
        var text = replace(setColor(isEmpty(message) ? "" : message), "\\n", "|~");
        for (String line : Splitter.on("|~").omitEmptyStrings().split(text)) {
            sender.sendMessage(line = (color + line));
            if (line.indexOf('§') > -1) {
                color = getColors(line);
            }
        }
    }

    public static boolean tempPerm(@NotNull SBPlayer sbPlayer, @NotNull Permission permission, @NotNull Supplier<Boolean> supplier) {
        return sbPlayer.isOnline() && CommandLog.supplier(sbPlayer.getWorld(), () -> {
            if (sbPlayer.hasPermission(permission.getNode())) {
                return supplier.get();
            } else {
                var attachment = sbPlayer.addAttachment(ScriptBlock.getInstance());
                try {
                    attachment.setPermission(permission.getNode(), true);
                    return supplier.get();
                } finally {
                    attachment.unsetPermission(permission.getNode());
                }
            }
        });
    }

    public static boolean dispatchCommand(@NotNull CommandSender sender, @NotNull String command) {
        return dispatchCommand(sender, null, command);
    }

    public static boolean dispatchCommand(@NotNull CommandSender sender, @Nullable Location location, @NotNull String command) {
        command = command.startsWith("/") ? command.substring(1) : command;
        var commandSender = sender instanceof SBPlayer ? ((SBPlayer) sender).getPlayer() : sender;
        if (CommandSelector.INSTANCE.has(command)) {
            var commands = CommandSelector.INSTANCE.build(commandSender, location, command);
            return commands.stream().allMatch(s -> Bukkit.dispatchCommand(commandSender, s));
        }
        return Bukkit.dispatchCommand(commandSender, command);
    }

    @NotNull
    public static World getWorld(@NotNull String name) {
        var world = Bukkit.getWorld(name);
        if (world == null) {
            var file = new File(Bukkit.getWorldContainer(), name + "/level.dat");
            if (file.exists()) {
                world = Bukkit.createWorld(WorldCreator.name(name));
            }
        }
        return Objects.requireNonNull(world, name + "does not exist");
    }

    @SuppressWarnings("deprecation")
    public static void updateInventory(@NotNull Player player) {
        player.updateInventory();
    }

    @Nullable
    public static String getName(@NotNull UUID uuid) {
        try {
            var player = Bukkit.getOfflinePlayer(uuid);
            return !player.hasPlayedBefore() ? NameFetcher.getName(uuid) : player.getName();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}