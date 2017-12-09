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
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

import com.github.yuttyann.scriptblockplus.utils.Utils;

public class BaseSBPlayer extends PlayerData implements SBPlayer {

	static final Map<UUID, BaseSBPlayer> players = new HashMap<UUID, BaseSBPlayer>(32);

	private final UUID uuid;
	private Player player;

	BaseSBPlayer(UUID uuid) {
		this.uuid = Objects.requireNonNull(uuid);
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
		Player player = getPlayer();
		return player == null ? null : player.getLocation();
	}

	@Override
	public Server getServer() {
		Player player = getPlayer();
		return player == null ? null : player.getServer();
	}

	public void setPlayer(Player player) {
		this.player = player != null && player.getUniqueId().equals(uuid) ? player : null;
	}

	@Override
	public Player getPlayer() {
		return player;
	}

	@Override
	public OfflinePlayer getOfflinePlayer() {
		OfflinePlayer player = getPlayer();
		if (player == null) {
			player = Utils.getOfflinePlayer(uuid);
		}
		return player;
	}

	@Override
	public boolean isOnline() {
		return getPlayer() != null;
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
		Player player = getPlayer();
		if (player != null) {
			player.sendMessage(message);
		}
	}

	@Override
	public void sendMessage(String[] messages) {
		Player player = getPlayer();
		if (player != null) {
			player.sendMessage(messages);
		}
	}

	@Override
	public PermissionAttachment addAttachment(Plugin plugin) {
		Player player = getPlayer();
		return player == null ? null : player.addAttachment(plugin);
	}

	@Override
	public PermissionAttachment addAttachment(Plugin plugin, int value) {
		Player player = getPlayer();
		return player == null ? null : player.addAttachment(plugin, value);
	}

	@Override
	public PermissionAttachment addAttachment(Plugin plugin, String permission, boolean value) {
		Player player = getPlayer();
		return player == null ? null : player.addAttachment(plugin, permission, value);
	}

	@Override
	public PermissionAttachment addAttachment(Plugin plugin, String permission, boolean value, int ticks) {
		Player player = getPlayer();
		return player == null ? null : player.addAttachment(plugin, permission, value, ticks);
	}

	@Override
	public Set<PermissionAttachmentInfo> getEffectivePermissions() {
		Player player = getPlayer();
		return player == null ? null : player.getEffectivePermissions();
	}

	@Override
	public boolean hasPermission(String permission) {
		Player player = getPlayer();
		return player == null ? false : player.hasPermission(permission);
	}

	@Override
	public boolean hasPermission(Permission permission) {
		Player player = getPlayer();
		return player == null ? false : player.hasPermission(permission);
	}

	@Override
	public boolean isPermissionSet(String permission) {
		Player player = getPlayer();
		return player == null ? false : player.isPermissionSet(permission);
	}

	@Override
	public boolean isPermissionSet(Permission permission) {
		Player player = getPlayer();
		return player == null ? false : player.isPermissionSet(permission);
	}

	@Override
	public void recalculatePermissions() {
		Player player = getPlayer();
		if (player != null) {
			player.recalculatePermissions();
		}
	}

	@Override
	public void removeAttachment(PermissionAttachment attachment) {
		Player player = getPlayer();
		if (player != null) {
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