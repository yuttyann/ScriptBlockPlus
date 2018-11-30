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

public final class BaseSBPlayer extends PlayerData {

	private static final Map<UUID, SBPlayer> PLAYERS = new HashMap<>(64);

	private final UUID uuid;
	private Player player;

	private BaseSBPlayer(UUID uuid) {
		this.uuid = Objects.requireNonNull(uuid);
	}

	static SBPlayer getSBPlayer(UUID uuid) {
		SBPlayer sbPlayer = PLAYERS.get(uuid);
		if (sbPlayer == null) {
			PLAYERS.put(uuid, sbPlayer = new BaseSBPlayer(uuid));
		}
		return sbPlayer;
	}

	public static void clear() {
		PLAYERS.clear();
	}

	public void setPlayer(Player player) {
		this.player = player != null && player.getUniqueId().equals(uuid) ? player : null;
	}

	@Override
	public Server getServer() {
		return isOnline() ? player.getServer() : null;
	}

	@Override
	public Player getPlayer() {
		return player == null ? player = Bukkit.getPlayer(uuid) : player;
	}

	@Override
	public OfflinePlayer getOfflinePlayer() {
		return isOnline() ? player : Utils.getOfflinePlayer(uuid);
	}

	@Override
	public PlayerInventory getInventory() {
		return isOnline() ? player.getInventory() : null;
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
	public World getWorld() {
		return isOnline() ? player.getLocation().getWorld() : null;
	}

	@Override
	public Location getLocation() {
		return isOnline() ? player.getLocation() : null;
	}

	@Override
	public ItemStack getItemInMainHand() {
		return isOnline() ? ItemUtils.getItemInMainHand(player) : null;
	}

	@Override
	public ItemStack getItemInOffHand() {
		return isOnline() ? ItemUtils.getItemInOffHand(player) : null;
	}

	@Override
	public boolean isOnline() {
		return player.isOnline();
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
		return isOnline() ? player.addAttachment(plugin) : null;
	}

	@Override
	public PermissionAttachment addAttachment(Plugin plugin, int value) {
		return isOnline() ? player.addAttachment(plugin, value) : null;
	}

	@Override
	public PermissionAttachment addAttachment(Plugin plugin, String permission, boolean value) {
		return isOnline() ? player.addAttachment(plugin, permission, value) : null;
	}

	@Override
	public PermissionAttachment addAttachment(Plugin plugin, String permission, boolean value, int ticks) {
		return isOnline() ? player.addAttachment(plugin, permission, value, ticks) : null;
	}

	@Override
	public Set<PermissionAttachmentInfo> getEffectivePermissions() {
		return isOnline() ? player.getEffectivePermissions() : null;
	}

	@Override
	public boolean hasPermission(String permission) {
		return isOnline() ? player.hasPermission(permission) : false;
	}

	@Override
	public boolean hasPermission(Permission permission) {
		return isOnline() ? player.hasPermission(permission) : false;
	}

	@Override
	public boolean isPermissionSet(String permission) {
		return isOnline() ? player.isPermissionSet(permission) : false;
	}

	@Override
	public boolean isPermissionSet(Permission permission) {
		return isOnline() ? player.isPermissionSet(permission) : false;
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
		return this == obj || obj instanceof BaseSBPlayer && uuid.equals(((BaseSBPlayer) obj).uuid);
	}

	@Override
	public String toString() {
		return "SBPlayer{uuid=" + uuid + ", name=" + getName() + "}";
	}
}