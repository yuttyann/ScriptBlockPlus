package com.github.yuttyann.scriptblockplus.hook.plugin;

import com.github.yuttyann.scriptblockplus.hook.HookPlugin;
import net.milkbowl.vault.permission.Permission;
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