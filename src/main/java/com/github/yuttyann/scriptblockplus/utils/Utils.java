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
import com.github.yuttyann.scriptblockplus.file.SBFile;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.selector.CommandSelector;
import com.google.common.base.Splitter;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    private static final Map<String, Boolean> VERSION_CACHE = new HashMap<>();

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
            var version = Bukkit.getBukkitVersion();
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
        var result = VERSION_CACHE.get(version);
        if (result == null) {
            VERSION_CACHE.put(version, result = isUpperVersion(getServerVersion(), version));
        }
        return result;
    }

    public static boolean isUpperVersion(@NotNull String source, @NotNull String target) {
        if (isNotEmpty(source) && isNotEmpty(target)) {
            return getVersionInt(source) >= getVersionInt(target);
        }
        return false;
    }

    public static int getVersionInt(@NotNull String version) {
        int dot1 = version.indexOf('.', 0);
        int dot2 = version.indexOf('.', dot1 + 1);
        if (dot1 < 0) {
            throw new IllegalArgumentException("Invalid Version: " + version);
        }
        int part1 = Integer.parseInt(version, 0, dot1, 10);
        int part2 = Integer.parseInt(version, dot1 + 1, dot2 < 0 ? version.length() : dot2, 10);
        int result = (part1 * 100000) + (part2 * 1000);
        if (dot2 >= 0) {
            result += Integer.parseInt(version, dot2 + 1, version.length(), 10);
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
        for (var line : Splitter.on("|~").omitEmptyStrings().split(text)) {
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
        if (CommandSelector.has(command)) {
            var commands = CommandSelector.build(sender, location, command);
            return !commands.isEmpty() && StreamUtils.allMatch(commands, s -> Bukkit.dispatchCommand(sender, s));
        }
        return Bukkit.dispatchCommand(sender, command);
    }

    @NotNull
    public static World getWorld(@NotNull String name) {
        var world = Bukkit.getWorld(name);
        if (world == null) {
            var file = new SBFile(Bukkit.getWorldContainer(), name + "/level.dat");
            if (file.exists()) {
                world = Bukkit.createWorld(WorldCreator.name(name));
            } else {
                throw new NullPointerException(name + " does not exist");
            }
        }
        return world;
    }

    @Nullable
    public static String getName(@NotNull UUID uuid) {
        var player = Bukkit.getOfflinePlayer(uuid);
        return !player.hasPlayedBefore() ? "null" : player.getName();
    }
}