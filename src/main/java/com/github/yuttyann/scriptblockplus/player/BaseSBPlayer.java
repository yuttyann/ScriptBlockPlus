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
    private boolean isOnline;

    BaseSBPlayer(@NotNull UUID uuid) {
        this.uuid = uuid;
    }

    @NotNull
    public static Map<UUID, BaseSBPlayer> getSBPlayers() {
        return PLAYERS;
    }

    public void setOnline(boolean isOnline) {
        synchronized(this) {
            this.player = (this.isOnline = isOnline) ? Bukkit.getPlayer(uuid) : null;
        }
    }

    @Override
    public boolean isOnline() {
        return isOnline && player != null;
    }

    @Override
    @NotNull
    public Server getServer() {
        return player.getServer();
    }

    @Override
    @NotNull
    public Player getPlayer() {
        var player = isOnline() ? this.player : Bukkit.getPlayer(uuid);
        return Objects.requireNonNull(player, "Player cannot be null");
    }

    @Override
    @NotNull
    public OfflinePlayer getOfflinePlayer() {
        return isOnline() ? player : Bukkit.getOfflinePlayer(uuid);
    }

    @Override
    @NotNull
    public PlayerInventory getInventory() {
        return player.getInventory();
    }

    @Override
    @NotNull
    public String getName() {
        return getOfflinePlayer().getName();
    }

    @Override
    @NotNull
    public UUID getUniqueId() {
        return uuid;
    }

    @Override
    @NotNull
    public World getWorld() {
        return player.getWorld();
    }

    @Override
    @NotNull
    public Location getLocation() {
        return player.getLocation();
    }

    @Override
    @NotNull
    public ItemStack getItemInMainHand() {
        return player.getInventory().getItemInMainHand();
    }

    @Override
    @NotNull
    public ItemStack getItemInOffHand() {
        return player.getInventory().getItemInOffHand();
    }

    @Override
    public boolean isOp() {
        return getOfflinePlayer().isOp();
    }

    @Override
    public void setOp(boolean value) {
        getOfflinePlayer().setOp(value);
    }

    @Override
    public void sendMessage(@NotNull String message) {
        player.sendMessage(message);
    }

    @Override
    public void sendMessage(@NotNull String[] messages) {
        player.sendMessage(messages);
    }

    @Override
    public void sendMessage(UUID uuid, String message) {
        player.sendMessage(uuid, message);
    }

    @Override
    public void sendMessage(UUID uuid, String[] messages) {
        player.sendMessage(uuid, messages);
    }

    @Override
    @NotNull
    public PermissionAttachment addAttachment(@NotNull Plugin plugin) {
        return player.addAttachment(plugin);
    }

    @Override
    @Nullable
    public PermissionAttachment addAttachment(@NotNull Plugin plugin, int value) {
        return player.addAttachment(plugin, value);
    }

    @Override
    @NotNull
    public PermissionAttachment addAttachment(@NotNull Plugin plugin, @NotNull String permission, boolean value) {
        return player.addAttachment(plugin, permission, value);
    }

    @Override
    @Nullable
    public PermissionAttachment addAttachment(@NotNull Plugin plugin, @NotNull String permission, boolean value, int ticks) {
        return player.addAttachment(plugin, permission, value, ticks);
    }

    @Override
    @NotNull
    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return player.getEffectivePermissions();
    }

    @Override
    public boolean hasPermission(@NotNull String permission) {
        return player.hasPermission(permission);
    }

    @Override
    public boolean hasPermission(@NotNull Permission permission) {
        return player.hasPermission(permission);
    }

    @Override
    public boolean isPermissionSet(@NotNull String permission) {
        return player.isPermissionSet(permission);
    }

    @Override
    public boolean isPermissionSet(@NotNull Permission permission) {
        return player.isPermissionSet(permission);
    }

    @Override
    public void recalculatePermissions() {
        player.recalculatePermissions();
    }

    @Override
    public void removeAttachment(@NotNull PermissionAttachment attachment) {
        player.removeAttachment(attachment);
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || obj instanceof BaseSBPlayer && uuid.equals(((BaseSBPlayer) obj).uuid);
    }

    @Override
    public String toString() {
        return "BaseSBPlayer{uuid=" + uuid + ", player=" + player + ", isOnline=" + isOnline + '}';
    }

    @Override
    public Spigot spigot() {
        return player.spigot();
    }
}