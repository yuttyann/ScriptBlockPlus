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
package com.github.yuttyann.scriptblockplus.player;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * ScriptBlockPlus BaseSBPlayer クラス
 * <p>
 * {@link Player} のラッパークラス
 * @author yuttyann44581
 */
public final class BaseSBPlayer extends PlayerMap {

    private static final Map<UUID, BaseSBPlayer> PLAYERS = new HashMap<>(64);

    private final UUID uuid;

    private Player player;
    private boolean online;

    /**
     * コンストラクタ
     * @param uuid - プレイヤーの{@link UUID}
     */
    private BaseSBPlayer(@NotNull UUID uuid) {
        this.uuid = uuid;
    }

    /**
     * プレイヤーを取得します。
     * @param uuid - プレイヤーの{@link UUID}
     * @return {@link SBPlayer} - プレイヤー
     */
    @NotNull
    public static SBPlayer fromUUID(@NotNull UUID uuid) {
        var sbPlayer = PLAYERS.get(uuid);
        if (sbPlayer == null) {
            PLAYERS.put(uuid, sbPlayer = new BaseSBPlayer(uuid));
        }
        return sbPlayer;
    }

    /**
     * プレイヤーの一覧を取得します。
     * @return {@link Collection}&lt;{@link BaseSBPlayer}&gt; - プレイヤーの一覧
     */
    @NotNull
    public static Collection<BaseSBPlayer> getPlayers() {
        return PLAYERS.values();
    }

    @Override
    public synchronized void setOnline(boolean isOnline) {
        this.player = (this.online = isOnline) ? Bukkit.getPlayer(uuid) : null;
        if (player == null) {
            PLAYERS.remove(uuid);
        }
    }

    @Override
    public boolean isOnline() {
        return online && player != null;
    }

    @Override
    public boolean isSneaking() {
        return toPlayer().isSneaking();
    }

    @Override
    @NotNull
    public Server getServer() {
        return toPlayer().getServer();
    }

    @Override
    @NotNull
    public Player toPlayer() {
        var player = isOnline() ? this.player : Bukkit.getPlayer(uuid);
        return Objects.requireNonNull(player, "Player not found.");
    }

    @Override
    @NotNull
    public OfflinePlayer toOfflinePlayer() {
        return isOnline() ? toPlayer() : Objects.requireNonNull(Bukkit.getOfflinePlayer(uuid), "Player not found.");
    }

    @Override
    @NotNull
    public PlayerInventory getInventory() {
        return toPlayer().getInventory();
    }

    @Override
    @NotNull
    public String getName() {
        return toOfflinePlayer().getName();
    }

    @Override
    @NotNull
    public UUID getUniqueId() {
        return uuid;
    }

    @Override
    @NotNull
    public World getWorld() {
        return toPlayer().getWorld();
    }

    @Override
    @NotNull
    public Location getLocation() {
        return toPlayer().getLocation();
    }

    @Override
    @NotNull
    public ItemStack getItemInMainHand() {
        return toPlayer().getInventory().getItemInMainHand();
    }

    @Override
    @NotNull
    public ItemStack getItemInOffHand() {
        return toPlayer().getInventory().getItemInOffHand();
    }

    @Override
    public boolean isOp() {
        return toOfflinePlayer().isOp();
    }

    @Override
    public void setOp(boolean value) {
        toOfflinePlayer().setOp(value);
    }

    @Override
    public void sendMessage(@NotNull String message) {
        toPlayer().sendMessage(message);
    }

    @Override
    public void sendMessage(@NotNull String... messages) {
        toPlayer().sendMessage(messages);
    }

    @Override
    public void sendMessage(@NotNull UUID uuid, @NotNull String message) {
        toPlayer().sendMessage(uuid, message);
    }

    @Override
    public void sendMessage(@NotNull UUID uuid, @NotNull String... messages) {
        toPlayer().sendMessage(uuid, messages);   
    }

    @Override
    @NotNull
    public PermissionAttachment addAttachment(@NotNull Plugin plugin) {
        return toPlayer().addAttachment(plugin);
    }

    @Override
    @Nullable
    public PermissionAttachment addAttachment(@NotNull Plugin plugin, int value) {
        return toPlayer().addAttachment(plugin, value);
    }

    @Override
    @NotNull
    public PermissionAttachment addAttachment(@NotNull Plugin plugin, @NotNull String permission, boolean value) {
        return toPlayer().addAttachment(plugin, permission, value);
    }

    @Override
    @Nullable
    public PermissionAttachment addAttachment(@NotNull Plugin plugin, @NotNull String permission, boolean value, int ticks) {
        return toPlayer().addAttachment(plugin, permission, value, ticks);
    }

    @Override
    @NotNull
    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return toPlayer().getEffectivePermissions();
    }

    @Override
    public boolean hasPermission(@NotNull String permission) {
        return toPlayer().hasPermission(permission);
    }

    @Override
    public boolean hasPermission(@NotNull Permission permission) {
        return toPlayer().hasPermission(permission);
    }

    @Override
    public boolean isPermissionSet(@NotNull String permission) {
        return toPlayer().isPermissionSet(permission);
    }

    @Override
    public boolean isPermissionSet(@NotNull Permission permission) {
        return toPlayer().isPermissionSet(permission);
    }

    @Override
    public void recalculatePermissions() {
        toPlayer().recalculatePermissions();
    }

    @Override
    public void removeAttachment(@NotNull PermissionAttachment attachment) {
        toPlayer().removeAttachment(attachment);
    }

    @Override
    public Spigot spigot() {
        return toPlayer().spigot();
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return this == obj || obj instanceof BaseSBPlayer && uuid.equals(((BaseSBPlayer) obj).uuid);
    }

    @Override
    @NotNull
    public String toString() {
        return "BaseSBPlayer{uuid=" + uuid + ", player=" + toOfflinePlayer() + ", isOnline=" + online + '}';
    }
}