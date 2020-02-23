package com.github.yuttyann.scriptblockplus.player;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

import com.github.yuttyann.scriptblockplus.utils.ItemUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class BaseSBPlayer extends PlayerData {

	private static final Map<UUID, SBPlayer> PLAYERS = new HashMap<>(64);

	private final UUID uuid;

	private Player player;
	private boolean isOnline;

	private BaseSBPlayer(@NotNull UUID uuid) {
		this.uuid = Objects.requireNonNull(uuid);
	}

	@NotNull
	static SBPlayer getSBPlayer(@NotNull UUID uuid) {
		SBPlayer sbPlayer = PLAYERS.get(uuid);
		if (sbPlayer == null) {
			PLAYERS.put(uuid, sbPlayer = new BaseSBPlayer(uuid));
		}
		return sbPlayer;
	}

	@NotNull
	public static Map<UUID, SBPlayer> getPlayers() {
		return PLAYERS;
	}

	@NotNull
	public BaseSBPlayer setPlayer(@Nullable Player player) {
		Objects.requireNonNull(player);
		synchronized(this) {
			this.player = player.getUniqueId().equals(uuid) ? player : null;
		}
		return this;
	}

	public void setOnline(boolean isOnline) {
		this.isOnline = isOnline;
	}

	@NotNull
	@Override
	public Server getServer() {
		return isOnline() ? player.getServer() : Bukkit.getServer();
	}

	@Nullable
	@Override
	public synchronized Player getPlayer() {
		return !isOnline() ? null : player == null ? player = Bukkit.getPlayer(uuid) : player;
	}

	@NotNull
	@Override
	public OfflinePlayer getOfflinePlayer() {
		return isOnline() ? player : Utils.getOfflinePlayer(uuid);
	}

	@Nullable
	@Override
	public PlayerInventory getInventory() {
		return isOnline() ? player.getInventory() : null;
	}

	@NotNull
	@Override
	public String getName() {
		return getOfflinePlayer().getName();
	}

	@NotNull
	@Override
	public UUID getUniqueId() {
		return uuid;
	}

	@Nullable
	@Override
	public World getWorld() {
		return isOnline() ? player.getLocation().getWorld() : null;
	}

	@Nullable
	@Override
	public Location getLocation() {
		return isOnline() ? player.getLocation() : null;
	}

	@Nullable
	@Override
	public ItemStack getItemInMainHand() {
		return isOnline() ? ItemUtils.getItemInMainHand(player) : null;
	}

	@Nullable
	@Override
	public ItemStack getItemInOffHand() {
		return isOnline() ? ItemUtils.getItemInOffHand(player) : null;
	}

	@Override
	public boolean isOnline() {
		return player != null && isOnline;
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
		if (isOnline()) player.sendMessage(message);
	}

	@Override
	public void sendMessage(@NotNull String[] messages) {
		if (isOnline()) player.sendMessage(messages);
	}

	@Nullable
	@Override
	public PermissionAttachment addAttachment(@NotNull Plugin plugin) {
		return isOnline() ? player.addAttachment(plugin) : null;
	}

	@Nullable
	@Override
	public PermissionAttachment addAttachment(@NotNull Plugin plugin, int value) {
		return isOnline() ? player.addAttachment(plugin, value) : null;
	}

	@Nullable
	@Override
	public PermissionAttachment addAttachment(@NotNull Plugin plugin, @NotNull String permission, boolean value) {
		return isOnline() ? player.addAttachment(plugin, permission, value) : null;
	}

	@Nullable
	@Override
	public PermissionAttachment addAttachment(@NotNull Plugin plugin, @NotNull String permission, boolean value, int ticks) {
		return isOnline() ? player.addAttachment(plugin, permission, value, ticks) : null;
	}

	@Nullable
	@Override
	public Set<PermissionAttachmentInfo> getEffectivePermissions() {
		return isOnline() ? player.getEffectivePermissions() : null;
	}

	@Override
	public boolean hasPermission(@NotNull String permission) {
		return isOnline() ? player.hasPermission(permission) : false;
	}

	@Override
	public boolean hasPermission(@NotNull Permission permission) {
		return isOnline() ? player.hasPermission(permission) : false;
	}

	@Override
	public boolean isPermissionSet(@NotNull String permission) {
		return isOnline() ? player.isPermissionSet(permission) : false;
	}

	@Override
	public boolean isPermissionSet(@NotNull Permission permission) {
		return isOnline() ? player.isPermissionSet(permission) : false;
	}

	@Override
	public void recalculatePermissions() {
		if (isOnline()) player.recalculatePermissions();
	}

	@Override
	public void removeAttachment(@NotNull PermissionAttachment attachment) {
		if (isOnline()) player.removeAttachment(attachment);
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
		return "SBPlayer{uuid=" + uuid + ", name=" + getName() + "}";
	}
}