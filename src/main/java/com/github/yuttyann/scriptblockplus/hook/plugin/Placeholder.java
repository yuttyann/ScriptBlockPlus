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
import com.github.yuttyann.scriptblockplus.utils.Utils;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.PlaceholderAPIPlugin;

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

    private Placeholder() { }

    @Override
    @NotNull
    public String getPluginName() {
        return "PlaceholderAPI";
    }

    @NotNull
    @SuppressWarnings("deprecation")
    public String setPlaceholder(@NotNull Player player, @NotNull String source) {
        var version = PlaceholderAPIPlugin.getInstance().getDescription().getVersion();
        if (Utils.isUpperVersion("2.8.8", version)) {
            source = PlaceholderAPI.setPlaceholders((OfflinePlayer) player, source);
        } else {
            source = PlaceholderAPI.setPlaceholders(player, source);
        }
        return source;
    }

    @NotNull
    public String replace(@NotNull Player player, @NotNull String source) {
        source = StringUtils.replace(source, "<world>", player.getWorld().getName());
        source = StringUtils.replace(source, "<player>", player.getName());
        return has() ? setPlaceholder(player, source) : source;
    }

    @NotNull
    public String replace(@NotNull World world, @NotNull String source) {
        source = StringUtils.replace(source, "<world>", world.getName());
        source = StringUtils.replace(source, "<online>", Bukkit.getOnlinePlayers().size());
        source = StringUtils.replace(source, "<players>", world.getPlayers().size());
        return source;
    }
}