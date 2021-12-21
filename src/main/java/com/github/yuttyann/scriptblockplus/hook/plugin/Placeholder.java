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
package com.github.yuttyann.scriptblockplus.hook.plugin;

import com.github.yuttyann.scriptblockplus.hook.HookPlugin;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import me.clip.placeholderapi.PlaceholderAPI;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * ScriptBlockPlus Placeholder クラス
 * @author yuttyann44581
 */
public final class Placeholder extends HookPlugin {

    public static final Placeholder INSTANCE = new Placeholder();

    private static final String[] ESCAPE_SEARCH = { "\\a", "\\b", "\\c", "\\h", "\\o", "\\p", "\\s", "\\l", "\\g", "\\i", "\\j" };
    private static final String[] ESCAPE_RESULT = { "&", " ", ",", "-", ":", "%", "/", "<", ">", "[", "]" };

    private Placeholder() { }

    @Override
    @NotNull
    public String getPluginName() {
        return "PlaceholderAPI";
    }

    /**
     * 指定された文字列を置換します。
     * @param player - プレイヤー
     * @param source - 文字列
     * @return {@link String} - 置換後の文字列
     */
    @NotNull
    public String setPlaceholder(@NotNull OfflinePlayer player, @NotNull String source) {
        return PlaceholderAPI.setPlaceholders(player, source);
    }

    /**
     * 指定された文字列を置換します。
     * @param player - プレイヤー
     * @param source - 文字列
     * @return {@link String} - 置換後の文字列
     */
    @NotNull
    public String replace(@NotNull Player player, @NotNull String source) {
        source = StringUtils.replace(source, "<world>", player.getWorld().getName());
        source = StringUtils.replace(source, "<player>", player.getName());
        return has() ? setPlaceholder(player, source) : source;
    }

    /**
     * 指定された文字列を置換します。
     * @param world - ワールド
     * @param source - 文字列
     * @return {@link String} - 置換後の文字列
     */
    @NotNull
    public String replace(@NotNull World world, @NotNull String source) {
        source = StringUtils.replace(source, "<world>", world.getName());
        source = StringUtils.replace(source, "<online>", Bukkit.getOnlinePlayers().size());
        source = StringUtils.replace(source, "<players>", world.getPlayers().size());
        return source;
    }

    /**
     * エスケープ文字を置換します。
     * <pre>
     * 下記は、エスケープと置換後の文字列の一覧です。
     * "\a" -> "&amp;"
     * "\b" -> " "
     * "\c" -> ","
     * "\h" -> "-"
     * "\o" -> ":"
     * "\p" -> "%"
     * "\s" -> "/"
     * "\l" -> "&lt;"
     * "\g" -> "&gt;"
     * "\i" -> "["
     * "\j" -> "]"
     * </pre>
     * @param source - 文字列
     * @return {@link String} - 置換後の文字列
     */
    @NotNull
    public String escape(@NotNull String source) {
        for (int i = 0; i < ESCAPE_SEARCH.length; i++) {
            source = StringUtils.replace(source, ESCAPE_SEARCH[i], ESCAPE_RESULT[i]);
        }
        return source;
    }
}