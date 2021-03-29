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
package com.github.yuttyann.scriptblockplus.bridge.plugin;

import net.milkbowl.vault.permission.Permission;

import com.github.yuttyann.scriptblockplus.bridge.HookPlugin;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * ScriptBlockPlus VaultPermission クラス
 * @author yuttyann44581
 */
public final class VaultPermission extends HookPlugin {

    public static final VaultPermission INSTANCE = new VaultPermission();

    private String name;
    private Permission permission;

    private VaultPermission() { }

    @Override
    @NotNull
    public String getPluginName() {
        return "Vault";
    }

    @NotNull
    public VaultPermission setupPermission() {
        var provider = Bukkit.getServicesManager().getRegistration(Permission.class);
        if (provider != null) {
            var vault = setVaultPermission(provider.getProvider());
            if (vault.isEnabled()) {
                return vault;
            }
        }
        return setVaultPermission(null);
    }

    private VaultPermission setVaultPermission(@Nullable Permission permission) {
        this.name = permission == null ? "None" : permission.getName();
        this.permission = permission;
        return this;
    }

    public boolean isEnabled() {
        return permission != null && permission.isEnabled();
    }

    @NotNull
    public String getName() {
        return name;
    }

    @Nullable
    public String getPrimaryGroup(@NotNull OfflinePlayer player) {
        return permission.getPrimaryGroup(null, player);
    }

    @Nullable
    public String getPrimaryGroup(@Nullable String world, @NotNull OfflinePlayer player) {
        return permission.getPrimaryGroup(world, player);
    }

    public boolean playerInGroup(@Nullable OfflinePlayer player, @NotNull String group) {
        return permission.playerInGroup(null, player, group);
    }

    public boolean playerInGroup(@Nullable String world, @NotNull OfflinePlayer player, @NotNull String group) {
        return permission.playerInGroup(world, player, group);
    }

    public boolean playerAddGroup(@NotNull OfflinePlayer player, @NotNull String group) {
        return permission.playerAddGroup(null, player, group);
    }

    public boolean playerAddGroup(@Nullable String world, @NotNull OfflinePlayer player, @NotNull String group) {
        return permission.playerAddGroup(world, player, group);
    }

    public boolean playerRemoveGroup(@NotNull OfflinePlayer player, @NotNull String group) {
        return permission.playerRemoveGroup(null, player, group);
    }

    public boolean playerRemoveGroup(@Nullable String world, @NotNull OfflinePlayer player, @NotNull String group) {
        return permission.playerRemoveGroup(world, player, group);
    }

    public boolean playerAdd(@NotNull OfflinePlayer player, @NotNull String permission) {
        return this.permission.playerAdd(null, player, permission);
    }

    public boolean playerAdd(@Nullable String world, @NotNull OfflinePlayer player, @NotNull String permission) {
        if (isSuperPerms()) {
            return playerHas(player, permission);
        }
        return this.permission.playerAdd(world, player, permission);
    }

    public boolean playerRemove(@NotNull OfflinePlayer player, @NotNull String permission) {
        return this.permission.playerRemove(null, player, permission);
    }

    public boolean playerRemove(@Nullable String world, @NotNull OfflinePlayer player, @NotNull String permission) {
        if (isSuperPerms()) {
            return playerRemove(player, permission);
        }
        return this.permission.playerRemove(world, player, permission);
    }

    public boolean playerHas(@NotNull OfflinePlayer player, @NotNull String permission) {
        return this.permission.playerHas(null, player, permission);
    }

    public boolean playerHas(@Nullable String world, @NotNull OfflinePlayer player, @NotNull String permission) {
        if (isSuperPerms()) {
            return playerHas(player, permission);
        }
        return this.permission.playerHas(world, player, permission);
    }

    public boolean has(@NotNull CommandSender sender, @NotNull String permission) {
        return this.permission.has(sender, permission);
    }

    public boolean isSuperPerms() {
        return "SuperPerms".equals(name);
    }
}