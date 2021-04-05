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

import net.milkbowl.vault.permission.Permission;

import com.github.yuttyann.scriptblockplus.hook.HookPlugin;

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

    /**
     * 権限プラグインを読み込みます。
     * @return {@link VaultPermission} - インスタンス
     */
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

    /**
     * {@code SuperPerms}が有効になっているのかどうか。
     * @return {@link boolean} - 有効な場合は{@code true}
     */
    public boolean isSuperPerms() {
        return "SuperPerms".equals(name);
    }

    /**
     * 権限プラグインが有効なのかどうか。
     * @return {@link boolean} - 有効な場合は{@code true}
     */
    public boolean isEnabled() {
        return permission != null && permission.isEnabled();
    }

    /**
     * 権限プラグインの名前を取得します。
     * @return {@link String} - 権限プラグインの名前
     */
    @NotNull
    public String getName() {
        return name;
    }

    /**
     * グループの名前を取得します。
     * @param player - プレイヤー
     * @return {@link String} - グループの名前
     */
    @Nullable
    public String getPrimaryGroup(@NotNull OfflinePlayer player) {
        return permission.getPrimaryGroup(null, player);
    }

    /**
     * グループの名前を取得します。
     * @param world - ワールド
     * @param player - プレイヤー
     * @return {@link String} - グループの名前
     */
    @Nullable
    public String getPrimaryGroup(@Nullable String world, @NotNull OfflinePlayer player) {
        return permission.getPrimaryGroup(world, player);
    }

    /**
     * プレイヤーが指定されたグループに所属しているのかどうか。
     * @param player - プレイヤー
     * @param group - グループ
     * @return {@link boolean} - 所属している場合は{@code true}
     */
    public boolean playerInGroup(@Nullable OfflinePlayer player, @NotNull String group) {
        return permission.playerInGroup(null, player, group);
    }

    /**
     * プレイヤーが指定されたグループに所属しているのかどうか。
     * @param world - ワールド
     * @param player - プレイヤー
     * @param group - グループ
     * @return {@link boolean} - 所属している場合は{@code true}
     */
    public boolean playerInGroup(@Nullable String world, @NotNull OfflinePlayer player, @NotNull String group) {
        return permission.playerInGroup(world, player, group);
    }

    /**
     * プレイヤーを指定されたグループに所属させます。
     * @param player - プレイヤー
     * @param group - グループ
     * @return {@link boolean} - 成功した場合は{@code true}
     */
    public boolean playerAddGroup(@NotNull OfflinePlayer player, @NotNull String group) {
        return permission.playerAddGroup(null, player, group);
    }

    /**
     * プレイヤーを指定されたグループに所属させます。
     * @param world - ワールド
     * @param player - プレイヤー
     * @param group - グループ
     * @return {@link boolean} - 成功した場合は{@code true}
     */
    public boolean playerAddGroup(@Nullable String world, @NotNull OfflinePlayer player, @NotNull String group) {
        return permission.playerAddGroup(world, player, group);
    }

    /**
     * プレイヤーを指定されたグループから脱退させます。
     * @param player - プレイヤー
     * @param group - グループ
     * @return {@link boolean} - 成功した場合は{@code true}
     */
    public boolean playerRemoveGroup(@NotNull OfflinePlayer player, @NotNull String group) {
        return permission.playerRemoveGroup(null, player, group);
    }

    /**
     * プレイヤーを指定されたグループから脱退させます。
     * @param world - ワールド
     * @param player - プレイヤー
     * @param group - グループ
     * @return {@link boolean} - 成功した場合は{@code true}
     */
    public boolean playerRemoveGroup(@Nullable String world, @NotNull OfflinePlayer player, @NotNull String group) {
        return permission.playerRemoveGroup(world, player, group);
    }

    /**
     * プレイヤーに指定された権限を追加します。
     * @param player - プレイヤー
     * @param permission - パーミッション
     * @return {@link boolean} - 成功した場合は{@code true}
     */
    public boolean playerAdd(@NotNull OfflinePlayer player, @NotNull String permission) {
        return this.permission.playerAdd(null, player, permission);
    }

    /**
     * プレイヤーに指定された権限を追加します。
     * @param world - ワールド
     * @param player - プレイヤー
     * @param permission - パーミッション
     * @return {@link boolean} - 成功した場合は{@code true}
     */
    public boolean playerAdd(@Nullable String world, @NotNull OfflinePlayer player, @NotNull String permission) {
        if (isSuperPerms()) {
            return playerHas(player, permission);
        }
        return this.permission.playerAdd(world, player, permission);
    }

    /**
     * プレイヤーから指定された権限を削除します。
     * @param player - プレイヤー
     * @param permission - パーミッション
     * @return {@link boolean} - 成功した場合は{@code true}
     */
    public boolean playerRemove(@NotNull OfflinePlayer player, @NotNull String permission) {
        return this.permission.playerRemove(null, player, permission);
    }

    /**
     * プレイヤーから指定された権限を削除します。
     * @param world - ワールド
     * @param player - プレイヤー
     * @param permission - パーミッション
     * @return {@link boolean} - 成功した場合は{@code true}
     */
    public boolean playerRemove(@Nullable String world, @NotNull OfflinePlayer player, @NotNull String permission) {
        if (isSuperPerms()) {
            return playerRemove(player, permission);
        }
        return this.permission.playerRemove(world, player, permission);
    }

    /**
     * プレイヤーが指定された権限を所持しているのかどうか。
     * @param player - プレイヤー
     * @param permission - パーミッション
     * @return {@link boolean} - 所持している場合は{@code true}
     */
    public boolean playerHas(@NotNull OfflinePlayer player, @NotNull String permission) {
        return this.permission.playerHas(null, player, permission);
    }

    /**
     * プレイヤーが指定された権限を所持しているのかどうか。
     * @param world - ワールド
     * @param player - プレイヤー
     * @param permission - パーミッション
     * @return {@link boolean} - 所持している場合は{@code true}
     */
    public boolean playerHas(@Nullable String world, @NotNull OfflinePlayer player, @NotNull String permission) {
        if (isSuperPerms()) {
            return playerHas(player, permission);
        }
        return this.permission.playerHas(world, player, permission);
    }

    /**
     * 指定された権限を所持しているのかどうか。
     * @param sender - 送信者
     * @param permission - パーミッション
     * @return {@link boolean} - 所持している場合は{@code true}
     */
    public boolean has(@NotNull CommandSender sender, @NotNull String permission) {
        return this.permission.has(sender, permission);
    }
}