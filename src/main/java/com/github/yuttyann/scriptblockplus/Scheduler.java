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
package com.github.yuttyann.scriptblockplus;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

/**
 * ScriptBlockPlus Scheduler クラス
 * @author yuttyann44581
 */
public final class Scheduler {

    private Plugin plugin;

    public Scheduler(@NotNull Plugin plugin) {
        this.plugin = plugin;
    }

    @NotNull
    public Plugin getPlugin() {
        return plugin;
    }

    @NotNull
    public BukkitTask run(@NotNull Runnable task) {
        return Bukkit.getScheduler().runTask(plugin, task);
    }

    @NotNull
    public BukkitTask run(@NotNull Runnable task, final long delay) {
        return Bukkit.getScheduler().runTaskLater(plugin, task, delay);
    }

    @NotNull
    public BukkitTask run(@NotNull Runnable task, final long delay, final long period) {
        return Bukkit.getScheduler().runTaskTimer(plugin, task, delay, period);
    }

    @NotNull
    public BukkitTask asyncRun(@NotNull Runnable task) {
        return Bukkit.getScheduler().runTaskAsynchronously(plugin, task);
    }

    @NotNull
    public BukkitTask asyncRun(@NotNull Runnable task, final long delay) {
        return Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, task, delay);
    }

    @NotNull
    public BukkitTask asyncRun(@NotNull Runnable task, final long delay, final long period) {
        return Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, task, delay, period);
    }    
}