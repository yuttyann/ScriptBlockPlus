package com.github.yuttyann.scriptblockplus.collplugin;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicesManager;

public class VaultPermission {

	private Permission permission;

	public static VaultPermission setupPermission() {
		if (!CollPlugins.hasVault()) {
			return null;
		}
		ServicesManager services = Bukkit.getServer().getServicesManager();
		RegisteredServiceProvider<Permission> provider = services.getRegistration(Permission.class);
		if (provider != null) {
			VaultPermission vault = new VaultPermission();
			vault.permission = provider.getProvider();
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
		return permission.getPrimaryGroup(player);
	}

	public boolean playerInGroup(Player player, String group) {
		return permission.playerInGroup(player, group);
	}

	public boolean playerAddGroup(Player player, String group) {
		return permission.playerAddGroup(player, group);
	}

	public boolean playerRemoveGroup(Player player, String group) {
		return permission.playerRemoveGroup(player, group);
	}

	public boolean playerAdd(Player player, String permission) {
		return this.permission.playerAdd(player, permission);
	}

	public boolean playerAdd(String world, Player player, String permission) {
		return this.permission.playerAdd(world, player, permission);
	}

	public boolean playerRemove(Player player, String permission) {
		return this.permission.playerRemove(player, permission);
	}

	public boolean playerRemove(String world, Player player, String permission) {
		return this.permission.playerRemove(world, player, permission);
	}

	public boolean has(Player player, String permission) {
		return this.permission.has(player, permission);
	}
}
