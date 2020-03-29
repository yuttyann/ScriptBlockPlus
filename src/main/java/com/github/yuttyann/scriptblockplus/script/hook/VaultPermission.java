package com.github.yuttyann.scriptblockplus.script.hook;

import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicesManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class VaultPermission {

	private String name;
	private Permission permission;

	private VaultPermission(@Nullable Permission permission) {
		this.permission = permission;
		this.name = permission == null ? "None" : permission.getName();
	}

	@NotNull
	static VaultPermission setupPermission() {
		ServicesManager services = Bukkit.getServicesManager();
		RegisteredServiceProvider<Permission> provider = services.getRegistration(Permission.class);
		if (provider != null) {
			VaultPermission vault = new VaultPermission(provider.getProvider());
			if (vault.isEnabled()) {
				return vault;
			}
		}
		return new VaultPermission(null);
	}

	public boolean isEnabled() {
		return permission != null && permission.isEnabled();
	}

	@Nullable
	public String getPrimaryGroup(@NotNull Player player) {
		return permission.getPrimaryGroup(null, player);
	}

	@Nullable
	public String getPrimaryGroup(@Nullable String world, @NotNull Player player) {
		return permission.getPrimaryGroup(world, player);
	}

	public boolean playerInGroup(@Nullable Player player, @NotNull String group) {
		return permission.playerInGroup(null, player, group);
	}

	public boolean playerInGroup(@Nullable String world, @NotNull Player player, @NotNull String group) {
		return permission.playerInGroup(world, player, group);
	}

	public boolean playerAddGroup(@NotNull Player player, @NotNull String group) {
		return permission.playerAddGroup(null, player, group);
	}

	public boolean playerAddGroup(@Nullable String world, @NotNull Player player, @NotNull String group) {
		return permission.playerAddGroup(world, player, group);
	}

	public boolean playerRemoveGroup(@NotNull Player player, @NotNull String group) {
		return permission.playerRemoveGroup(null, player, group);
	}

	public boolean playerRemoveGroup(@Nullable String world, @NotNull Player player, @NotNull String group) {
		return permission.playerRemoveGroup(world, player, group);
	}

	public boolean playerAdd(@NotNull Player player, @NotNull String permission) {
		return this.permission.playerAdd(null, player, permission);
	}

	public boolean playerAdd(@Nullable String world, @NotNull Player player, @NotNull String permission) {
		return this.permission.playerAdd(world, player, permission);
	}

	public boolean playerRemove(@NotNull Player player, @NotNull String permission) {
		return this.permission.playerRemove(null, player, permission);
	}

	public boolean playerRemove(@Nullable String world, @NotNull Player player, @NotNull String permission) {
		return this.permission.playerRemove(world, player, permission);
	}

	public boolean playerHas(@NotNull Player player, @NotNull String permission) {
		return this.permission.playerHas(null, player, permission);
	}

	public boolean playerHas(@Nullable String world, @NotNull Player player, @NotNull String permission) {
		return this.permission.playerHas(world, player, permission);
	}

	public boolean has(@NotNull CommandSender sender, @NotNull String permission) {
		return this.permission.has(sender, permission);
	}

	public boolean has(@NotNull Player player, @NotNull String permission) {
		return this.permission.has(player, permission);
	}

	public boolean isSuperPerms() {
		return "SuperPerms".equals(name);
	}
}