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

import static com.github.yuttyann.scriptblockplus.utils.StringUtils.*;
import static com.github.yuttyann.scriptblockplus.utils.version.McVersion.*;

import java.text.SimpleDateFormat;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.enums.CommandLog;
import com.github.yuttyann.scriptblockplus.enums.Permission;
import com.github.yuttyann.scriptblockplus.file.SBFile;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.selector.CommandSelector;
import com.github.yuttyann.scriptblockplus.utils.server.CraftBukkit;
import com.github.yuttyann.scriptblockplus.utils.version.McVersion;
import com.github.yuttyann.scriptblockplus.utils.version.Version;
import com.google.common.base.Splitter;

/**
 * ScriptBlockPlus Utils クラス
 * @author yuttyann44581
 */
public final class Utils {

    public static final String MINECRAFT = "minecraft:";
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    private static final Splitter SPLITTER = Splitter.on("|~").omitEmptyStrings();

    /**
     * ランダムなUUIDの文字列を取得します。
     * @return {@link String} - UUIDの文字列
     */
    @NotNull
    public static String randomUUID() {
        return UUID.randomUUID().toString();
    }

    /**
     * パッケージのバージョン(例: {@code v1_16_R3})を取得します。
     * @return {@link String} - パッケージのバージョン
     * @see CraftBukkit#getLegacyPackageVersion()
     */
    @Deprecated
    @NotNull
    public static String getPackageVersion() {
        var name = Bukkit.getServer().getClass().getPackage().getName();
        return name.substring(name.lastIndexOf('.') + 1);
    }

    /**
     * サーバーバージョンを比較します。
     * @param version - 比較元
     * @return @code boolean} - 比較元がサーバーのバージョン以上だった場合は{@code true}
     * @see McVersion#isSupported()
     */
    @Deprecated
    public static boolean isCBXXXorLater(@NotNull String version) {
        return GAME_VERSION.isUpperVersion(Version.of(version));
    }

    /**
     * バージョンを比較します。
     * <p>
     * {@code isUpperVersion("1.8", "1.8")} -> {@code true}
     * <p>
     * {@code isUpperVersion("1.8", "1.8.1")} -> {@code true}
     * <p>
     * {@code isUpperVersion("1.8.1", "1.8")} -> {@code false}
     * @param source - 比較元
     * @param target - 比較先
     * @return {@code boolean} - 比較元が比較先のバージョン以上だった場合は{@code true}
     * @see Version#isUpperVersion(String, String)
     */
    @Deprecated
    public static boolean isUpperVersion(@NotNull String source, @NotNull String target) {
        return Version.isUpperVersion(source, target);
    }

    /**
     * 送信対象にメッセージを送信します。
     * <p>
     * {@code \n}または{@code |~}で改行を行うことができます。
     * @param sender - 送信対象
     * @param message - メッセージ
     */
    public static void sendColorMessage(@NotNull CommandSender sender, @Nullable String message) {
        var color = "";
        for (var line : SPLITTER.split(replace(setColor(message), "\\n", "|~"))) {
            sender.sendMessage(line = (color + line));
            if (line.indexOf('§') > -1) {
                color = getColors(line);
            }
        }
    }

    /**
     * 権限を一時的に与えて、処理を実行します。
     * @param <T> 戻り値の型
     * @param sbPlayer - プレイヤー
     * @param permission - 権限
     * @param supplier - 処理
     * @return {@link T} - 任意の戻り値
     */
    public static <T> T tempPerm(@NotNull SBPlayer sbPlayer, @NotNull Permission permission, @NotNull Supplier<T> supplier) {
        return CommandLog.supplier(sbPlayer.getWorld(), () -> {
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

    /**
     * コマンドを実行します。
     * <p>
     * ターゲットセレクターが含まれている場合は、それを考慮した上で実行されます。
     * @param sender - 送信者
     * @param location - 座標
     * @param command - コマンド
     * @return {@code boolean} - コマンドの実行に成功した場合は{@code true}
     */
    public static boolean dispatchCommand(@NotNull CommandSender sender, @NotNull Location location, @NotNull String command) {
        if (CommandSelector.has(command = command.startsWith("/") ? command.substring(1) : command)) {
            var commands = CommandSelector.build(sender, location, command);
            return !commands.isEmpty() && StreamUtils.allMatch(commands, s -> Bukkit.dispatchCommand(sender, s));
        }
        return Bukkit.dispatchCommand(sender, command);
    }

    /**
     * ワールドを取得します。
     * @param name - ワールドの名前
     * @return {@link World} - ワールド
     */
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

    /**
     * プレイヤーの名前を取得します。
     * @param uuid - プレイヤーの{@link UUID}
     * @return {@link String} - プレイヤーの名前
     */
    @NotNull
    public static String getName(@NotNull UUID uuid) {
        var player = Bukkit.getOfflinePlayer(uuid);
        return !player.hasPlayedBefore() ? "null" : player.getName();
    }

    @NotNull
    public static Class<?> getClassForName(@NotNull String className) throws IllegalArgumentException {
        try {
            return Class.forName(Objects.requireNonNull(className));
        } catch (ClassNotFoundException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    public static boolean isSupported(@NotNull Class<?> source, @NotNull Class<?> target) {
        return source.isAssignableFrom(target) || source.equals(target);
    }
}