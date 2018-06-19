package com.github.yuttyann.scriptblockplus.player;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

import com.github.yuttyann.scriptblockplus.utils.Utils;

public final class BaseSBPlayer extends PlayerData {

	private static final Map<UUID, SBPlayer> PLAYERS = new HashMap<>(32);

	private final UUID uuid;
	private Player player;

	private BaseSBPlayer(UUID uuid) {
		this.uuid = Objects.requireNonNull(uuid);
	}

	protected static SBPlayer getSBPlayer(UUID uuid) {
		SBPlayer sbPlayer = PLAYERS.get(uuid);
		if (sbPlayer == null) {
			sbPlayer = new BaseSBPlayer(uuid);
			PLAYERS.put(uuid, sbPlayer);
		}
		return sbPlayer;
	}

	public void setPlayer(Player player) {
		this.player = player != null && player.getUniqueId().equals(uuid) ? player : null;
	}

	@Override
	public Server getServer() {
		return player == null ? null : player.getServer();
	}

	@Override
	public Player getPlayer() {
		return player;
	}

	@Override
	public OfflinePlayer getOfflinePlayer() {
		return player == null ? Utils.getOfflinePlayer(uuid) : player;
	}

	@Override
	public PlayerInventory getInventory() {
		return player == null ? null : player.getInventory();
	}

	@Override
	public ItemStack getItemInMainHand() {
		return player == null ? null : Utils.getItemInMainHand(player);
	}

	@Override
	public ItemStack getItemInOffHand() {
		return player == null ? null : Utils.getItemInOffHand(player);
	}

	@Override
	public String getName() {
		return getOfflinePlayer().getName();
	}

	@Override
	public UUID getUniqueId() {
		return uuid;
	}

	@Override
	public Location getLocation() {
		return player == null ? null : player.getLocation();
	}

	@Override
	public boolean isOnline() {
		return player != null;
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
	public void sendMessage(String message) {
		if (isOnline()) {
			player.sendMessage(message);
		}
	}

	@Override
	public void sendMessage(String[] messages) {
		if (isOnline()) {
			player.sendMessage(messages);
		}
	}

	@Override
	public PermissionAttachment addAttachment(Plugin plugin) {
		return player == null ? null : player.addAttachment(plugin);
	}

	@Override
	public PermissionAttachment addAttachment(Plugin plugin, int value) {
		return player == null ? null : player.addAttachment(plugin, value);
	}

	@Override
	public PermissionAttachment addAttachment(Plugin plugin, String permission, boolean value) {
		return player == null ? null : player.addAttachment(plugin, permission, value);
	}

	@Override
	public PermissionAttachment addAttachment(Plugin plugin, String permission, boolean value, int ticks) {
		return player == null ? null : player.addAttachment(plugin, permission, value, ticks);
	}

	@Override
	public Set<PermissionAttachmentInfo> getEffectivePermissions() {
		return player == null ? null : player.getEffectivePermissions();
	}

	@Override
	public boolean hasPermission(String permission) {
		return player == null ? false : player.hasPermission(permission);
	}

	@Override
	public boolean hasPermission(Permission permission) {
		return player == null ? false : player.hasPermission(permission);
	}

	@Override
	public boolean isPermissionSet(String permission) {
		return player == null ? false : player.isPermissionSet(permission);
	}

	@Override
	public boolean isPermissionSet(Permission permission) {
		return player == null ? false : player.isPermissionSet(permission);
	}

	@Override
	public void recalculatePermissions() {
		if (isOnline()) {
			player.recalculatePermissions();
		}
	}

	@Override
	public void removeAttachment(PermissionAttachment attachment) {
		if (isOnline()) {
			player.removeAttachment(attachment);
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		return obj instanceof BaseSBPlayer && uuid.equals(((BaseSBPlayer) obj).uuid);
	}

	@Override
	public String toString() {
		return "SBPlayer{uuid=" + uuid + ", name=" + getName() + "}";
	}
}