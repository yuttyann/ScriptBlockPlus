package com.github.yuttyann.scriptblockplus.script.hook;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicesManager;

public class VaultPermission {

	private Permission permission;

	private VaultPermission(Permission permission) {
		this.permission = permission;
	}

	protected static VaultPermission setupPermission() {
		ServicesManager services = Bukkit.getServicesManager();
		RegisteredServiceProvider<Permission> provider = services.getRegistration(Permission.class);
		if (provider != null) {
			VaultPermission vault = new VaultPermission(provider.getProvider());
			try {
				if (vault.permission.isEnabled()) {
					return vault;
				}
			} catch (Exception e) {
				return vault;
			}
		}
		return null;
	}

	public String getPrimaryGroup(Player player) {
		return permission.getPrimaryGroup(null, player);
	}

	public String getPrimaryGroup(String world, Player player) {
		return permission.getPrimaryGroup(world, player);
	}

	public boolean playerInGroup(Player player, String group) {
		return permission.playerInGroup(null, player, group);
	}

	public boolean playerInGroup(String world, Player player, String group) {
		return permission.playerInGroup(world, player, group);
	}

	public boolean playerAddGroup(Player player, String group) {
		return permission.playerAddGroup(null, player, group);
	}

	public boolean playerAddGroup(String world, Player player, String group) {
		return permission.playerAddGroup(world, player, group);
	}

	public boolean playerRemoveGroup(Player player, String group) {
		return permission.playerRemoveGroup(null, player, group);
	}

	public boolean playerRemoveGroup(String world, Player player, String group) {
		return permission.playerRemoveGroup(world, player, group);
	}

	public boolean playerAdd(Player player, String permission) {
		return this.permission.playerAdd(null, player, permission);
	}

	public boolean playerAdd(String world, Player player, String permission) {
		return this.permission.playerAdd(world, player, permission);
	}

	public boolean playerRemove(Player player, String permission) {
		return this.permission.playerRemove(null, player, permission);
	}

	public boolean playerRemove(String world, Player player, String permission) {
		return this.permission.playerRemove(world, player, permission);
	}

	public boolean playerHas(Player player, String permission) {
		return this.permission.playerHas(null, player, permission);
	}

	public boolean playerHas(String world, Player player, String permission) {
		return this.permission.playerHas(world, player, permission);
	}
}